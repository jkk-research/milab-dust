
if ( !('DustBase' in window) ){
	function DustBaseInit () {
		
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
	
	DustBase = new DustBaseInit();
	
	console.log('DustBase 01 initialized.');
} else {
	console.error('DustBase init error. You should load DustBase.js only once!');
}
