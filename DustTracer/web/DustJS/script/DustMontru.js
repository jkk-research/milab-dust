
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

	function GuiNarrative() {
	}

	function ContainerNarrative() {
	}

	function AreaNarrative() {
		var txt = Dust.access(MindAccess.Peek, null, MindContext.Target, DustHandles.TEXT_ATT_PLAIN_TEXT);

		var $area = Dust.access(MindAccess.Peek, null, MindContext.Self, DustHandles.DUST_ATT_IMPL_INSTANCE);
		if (!$area) {
			var id = Dust.access(MindAccess.Peek, null, MindContext.Self, DustHandles.TEXT_ATT_TOKEN);

			$area = $('#' + id);
			Dust.access(MindAccess.Set, $area, MindContext.Self, DustHandles.DUST_ATT_IMPL_INSTANCE);
		}

		$area.text(txt);
	}

	function GridNarrative() {
		var grid = Dust.access(MindAccess.Peek, null, MindContext.Self, DustHandles.DUST_ATT_IMPL_INSTANCE);
		if (!grid) {
			var id = Dust.access(MindAccess.Peek, null, MindContext.Self, DustHandles.TEXT_ATT_TOKEN);
			$grid = $('#' + id);
			
			var rows = Dust.access(MindAccess.Peek, null, MindContext.Self, DustHandles.MONTRU_ATT_GRID_AXES, DustHandles.GEOMETRY_TAG_VALTYPE_CARTESIAN_Y, DustHandles.MISC_ATT_CONN_MEMBERARR);
			var cols = Dust.access(MindAccess.Peek, null, MindContext.Self, DustHandles.MONTRU_ATT_GRID_AXES, DustHandles.GEOMETRY_TAG_VALTYPE_CARTESIAN_X, DustHandles.MISC_ATT_CONN_MEMBERARR);

			var $table = $('<table/>');
			for (r of rows) {
				var content = '';
				for (c of cols) {
					var vv = Dust.access(MindAccess.Peek, ' - ', c, r);
					content = content.concat('<td> ' + vv + ' </td>');
				};
				$table.append('<tr><td>' + r + '</td>' + content + '</tr>');
			}
			$grid.append($table);

			Dust.access(MindAccess.Set, grid, MindContext.Self, DustHandles.DUST_ATT_IMPL_INSTANCE);
		}
	}

	function modMontruNarrative() {
		var ret = DustHandles.MIND_TAG_RESULT_ACCEPT;

		var absNar = Dust.access(MindAccess.Peek, null, MindContext.Target, DustHandles.MIND_ATT_AGENT_NARRATIVE);

		switch (absNar) {
			case DustHandles.MONTRU_NAR_GUI:
				Dust.access(MindAccess.Set, GuiNarrative, MindContext.Target, DustHandles.DUST_ATT_IMPL_NARRATIVE);
				break;
		
			case DustHandles.MONTRU_NAR_CONTAINER:
				Dust.access(MindAccess.Set, ContainerNarrative, MindContext.Target, DustHandles.DUST_ATT_IMPL_NARRATIVE);
				break;

			case DustHandles.MONTRU_NAR_AREA:
				Dust.access(MindAccess.Set, AreaNarrative, MindContext.Target, DustHandles.DUST_ATT_IMPL_NARRATIVE);
				break;

			case DustHandles.MONTRU_NAR_GRID:
				Dust.access(MindAccess.Set, GridNarrative, MindContext.Target, DustHandles.DUST_ATT_IMPL_NARRATIVE);
				break;

			default:
				ret = DustHandles.MIND_TAG_RESULT_PASS;
				break;
		}

		return ret;
	}
	Dust.access(MindAccess.Set, modMontruNarrative, DustBoot.modMontru, DustHandles.DUST_ATT_IMPL_NARRATIVE);

	var btns = $("button");
	btns.on("click", handleAction);



	console.log('Dust.Montru initialized.');
}
