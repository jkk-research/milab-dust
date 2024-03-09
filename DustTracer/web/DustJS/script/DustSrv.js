
if ( !('DustBase' in window) ){
	console.error('DustBase init error. You should load DustBase.js first!');
} else {
	function DustSrv () {
		var requestId = 0;
		
		function doSend(request) {
			request.beforeSend = function(jqXHR, settings ) {
				jqXHR.DustRequestId = ++requestId;
				jqXHR.DustMethod = request.method;
				jqXHR.DustURL = request.url;
				
				console.log('Request ' + jqXHR.DustRequestId + ' sending...');
			};
			
			$.ajax(request)  
			.done(function(data, textStatus, jqXHR) {
				console.log('Request ' + jqXHR.DustRequestId + ' done.');
			})
			.fail(function(jqXHR, textStatus, errorThrown ) {
				console.log('Request ' + jqXHR.DustRequestId + ' failed: ' + errorThrown);
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
	
	DustBase.Srv = new DustSrv();

	console.log('DustSrv 01 initialized.');
}