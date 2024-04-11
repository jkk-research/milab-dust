
if ('Dust' in window) {

	function DustComm() {
		var requestId = 0;

		this.doSend = function(request) {
			request.beforeSend = function(jqXHR, settings) {
				jqXHR.DustRequestId = ++requestId;
				jqXHR.DustMethod = request.method;
				jqXHR.DustURL = request.url;
				jqXHR.DustProc = request.respProc;

				console.log('Request ' + jqXHR.DustRequestId + ' sending...');
			};

			$.ajax(request)
				.done(function(data, textStatus, jqXHR) {
					if (jqXHR.DustProc) {

						var ct = jqXHR.getResponseHeader('content-type');

						var ids = [];

						if (ct.includes('application/vnd.api+json')) {
							Dust.access(MindAccess.Set, data, DustBoot.dataBulkLoad, DustHandles.MISC_ATT_VARIANT_VALUE);
							Dust.access(MindAccess.Commit, MindAction.Process, DustBoot.dataBulkLoad);
							ids = Dust.access(MindAccess.Peek, [], DustBoot.dataBulkLoad, DustHandles.MISC_ATT_CONN_MEMBERARR);
							//							Dust.processResponseData(data, ids);
						}

						var listeners = Array.isArray(jqXHR.DustProc) ? jqXHR.DustProc : [jqXHR.DustProc];

						for (l of listeners) {
							l(jqXHR.DustRequestId, true, textStatus, data, ids);
						}
					} else {
						console.log('Request ' + jqXHR.DustRequestId + ' done.');
					}
				})
				.fail(function(jqXHR, textStatus, errorThrown) {
					if (jqXHR.DustProc) {
						jqXHR.DustProc(jqXHR.DustRequestId, false, textStatus, errorThrown);
					} else {
						console.log('Request ' + jqXHR.DustRequestId + ' failed: ' + errorThrown);
					}
				});
		}

		this.sendData = function(cmd, content, request) {
			if (!request) {
				request = {};
			}

			request.method = 'POST';
			request.url = '/cmd/send';

			var data = {
				cmd: cmd,
				data: content
			}

			request.data = JSON.stringify(data);

			this.doSend(request);
		};

		this.loadResource = function(request) {
			request.method = 'GET';
			this.doSend(request);
		}
	}

	var Comm = new DustComm();

	function CommNarrative() {
		var u = '/' + Dust.access(MindAccess.Peek, "", MindContext.Target, DustHandles.RESOURCE_ATT_URL_PATH) + '/' +
			Dust.access(MindAccess.Peek, "", MindContext.Target, DustHandles.TEXT_ATT_TOKEN);

		var l = Dust.access(MindAccess.Peek, [], MindContext.Target, DustHandles.DUST_ATT_IMPL_INSTANCE);

		var request = {
			method: 'GET',
			url: u,
			respProc: l
		};

		Comm.doSend(request);

		return DustHandles.MIND_TAG_RESULT_ACCEPT;
	}

	function modCommNarrative() {
		var ret = DustHandles.MIND_TAG_RESULT_ACCEPT;

		var absNar = Dust.access(MindAccess.Peek, null, MindContext.Target, DustHandles.MIND_ATT_AGENT_NARRATIVE);

		switch (absNar) {
			case DustHandles.NET_NAR_HTTPCLICOMM:
				Dust.access(MindAccess.Set, CommNarrative, MindContext.Target, DustHandles.DUST_ATT_IMPL_NARRATIVE);
				break;

			default:
				ret = DustHandles.MIND_TAG_RESULT_PASS;
				break;
		}

		return ret;
	}

	Dust.access(MindAccess.Set, modCommNarrative, DustBoot.modComm, DustHandles.DUST_ATT_IMPL_NARRATIVE);


	//	Dust.access(MindAccess.Set, CommNarrative, DustBoot.narComm, DustHandles.DUST_ATT_NATIVE_INSTANCE);
	Dust.access(MindAccess.Set, DustHandles.NET_NAR_HTTPCLICOMM, DustBoot.narComm, DustHandles.MIND_ATT_AGENT_NARRATIVE);

	Dust.access(MindAccess.Set, [DustBoot.narComm], DustBoot.dataSrvReq, DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS);

	Dust.Comm = Comm;

	console.log('Dust.Comm initialized.');
}
