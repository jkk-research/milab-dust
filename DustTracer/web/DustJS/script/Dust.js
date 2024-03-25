
if ( !('Dust' in window) ){
	function DustInit () {
		
		var Tokens = {};
		var TokenInfo = {};
		var Paths = {};
		
		function getPath(root, rel) {
			var path = null;
			
			if ( root ) {
				path = new URL(root + rel).href;
				if (path.slice(-1) === '/') {
					path = path.slice(0, -1);
				}
			}
			
			return path;
		}

		var r = document.currentScript.src;
		
		if ( r ) {
			Paths.script = getPath(r, '/..');
			Paths.res = getPath(Paths.script, '/../res');
		} else {
			console.error('DustBase path init error.');
		}
		
		this.getTokens = function(module) {
			return Tokens[module];
		}
		
		this.addModule = function(module, ... tokens) {
			var mod = Tokens[module];
			
			if ( !mod ) {
				mod = {};
				Tokens[module] = mod;
				
				for (t of tokens) {
					var id = t.type + '_' + module + '_' + t.id;
					var entity = t.entity ? t.entity : id;
					mod[id] = entity;
					
					if ( t.info ) {
						TokenInfo[id] = t.info;
					}
				}
			} else {
				console.error('Multiple declaration of module ' + module);
			}	
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
					
					jqXHR.DustProc(jqXHR.DustRequestId, true, textStatus, data);
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
