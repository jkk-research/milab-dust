
if ( !('DustBase' in window) ){
	console.error('DustBase init error. You should load DustBase.js first!');
} else {
	function DustControl () {
		var Logics = {};
	}
	
	DustBase.Control = new DustControl();

	console.log('DustControl 01 initialized.');
}