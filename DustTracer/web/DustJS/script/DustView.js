
if ( !('DustBase' in window) ){
	console.error('DustBase init error. You should load DustBase.js first!');
} else {
	function DustView () {
		var DomFragments = {};
	}
	
	DustBase.View = new DustView();

	console.log('DustView 01 initialized.');
}