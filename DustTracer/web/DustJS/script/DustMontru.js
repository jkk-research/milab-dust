
if ('Dust' in window) {
	
	function DustMontru() {
		this.agentProcess = function(action) {
		console.log( "Action performed, " + action );

			return DustHandles.MIND_TAG_RESULT_ACCEPT;
		}

	}
	
	function handleAction(event) {
		var el = event.target;
		
		var h = $(el).data('dustItem');
		
		if ( h === undefined ) {
			var id = el.id;
			var h = Dust.find( null, function(val){
				console.log( "Compare ", id, " ", val );
				return id == val;
			}, DustHandles.TEXT_ATT_TOKEN );
			
			if ( h ) {
				h = Dust.lookup(h)[DustHandles.MISC_ATT_CONN_TARGET];
			}
			
			$(el).data('dustItem', h ? h : null);
		}
		
		if ( h ) {
			Dust.access(DustHandles.MIND_TAG_ACCESS_COMMIT, DustHandles.MIND_TAG_ACTION_PROCESS, h);
		}
		
		console.log( "Action performed, " + el.id );
	}
	
	Dust.Montru = new DustMontru();
	
	var btns = $( "button" );
	btns.on( "click", handleAction );

	console.log('Dust.Montru initialized.');
}
