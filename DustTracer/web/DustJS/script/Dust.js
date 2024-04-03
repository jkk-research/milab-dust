
if (!('Dust' in window)) {
	var KnowledgeMap = {};

	var Context = null;
	var Notifier = null;

	var Relations = new Set();

	function optNotifyCollect(changed, command) {
		var ret = false;

		var listeners = Dust.lookup(changed)[DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS];

		if (listeners) {
			if (null == Notifier) {
				Notifier = { seen: new Set(), queue: [] };
				for (l of listeners) {
					Notifier.queue.push({ agent: l, cmd: command, chg: [changed] });
				}
				ret = true;
			} else {
				var nq = Notifier.queue;
				var ql = nq.length();

				for (l of listeners) {
					if (!Notifier.seen.has(l)) {
						for (let i = 0; i < ql; i++) {
							var qi = nq[i];
							if (qi.agent == l) {
								if (!qi.chg.includes(changed)) {
									qi.chg.push(changed);
									nq.splice(i, 1);
									nq.push(qi);
									--i;
									--ql;
									break;
								}
							}
						}
					}
					Notifier.queue.push({ agent: l, chg: [changed] });
				}
			}
		}

		return ret;
	}

	function optNotifySend() {
		var ret = false;

		if (Notifier && Notifier.queue.length) {
			var qi = Notifier.queue.shift();
			var l = qi.agent;
			Notifier.seen.add(l);

			try {
				var agent = Dust.lookup(l);
				var impl = agent[DustHandles.DUST_ATT_NATIVELOGIC_INSTANCE];
				if (!impl) {
					var logic = agent[DustHandles.MIND_ATT_AGENT_LOGIC];

					// create implementation from module
				}
				Context = qi;
				impl.agentProcess(qi.chg);
			} finally {
				if (0 < Notifier.queue.length) {
					ret = true;
				} else {
					Notifier = null;
				}
			}
		}

		return ret;
	}

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
			var item = root;

			// resolve path

			switch (cmd) {
				case DustHandles.MIND_TAG_ACCESS_PEEK:
					break;
				case DustHandles.MIND_TAG_ACCESS_COMMIT:
					if (optNotifyCollect(item, val)) {
						while (optNotifySend());
					}
					break;
			}
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

		this.loadApp = function(root, respProcArr) {
			Dust.Comm.loadResource({ url: root, respProc: respProcArr });
		}

		this.isRelation = function(key) {
			return Relations.has(key);
		}

		this.agentProcess = function(action) {
			switch (action) {
				case DustHandles.MIND_TAG_ACTION_PROCESS:
					break;
			}

			return DustHandles.MIND_TAG_RESULT_ACCEPT;
		}
	}

	Dust = new DustInit();

	console.log('Dust initialized.');
}
