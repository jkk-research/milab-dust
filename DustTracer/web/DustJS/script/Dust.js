
if ( !('Dust' in window) ){
	var KnowledgeMap = {};

	function DustInit () {
		this.lookup = function(id) {
			return KnowledgeMap[id];
		}
	}
	
	function DustSrv () {
		var requestId = 0;
		
		function doSend(request) {
			request.beforeSend = function(jqXHR, settings ) {
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
					
					if ( ct.includes('application/vnd.api+json') ) {
						var respArr = data.data;
						
						for ( item of respArr ) {
							var itemId = item.id.split(' ')[0];
							ids.push(itemId);
							KnowledgeMap[itemId] = item;
						}
					}
					
					var listeners = Array.isArray(jqXHR.DustProc) ? jqXHR.DustProc : [jqXHR.DustProc];
					
					for ( l of listeners ) {
						l(jqXHR.DustRequestId, true, textStatus, data, ids);
					}
				} else {
					console.log('Request ' + jqXHR.DustRequestId + ' done.');
				}
			})
			.fail(function(jqXHR, textStatus, errorThrown ) {
				if (jqXHR.DustProc) {
					jqXHR.DustProc(jqXHR.DustRequestId, false, textStatus, errorThrown);
				} else {
					console.log('Request ' + jqXHR.DustRequestId + ' failed: ' + errorThrown);
				}
			});
		}
		
		this.sendData = function(cmd, content, request) {			
			if ( !request ) {
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
	}
		
	Dust = new DustInit();
	Dust.Srv = new DustSrv();
	
	console.log('Dust initialized.');
}
