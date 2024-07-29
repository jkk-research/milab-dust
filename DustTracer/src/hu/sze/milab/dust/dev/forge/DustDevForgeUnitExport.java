package hu.sze.milab.dust.dev.forge;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
		Map<String, String> hints = new HashMap<>();
		
		Comparator<String> linecomp = new Comparator<String>() {
			
			@Override
			public int compare(String o1, String o2) {
				Integer i1 = Integer.parseInt(o1.split("\t")[0]);
				Integer i2 = Integer.parseInt(o2.split("\t")[0]);
				int d = DustUtils.safeCompare(i1, i2);
				
				return (0 == d) ? DustUtils.safeCompare(o1, o2) : d;
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
					
					Object val = info.getValue();
					
					if (DEV_ATT_HINT == hAtt ) {
						hints.put(sItem, (String) val);
						return MIND_TAG_RESULT_READACCEPT;
					}

					MindCollType ct = info.getCollType();
					String at = "";
					Object key = info.getKey();

					if (key == hAtt) {
						ct = MindCollType.One;
					}

					switch (ct) {
					case Arr:
						at = "a";
						key = String.format("%02d", (Integer) key);
						break;
					case Map:
						at = "m";
						key = prefixVal(key);
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

					String vs = prefixVal(val);

					StringBuilder sb = DustUtils.sbAppend(null, "\t", false, sItem, att, key, vs);

					lines.add(sb.toString());
				}
				return MIND_TAG_RESULT_READACCEPT;
			}
		};

		public String prefixVal(Object val) {
			String vt = "b";
			if (val instanceof MindHandle) {
				val = handleToId((MindHandle) val);
				vt = "h";
			} else if ((val instanceof Double) || (val instanceof Float)) {
				vt = "r";
			} else if ((val instanceof Long) || (val instanceof Integer)) {
				vt = "i";
			}
			
			return vt + val;
		};

		public void export(File root, String strDate, MindHandle hUnit) throws Exception {
			this.hUnit = hUnit;

			idxAuthors.reset();
			idxAuthors.getIndex("giskard.me");

			idxRefUnit.reset();
			idxLocalItems.reset();
			
			lines.clear();
			hints.clear();

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
					line = "0	" + ui + "	1.0	1	" + strDate;
					writer.println(line);
				}

				writer.println("\n!graph");
				lines.sort(linecomp);
				for (String l : lines) {
					writer.println(l);
				}
			}
			
			if ( !hints.isEmpty() ) {
				idxRefUnit.reset();
				idxRefUnit.getIndex(hUnit);
				
				hUnit = null;
				lines.clear();
				
				int txtId = 0;
				
				String aPrimAsp = "o" + handleToId(MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
				String aTags = "m" + handleToId(MIND_ATT_KNOWLEDGE_TAGS);
				String tText = "h" + handleToId(TEXT_ASP_PLAIN);
				String tLang = "h" + handleToId(TEXT_TAG_LANGUAGE);
				String tLangEn = "h" + handleToId(TEXT_TAG_LANGUAGE_EN_US);
				String aOwner = "o" + handleToId(MISC_ATT_CONN_OWNER);
				String aToken = "o" + handleToId(TEXT_ATT_TOKEN);
				
				ArrayList<String> ids = new ArrayList<>(hints.keySet());
				ids.sort(linecomp);
				
				for ( String sItem : ids ) {
					String txt = hints.get(sItem);
//					for ( Map.Entry<String, String> e : hints.entrySet() ) {
//					String sItem = handleToId(e.getKey());
//					String txt = e.getValue();
					
					StringBuilder sb = DustUtils.sbAppend(null, "\t", false, txtId, aPrimAsp, tText);
					lines.add(sb.toString());
					
					sb = DustUtils.sbAppend(null, "\t", false, txtId, aOwner, "h0:" + sItem);
					lines.add(sb.toString());
					
					sb = DustUtils.sbAppend(null, "\t", false, txtId, aTags, tLang, tLangEn);
					lines.add(sb.toString());
					
					sb = DustUtils.sbAppend(null, "\t", false, txtId, aToken, "b" + txt);
					lines.add(sb.toString());
					
					lines.add("");
					
					++txtId;
				}
				String txtID = unitID + "_txt_en";
				
				File rs = new File(root, "res");
				rs.mkdirs();
				
				try (PrintWriter writer = new PrintWriter(new File(rs, txtID + ".dut"))) {
					String authorID = "giskard.me";
					String line = "DustUnitText	1.0	UTF8	" + authorID + "	" + txtID + "	1.0	1	" + strDate;

					writer.println(line);

					writer.println("\n!authors");
					for (String id : idxAuthors.keys()) {
						writer.println(id);
					}

					writer.println("\n!units");
					for (MindHandle h : idxRefUnit.keys()) {
						String ui = getShortUnitId(h);
						line = "0	" + ui + "	1.0	1	" + strDate;
						writer.println(line);
					}

					writer.println("\n!graph");
					for (String l : lines) {
						writer.println(l);
					}
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
