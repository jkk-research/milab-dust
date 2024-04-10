
if ('Dust' in window) {

	function DustMontru() {
		this.agentProcess = function(action) {
			console.log("Action performed, " + action);

			return DustHandles.MIND_TAG_RESULT_ACCEPT;
		}

	}

	function handleAction(event) {
		var el = event.target;

		var h = $(el).data('dustItem');

		if (h === undefined) {
			var id = el.id;
			var h = Dust.find(null, function(val) {
				console.log("Compare ", id, " ", val);
				return id == val;
			}, DustHandles.TEXT_ATT_TOKEN);

			if (h) {
				h = Dust.lookup(h)[DustHandles.MISC_ATT_CONN_TARGET];
			}

			$(el).data('dustItem', h ? h : null);
		}

		if (h) {
			Dust.access(DustHandles.MIND_TAG_ACCESS_COMMIT, DustHandles.MIND_TAG_ACTION_PROCESS, h);
		}

		console.log("Action performed, " + el.id);
	}

	//	Dust.Montru = new DustMontru();

	function GuiNarrative() {
	}

	function modMontruNarrative() {
		var ret = DustHandles.MIND_TAG_RESULT_ACCEPT;

		var absNar = Dust.access(MindAccess.Peek, null, MindContext.Target, DustHandles.MIND_ATT_AGENT_NARRATIVE);

		switch (absNar) {
			case DustHandles.MONTRU_NAR_GUI:
				Dust.access(MindAccess.Set, GuiNarrative, MindContext.Target, DustHandles.DUST_ATT_NATIVE_INSTANCE);
				break;

			default:
				ret = DustHandles.MIND_TAG_RESULT_PASS;
				break;
		}

		return ret;
	}
	Dust.access(MindAccess.Set, modMontruNarrative, DustBoot.modMontru, DustHandles.DUST_ATT_NATIVE_INSTANCE);

	var btns = $("button");
	btns.on("click", handleAction);



	console.log('Dust.Montru initialized.');
}
