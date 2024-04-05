
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

		optCollect: function(changed, command) {
			var ret = false;

			var listeners = Dust.lookup(changed)[DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS];

			if (listeners) {
				if (this.first) {
					this.first = false;
					for (l of listeners) {
						this.queue.push({ agent: l, cmd: command, chg: [changed] });
					}
					ret = true;
				} else {
					var nq = this.queue;
					var ql = nq.length;

					for (l of listeners) {
						if (!this.seen.has(l)) {
							var found = false;

							for (let i = 0; i < ql; i++) {
								var qi = nq[i];
								if (qi.agent == l) {
									if (!qi.chg.includes(changed)) {
										qi.chg.push(changed);
										nq.splice(i, 1);
										nq.push(qi);
										found = true;
										break;
									}
								}
							}

							if (!found) {
								this.queue.push({ agent: l, chg: [changed] });
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
					var agent = Dust.lookup(l);
					var impl = agent[DustHandles.DUST_ATT_NATIVELOGIC_INSTANCE];
					if (!impl) {
						var logic = agent[DustHandles.MIND_ATT_AGENT_LOGIC];

						// create implementation from module
					}
					Context = qi;
					impl.agentProcess(qi.cmd);
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
							Dust.lookup(v, true);
							if (ob) {
								var rk = target.meta.key;
								Dust.lookup(rk, true);
								val[rk] = v;
							} else {
								val.push(v);
							}
						}
					} else {
						val = rel.id;
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
				case MindContext.Self:
					value = Context.agent;
					break;
				case MindContext.Target:
					value = Context.chg;
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
			Dust.access(MindAccess.Set, reqPath, DustBoot.dataReq, DustHandles.RESOURCE_ATT_URL_PATH);
			Dust.access(MindAccess.Set, mainModule, DustBoot.dataReq, DustHandles.TEXT_ATT_TOKEN);
			Dust.access(MindAccess.Set, respProcArr, DustBoot.dataReq, DustHandles.DUST_ATT_NATIVELOGIC_INSTANCE);

			Dust.access(MindAccess.Commit, MindAction.Process, DustBoot.dataReq);

//			var root = reqPath + mainModule;
//			Dust.Comm.loadResource({ url: root, respProc: respProcArr });
		}

		this.isRelation = function(key) {
			return Relations.has(key);
		}
	}

	function MachineInit() {

		this.agentProcess = function(action) {
			switch (action) {
				case DustHandles.MIND_TAG_ACTION_PROCESS:
					var respData = Dust.access(MindAccess.Peek, [], DustBoot.bulkLoad, DustHandles.MISC_ATT_VARIANT_VALUE);
					var ids = [];
					loadJsonApiData(respData, ids);
					Dust.access(MindAccess.Set, ids, DustBoot.bulkLoad, DustHandles.MISC_ATT_CONN_MEMBERARR);
					break;
			}

			return DustHandles.MIND_TAG_RESULT_ACCEPT;
		}
	}

	Dust = new DustInit();

	var m = new MachineInit();

	Dust.access(MindAccess.Set, m, DustBoot.machine, DustHandles.DUST_ATT_NATIVELOGIC_INSTANCE);
	Dust.access(MindAccess.Set, [DustBoot.machine], DustBoot.bulkLoad, DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS);

	console.log('Dust initialized.');
}
