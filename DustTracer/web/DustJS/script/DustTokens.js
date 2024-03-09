
if ( !('DustBase' in window) ){
	console.error('DustBase init error. You should load DustBase.js first!');
} else {
	DustBase.addModule('DustIdea', 
		{ type: 'DustIdeaType', id: 'Type'},
		{ type: 'DustIdeaType', id: 'Member'},
		{ type: 'DustIdeaType', id: 'Tag'},
		{ type: 'DustIdeaType', id: 'Constant'},
		
		{ type: 'DustIdeaTag', id: 'Val'},
		{ type: 'DustIdeaTag', id: 'Integer', info : {owner: 'Val'}},
		{ type: 'DustIdeaTag', id: 'Real', info : {owner: 'Val'}},
		{ type: 'DustIdeaTag', id: 'Ref', info : {owner: 'Val'}},

		{ type: 'DustIdeaTag', id: 'Coll'},
		{ type: 'DustIdeaTag', id: 'One', info : {owner: 'Coll'}},
		{ type: 'DustIdeaTag', id: 'Arr', info : {owner: 'Coll'}},
		{ type: 'DustIdeaTag', id: 'Set', info : {owner: 'Coll'}},
		{ type: 'DustIdeaTag', id: 'Map', info : {owner: 'Coll'}},
	);

	DustBase.addModule('DustModel', 
		{ type: 'DustIdeaType', id: 'Entity'},
		{ type: 'DustIdeaMember', id: 'Id', info : {owner: 'DustIdeaType__Entity'}},
		{ type: 'DustIdeaMember', id: 'PrimaryType', info : {owner: 'DustIdeaType__Entity'}},
		{ type: 'DustIdeaMember', id: 'Owner', info : {owner: 'DustIdeaType__Entity'}},
		{ type: 'DustIdeaMember', id: 'Tags', info : {owner: 'DustIdeaType__Entity'}},
	);

	DustBase.addModule('DustNative', 
		{ type: 'DustIdeaType', id: 'Cmd'},
		{ type: 'DustIdeaTag', id: 'String', info : {owner: 'DustIdea_Val'}},
		{ type: 'DustIdeaTag', id: 'Text', info : {owner: 'DustIdea_Val'}},
		{ type: 'DustIdeaTag', id: 'Date', info : {owner: 'DustIdea_Val'}},
		{ type: 'DustIdeaTag', id: 'Object', info : {owner: 'DustIdea_Val'}},
	);


	DustBase.addModule('DustDialog', 
		{ type: 'DustNativeCmd', id: 'Chk'},
		{ type: 'DustNativeCmd', id: 'Get'},
		{ type: 'DustNativeCmd', id: 'Set'},
		{ type: 'DustNativeCmd', id: 'Add'},
		{ type: 'DustNativeCmd', id: 'Del'},
	);


	DustBase.addModule('DustDecorate', 		
		{ type: 'DustIdeaTag', id: 'State'},
		{ type: 'DustIdeaTag', id: 'StateHidden', info : {owner: 'State'}},
		{ type: 'DustIdeaTag', id: 'StateDisabled', info : {owner: 'State'}},
		{ type: 'DustIdeaTag', id: 'StateActive', info : {owner: 'State'}},
		{ type: 'DustIdeaTag', id: 'StateHighlighted', info : {owner: 'State'}},
	);
	
	DustBase.addModule('DustEvent', 
		{ type: 'DustIdeaTag', id: 'Level'},
		{ type: 'DustIdeaTag', id: 'LevelCritical', info : {owner: 'Level'}},
		{ type: 'DustIdeaTag', id: 'LevelError', info : {owner: 'Level'}},
		{ type: 'DustIdeaTag', id: 'LevelWarning', info : {owner: 'Level'}},
		{ type: 'DustIdeaTag', id: 'LevelInfo', info : {owner: 'Level'}},
		{ type: 'DustIdeaTag', id: 'LevelOK', info : {owner: 'Level'}},
		{ type: 'DustIdeaTag', id: 'LevelTrace', info : {owner: 'Level'}},
		{ type: 'DustIdeaTag', id: 'LevelDebug', info : {owner: 'Level'}},
	);
	
	DustBase.addModule('DustLogic', 
		{ type: 'DustNativeCmd', id: 'Init'},
		{ type: 'DustNativeCmd', id: 'Begin'},
		{ type: 'DustNativeCmd', id: 'Process'},
		{ type: 'DustNativeCmd', id: 'End'},
		{ type: 'DustNativeCmd', id: 'Release'},
	);
	
	DustBase.addModule('DustStore', 
		{ type: 'DustNativeCmd', id: 'IsChanged'},
		{ type: 'DustNativeCmd', id: 'Undo'},
		{ type: 'DustNativeCmd', id: 'Redo'},
		{ type: 'DustNativeCmd', id: 'Commit'},
		{ type: 'DustNativeCmd', id: 'Rollback'},
	);

	console.log('DustTokens 01 initialized.');
}

