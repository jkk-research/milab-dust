
if (!('Dust' in window)) {
	var KnowledgeMap = {};

	var Context = null;
	var Notifier = null;

	var Relations = new Set();

	function optNotifyCollect(changed) {
		var ret = false;

		var listeners = KnowledgeMap[changed][DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS];

		if (listeners) {
			if (null == Notifier) {
				Notifier = { seen: new Set(), queue: [] };
				for (l of listeners) {
					Notifier.queue.push({ agent: l, chg: [changed] });
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

		if (Notifier && Notifier.queue.length()) {
			var qi = Notifier.shift();
			var l = qi.agent;
			Notifier.seen.add(l);

			var impl = KnowledgeMap[l][DUST_ATT_NATIVELOGIC_INSTANCE];
			if (!impl) {
				var agent = KnowledgeMap[l];
				var logic = KnowledgeMap[agent][MIND_ATT_AGENT_LOGIC];

				// create implementation from module
			}
			Context = qi;
			impl.agentProcess(qi.chg);

			ret = (0 < Notifier.queue.length());
		}

		return ret;
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
			switch ( cmd ) {
				case DustHandles.MIND_TAG_ACCESS_PEEK :
				break;
			}
		}

		this.find = function(defVal, expr, ... path) {
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
