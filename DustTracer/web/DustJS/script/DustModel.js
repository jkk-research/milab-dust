
if ( !('DustBase' in window) ){
	console.error('DustBase init error. You should load DustBase.js first!');
} else {
	function DustModel () {
		var Data = {};
		var Orig = {};
		var UndoIds = [];
		
		var Tokens = DustBase.getTokens('DustModel');
	
		this.selectEntity = function (... path) {
		    var entity = Data;
			var coll = null;
			var val = null;
		
			for (member of path) {
				if ( !entity && !coll ) {
					return null;
				}
				
		        val = coll ? coll[member] : entity[member];
				if ( !val ) {
					return null;
				}
				
				coll = ( (typeof val === 'object') || $.isArray(val)) ? val : null;
				entity = coll ? null : Data[val];
			}
		
			return val;
		}
	
		this.getValue = function (entity, key) {
			var e = Data[entity];
			return e ? e[key] : null;
		}
		
		this.setValue = function (entity, key, val) {
			var e = Data[entity];
			var ret = null;
			
			if ( e ) {
				ret = e[key];
				
				if ( val != ret ) {
					e[key] = val;
					
					$('[Dust-dataEntity=' + entity + ']').trigger('DustEventReload', { e: entity, k : key, v: val } );
			//		$('[Dust-dataEntity=' + entity + ']').('[Dust-dataKey=' + key + ']').trigger('DustEventReload', { e: entity, k : key, v: val } );
				}
			}
			return ret;
		}	
	}
	
	DustBase.Model = new DustModel();
	
	console.log('DustModel 01 initialized.');
}