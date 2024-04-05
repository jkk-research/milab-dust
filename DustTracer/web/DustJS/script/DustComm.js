
if ('Dust' in window) {

	function DustComm() {
		var requestId = 0;

		function doSend(request) {
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
							Dust.access(MindAccess.Set, data, DustBoot.bulkLoad, DustHandles.MISC_ATT_VARIANT_VALUE);
							Dust.access(MindAccess.Commit, MindAction.Process, DustBoot.bulkLoad);
							ids = Dust.access(MindAccess.Peek, [], DustBoot.bulkLoad, DustHandles.MISC_ATT_CONN_MEMBERARR);
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

			doSend(request);
		};

		this.loadResource = function(request) {
			request.method = 'GET';
			doSend(request);
		}

		this.agentProcess = function(action) {
			switch (action) {
				case DustHandles.MIND_TAG_ACTION_PROCESS:
					var u = Dust.access(MindAccess.Peek, "", DustBoot.dataReq, DustHandles.RESOURCE_ATT_URL_PATH) +
						Dust.access(MindAccess.Peek, "", DustBoot.dataReq, DustHandles.TEXT_ATT_TOKEN);

					var l = Dust.access(MindAccess.Peek, [], DustBoot.dataReq, DustHandles.DUST_ATT_NATIVELOGIC_INSTANCE);

					var request = {
						method: 'GET',
						url: u,
						respProc: l
					};

					doSend(request);

					break;
			}

			return DustHandles.MIND_TAG_RESULT_ACCEPT;
		}
	}

	var c = new DustComm();

	Dust.access(MindAccess.Set, c, DustBoot.comm, DustHandles.DUST_ATT_NATIVELOGIC_INSTANCE);
	Dust.access(MindAccess.Set, [DustBoot.comm], DustBoot.dataReq, DustHandles.MIND_ATT_KNOWLEDGE_LISTENERS);

	Dust.Comm = c;

	console.log('Dust.Comm initialized.');
}
