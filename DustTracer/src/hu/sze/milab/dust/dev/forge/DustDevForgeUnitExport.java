package hu.sze.milab.dust.dev.forge;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustVisitor;
import hu.sze.milab.dust.dev.DustDevIndexer;
import hu.sze.milab.dust.montru.DustMontruConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;

public class DustDevForgeUnitExport implements DustMontruConsts {

	static class UnitExporter {
		MindHandle hUnit = null;

		DustDevIndexer<String> idxAuthors = new DustDevIndexer<String>();
		DustDevIndexer<MindHandle> idxRefUnit = new DustDevIndexer<MindHandle>();
		DustDevIndexer<MindHandle> idxLocalItems = new DustDevIndexer<MindHandle>();

		ArrayList<String> lines = new ArrayList<>();
		
		Comparator<String> linecomp = new Comparator<String>() {
			
			@Override
			public int compare(String o1, String o2) {
				Integer i1 = Integer.parseInt(o1.split("\t")[0]);
				Integer i2 = Integer.parseInt(o2.split("\t")[0]);
				return DustUtils.safeCompare(i1, i2);
			}
		};

		String handleToId(MindHandle h) {
			String ret = DustUtils.getPostfix(h.getId(), ":");

			MindHandle hU = Dust.access(MindAccess.Peek, null, h, MIND_ATT_KNOWLEDGE_UNIT);

			if (null == hU) {
				return null;
			}

			if (hUnit != hU) {
				ret = idxRefUnit.getIndex(hU) + ":" + ret;
			}

			return ret;
		}

		DustVisitor vUnit = new DustVisitor(VisitFollowRef.Once) {
			@Override
			protected MindHandle agentBegin() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hItem = info.getItemHandle();
				boolean read = (hItem == hUnit)
						|| !(boolean) Dust.access(MindAccess.Check, MIND_ASP_UNIT, hItem, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

				if (read) {
					return MIND_TAG_RESULT_READACCEPT;
				} else {
					return MIND_TAG_RESULT_PASS;
				}
			}

			@Override
			protected MindHandle agentProcess() throws Exception {
				VisitInfo info = getInfo();

				MindHandle hItem = info.getItemHandle();
				String sItem = handleToId(hItem);
				MindHandle hAtt = info.getAttHandle();

				if (null == sItem) {
					Dust.log(EVENT_TAG_TYPE_WARNING, " --- Invalid item", hItem);
				} else if (sItem.contains(":")) {
					Dust.log(EVENT_TAG_TYPE_WARNING, " --- Alien item", hItem, "in unit", hUnit);
				} else if (null == hAtt) {
					Dust.log(EVENT_TAG_TYPE_TRACE, " --- No attribute in process", hItem);
				} else if (!DustUtilsAttCache.getAtt(MachineAtts.TransientAtt, hAtt, false)) {

					MindCollType ct = info.getCollType();
					String at = "";
					Object key = info.getKey();

					if (key == hAtt) {
						ct = MindCollType.One;
					}

					switch (ct) {
					case Arr:
						at = "a";
						key = null;
						break;
					case Map:
						at = "m";
						if (key instanceof MindHandle) {
							key = "h" + handleToId((MindHandle) key);
						} else if (key instanceof String) {
							key = "b" + key;
						}
						break;
					case One:
						at = "o";
						key = null;
						break;
					case Set:
						at = "s";
						key = null;
						break;
					}

					String att = at + handleToId(hAtt);

					Object val = info.getValue();
					String vt = "b";
					if (val instanceof MindHandle) {
						val = handleToId((MindHandle) val);
						vt = "h";
					} else if (val instanceof Double) {
						vt = "r";
					} else if (val instanceof Long) {
						vt = "i";
					}
					String vs = vt + val;

					StringBuilder sb = DustUtils.sbAppend(null, "\t", false, sItem, att, key, vs);

					lines.add(sb.toString());
				}
				return MIND_TAG_RESULT_READACCEPT;
			}
		};

		public void export(File root, String strDate, MindHandle hUnit) throws Exception {
			this.hUnit = hUnit;

			idxAuthors.reset();
			idxAuthors.getIndex("giskard.me");

			idxRefUnit.reset();
			idxLocalItems.reset();
			lines.clear();

			Dust.access(MindAccess.Visit, vUnit, hUnit, MIND_ATT_UNIT_HANDLES);

			String unitID = getShortUnitId(hUnit);

			try (PrintWriter writer = new PrintWriter(new File(root, unitID + ".dut"))) {
				String authorID = "giskard.me";
				String line = "DustUnitText	1.0	UTF8	" + authorID + "	" + unitID + "	1.0	1	" + strDate;

				writer.println(line);

				writer.println("\n!authors");
				for (String id : idxAuthors.keys()) {
					writer.println(id);
				}

				writer.println("\n!units");
				for (MindHandle h : idxRefUnit.keys()) {
					String ui = getShortUnitId(h);
					line = "0	" + authorID + "	" + ui + "	1.0	1	" + strDate;
					writer.println(line);
				}

				writer.println("\n!graph");
				lines.sort(linecomp);
				for (String l : lines) {
					writer.println(l);
				}
			}
		}

		private String getShortUnitId(MindHandle hUnit) {
			String unitID = hUnit.toString();
			return DustUtils.cutPostfix(unitID, "_").toLowerCase();
		}
	}

	public static synchronized void export(File root) throws Exception {

		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		Date date = new Date();

		String strDate = utcFormat.format(date);

		File r = new File(root, "giskard.me");
		r.mkdirs();

		Dust.log(EVENT_TAG_TYPE_TRACE, "export date", strDate, "root", root.getCanonicalPath());

		UnitExporter exporter = new UnitExporter();

		Dust.access(MindAccess.Visit, new DustVisitor() {
			@Override
			protected MindHandle agentProcess() throws Exception {
				MindHandle hUnit = getInfo().getValue();

				exporter.export(r, strDate, hUnit);

				return MIND_TAG_RESULT_READACCEPT;
			}
		}, APP_UNIT, DUST_ATT_MACHINE_UNITS);

	}
}
