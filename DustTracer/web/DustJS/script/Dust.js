
if (!('Dust' in window)) {
	MindAccess = {
		Check: DustHandles.MIND_TAG_ACCESS_CHECK,
		Peek: DustHandles.MIND_TAG_ACCESS_PEEK,
		Get: DustHandles.MIND_TAG_ACCESS_GET,
		Set: DustHandles.MIND_TAG_ACCESS_SET,
		Insert: DustHandles.MIND_TAG_ACCESS_INSERT,
		Delete: DustHandles.MIND_TAG_ACCESS_DELETE,
		Reset: DustHandles.MIND_TAG_ACCESS_RESET,
		Commit: DustHandles.MIND_TAG_ACCESS_COMMIT,
		Broadcast: DustHandles.MIND_TAG_ACCESS_BROADCAST,
		Lookup: DustHandles.MIND_TAG_ACCESS_LOOKUP
	};

	MindAction = {
		Init: DustHandles.MIND_TAG_ACTION_INIT,
		Begin: DustHandles.MIND_TAG_ACTION_BEGIN,
		Process: DustHandles.MIND_TAG_ACTION_PROCESS,
		End: DustHandles.MIND_TAG_ACTION_END,
		Release: DustHandles.MIND_TAG_ACTION_RELEASE,
	};

	MindContext = {
		Action: DustHandles.MIND_TAG_CONTEXT_DIALOG,
		Dialog: DustHandles.MIND_TAG_CONTEXT_DIALOG,
		Self: DustHandles.MIND_TAG_CONTEXT_SELF,
		Target: DustHandles.MIND_TAG_CONTEXT_TARGET,
		Direct: DustHandles.MIND_TAG_CONTEXT_DIRECT,
	};

	var KnowledgeMap = {};
	var Context = {};
	var Relations = new Set();

	var Notifier = {
		first: true,
		seen: new Set(),
		queue: [],

		optCollect: function(target, action) {
			var ret = false;

			var listeners = Dust.lookup(target)[DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS];

			if (listeners) {
				if (this.first) {
					this.first = false;
					for (l of listeners) {
						this.queue.push({ agent: l, action: action, target: target });
					}
					ret = true;
				} else {
					var nq = this.queue;

					for (l of listeners) {
						if (!this.seen.has(l)) {
							var found = false;

							for (qi of nq) {
								if ((qi.agent == l) && (qi.target == target)) {
									found = true;
									break;
								}
							}

							if (!found) {
								this.queue.push({ agent: l, action: action, target: target });
							}
						}
					}
				}
			}

			return ret;
		},

		optSend: function() {
			var ret = false;

			if (this.queue.length) {
				var qi = this.queue.shift();
				var l = qi.agent;
				this.seen.add(l);

				try {
					var agent = Dust.lookup(l, true);
					var narrative = agent[DustHandles.DUST_ATT_IMPL_NARRATIVE];
					if (!narrative) {
						var machine = Dust.lookup(DustBoot.narMachine);
						var modules = machine[DustHandles.DUST_ATT_MACHINE_MODULES];

						for (mod of modules) {
							var mn = Dust.access(MindAccess.Peek, null, mod, DustHandles.DUST_ATT_IMPL_NARRATIVE);

							if (mn) {
								Context = { agent: null, action: null, target: l };

								if (DustHandles.MIND_TAG_RESULT_ACCEPT == mn()) {
									narrative = agent[DustHandles.DUST_ATT_IMPL_NARRATIVE];
									break;
								}
							}
						}
					}

					Context = qi;
					narrative();
				} finally {
					if (0 < this.queue.length) {
						ret = true;
					} else {
						this.seen.clear();
						this.first = true;
					}
				}
			}

			return ret;
		}
	};

	function loadJsonApiData(jsonApiObj, ids) {
		var respArr = jsonApiObj.data;

		for (item of respArr) {
			var kItem = Dust.lookup(item.id, true);
			ids.push(kItem.id);

			if (item.attributes) {
				for (const key in item.attributes) {
					var val = item.attributes[key];

					var kAtt = Dust.lookup(key, true);
					kItem[kAtt.id] = val;

					if (kAtt.id == DustHandles.DEV_ATT_HINT) {
						kItem.label = val;
					}
				}
			}

			if (item.relationships) {
				for (const key in item.relationships) {
					var rel = item.relationships[key];
					var val = null;

					if (Array.isArray(rel)) {
						var ob;
						for (target of rel) {
							if (!val) {
								if (target.meta) {
									val = {};
									ob = true;
								} else {
									val = [];
									ob = false;
								}
							}

							var v = target.id;
							v = Dust.lookup(v, true).id;
							
							if (ob) {
								var rk = target.meta.key;
								Dust.lookup(rk, true);
								val[rk] = v;
							} else {
								val.push(v);
							}
						}
					} else {
						val = rel.id.split(' ')[0];
						Dust.lookup(val, true);
					}

					var kRel = Dust.lookup(key, true).id;
					Relations.add(key);
					Relations.add(kRel);

					kItem[kRel] = val;
				}
			}
		}
	}

	function DustInit() {
		this.lookup = function(key, createIfMissing) {
			var id = key.split(' ')[0];
			var ret = KnowledgeMap[id];

			if (!ret && createIfMissing) {
				ret = { 'id': id, 'label': key.split(' ')[1] };

				if (!ret.label) {
					console.log('Missing label ' + key);
				}
				KnowledgeMap[id] = ret;
			}

			return ret;
		}

		this.access = function(cmd, val, root, ...path) {
			var value;

			switch (root) {
				case MindContext.Action:
					value = Context.action;
					break;
				case MindContext.Self:
					value = Context.agent;
					break;
				case MindContext.Target:
					value = Context.target;
					break;
				default:
					value = root;
					break;
			}

			var lastColl = null;
			var lastKey = null;

			if (value) {
				var createMissing = false;
				switch (cmd) {
					case MindAccess.Get:
					case MindAccess.Set:
					case MindAccess.Insert:
						createMissing = true;
						break;
				}

				var resolve = true;
				for (p of path) {
					var curr = null;

					if (value) {
						if (resolve) {
							curr = this.lookup(value, createMissing);
						}
					} else if (createMissing) {

					}
					lastKey = p;
					resolve = Relations.has(lastKey);
					lastColl = curr;
					value = curr = curr[lastKey];
				}
			}

			switch (cmd) {
				case MindAccess.Reset:
					if (Array.isArray(lastColl) && (0 < lastColl.length)) {
						lastColl = [];
					}
					break;
				case MindAccess.Peek:
					value = value ? value : val;
					break;
				case MindAccess.Set:
					if (value != val) {
						lastColl[lastKey] = val;
					}
					break;
				case MindAccess.Commit:
					if (Notifier.optCollect(value, val)) {
						while (Notifier.optSend());
					}
					break;
			}

			return value;
		}

		this.find = function(defVal, expr, ...path) {
			var ret = (undefined === defVal) ? [] : undefined;

			for (const key in KnowledgeMap) {
				var i = KnowledgeMap[key];
				var val = i[path[0]];
				if (val && expr(val)) {
					if (undefined === defVal) {
						ret.push(key);
					} else {
						return key;
					}
				}
			}

			return ret;
		}

		this.processResponseData = function(respData, ids) {
			loadJsonApiData(respData, ids);
		}

		this.loadApp = function(reqPath, mainModule, respProcArr) {
			Dust.access(MindAccess.Set, reqPath, DustBoot.dataSrvReq, DustHandles.RESOURCE_ATT_URL_PATH);
			Dust.access(MindAccess.Set, mainModule, DustBoot.dataSrvReq, DustHandles.TEXT_ATT_TOKEN);
			Dust.access(MindAccess.Set, respProcArr, DustBoot.dataSrvReq, DustHandles.DUST_ATT_IMPL_INSTANCE);

			Dust.access(MindAccess.Commit, MindAction.Process, DustBoot.dataSrvReq);

			//			var root = reqPath + mainModule;
			//			Dust.Comm.loadResource({ url: root, respProc: respProcArr });
		}

		this.isRelation = function(key) {
			return Relations.has(key);
		}
	}

	function MachineNarrative() {
		var respData = Dust.access(MindAccess.Peek, [], MindContext.Target, DustHandles.MISC_ATT_VARIANT_VALUE);
		
		var target = Dust.access(MindAccess.Peek, null, MindContext.Self, DustHandles.MISC_ATT_CONN_TARGET);
		
//		Dust.access(MindAccess.Reset, null, target, DustHandles.MISC_ATT_CONN_MEMBERARR);
		var ids = [];
		Dust.access(MindAccess.Set, ids, target, DustHandles.MISC_ATT_CONN_MEMBERARR);
		loadJsonApiData(respData, ids);
		
		Dust.access(MindAccess.Commit, MindAction.Process, target);
//		Dust.access(MindAccess.Set, ids, MindContext.Target, DustHandles.MISC_ATT_CONN_MEMBERARR);

		return DustHandles.MIND_TAG_RESULT_ACCEPT;
	}

	function modDustNarrative() {
		var ret = DustHandles.MIND_TAG_RESULT_ACCEPT;

		var absNar = Dust.access(MindAccess.Peek, null, MindContext.Target, DustHandles.MIND_ATT_AGENT_NARRATIVE);

		switch (absNar) {
			case DustHandles.DUST_NAR_MACHINE:
				Dust.access(MindAccess.Set, MachineNarrative, MindContext.Target, DustHandles.DUST_ATT_IMPL_NARRATIVE);
				break;

			default:
				ret = DustHandles.MIND_TAG_RESULT_PASS;
				break;
		}

		return ret;
	}

	Dust = new DustInit();

	Dust.access(MindAccess.Set, modDustNarrative, DustBoot.modDust, DustHandles.DUST_ATT_IMPL_NARRATIVE);


	//	Dust.access(MindAccess.Set, MachineNarrative, DustBoot.narMachine, DustHandles.DUST_ATT_NATIVE_INSTANCE);
	Dust.access(MindAccess.Set, DustBoot.narGui, DustBoot.narMachine, DustHandles.MISC_ATT_CONN_TARGET);
	
	Dust.access(MindAccess.Set, DustHandles.DUST_NAR_MACHINE, DustBoot.narMachine, DustHandles.MIND_ATT_AGENT_NARRATIVE);
	Dust.access(MindAccess.Set, [DustBoot.narMachine], DustBoot.dataBulkLoad, DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS);
	Dust.access(MindAccess.Set, [DustBoot.modDust, DustBoot.modComm], DustBoot.narMachine, DustHandles.DUST_ATT_MACHINE_MODULES);

	console.log('Dust initialized.');
}
