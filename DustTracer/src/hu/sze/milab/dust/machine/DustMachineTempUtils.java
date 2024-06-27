package hu.sze.milab.dust.machine;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustCoreHandles;
import hu.sze.milab.dust.DustUnitHandles;
import hu.sze.milab.dust.machine.DustMachineConsts.DustHandle;
import hu.sze.milab.dust.math.DustMathHandles;
import hu.sze.milab.dust.montru.DustMontruHandles;
import hu.sze.milab.dust.net.DustNetHandles;
import hu.sze.milab.dust.stream.DustStreamHandles;
import hu.sze.milab.dust.stream.json.DustJsonApiDomAgent;
import hu.sze.milab.dust.stream.json.DustJsonConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsFile;

@SuppressWarnings({ "rawtypes" })
public class DustMachineTempUtils implements DustJsonConsts {

	private static final MindHandle[] META_HANDLES = { MIND_ASP_UNIT, MIND_ASP_ASPECT, MIND_ASP_ATTRIBUTE, MIND_ASP_TAG, MIND_ASP_AGENT, MIND_ASP_NARRATIVE, DUST_ASP_MODULE, MIND_ASP_ASSEMBLY,
			DUST_NAR_MACHINE };

	private static final File MODULE_DIR = new File("work/json/");

	public static void test(Object... params) throws Exception {
		initFromInterfaces(DustUnitHandles.class, DustCoreHandles.class, DustNetHandles.class, DustMathHandles.class, DustStreamHandles.class, DustMontruHandles.class);

//		dumpUnits();

//		Dust.log(EVENT_ASP_EVENT, NET_NAR_HTTPSRV);
//		readUnits();

		writeJavaMeta("gen", "hu.sze.milab.dust.DustHandles");
		
		writeJavaScriptMeta("web/DustJS/script", "DustHandles");
	}

	public static void readUnits() throws Exception {
		File dir = new File(MODULE_DIR, "giskard");

		if ( dir.isDirectory() ) {
			for (File f : dir.listFiles()) {
				if ( f.getName().toLowerCase().endsWith(DUST_EXT_JSON) ) {
					DustJsonApiDomAgent.readUnit(f);
				}
			}
		}
	}

	public static void dumpUnits() throws Exception {

		Map<Object, MindHandle> units = Dust.access(MindAccess.Peek, null, APP_UNIT, MIND_ATT_UNIT_HANDLES);

		for (MindHandle h : units.values()) {
			if ( null != Dust.access(MindAccess.Peek, null, h, MIND_ATT_UNIT_HANDLES) ) {
				File f = getUnitFile(h);
				DustJsonApiDomAgent.writeUnit(h, f, null);
			}
		}
	}

	public static File getUnitFile(MindHandle unit) throws Exception {
		String[] ids = unit.getId().split(DUST_SEP_ID);

		File dir = new File(MODULE_DIR, ids[0]);
		DustUtilsFile.ensureDir(dir);

		File f = new File(dir, ids[1] + DUST_EXT_JSON);

		return f;
	}

	public static void writeJavaMeta(String srcRoot, String targetInterfaceName) throws Exception {
		genSources(new DustMachineTempMetaGenJava(srcRoot, targetInterfaceName, META_HANDLES));
	}

	public static void writeJavaScriptMeta(String srcRoot, String objName) throws Exception {
		genSources(new DustMachineTempMetaGenJavaScript(srcRoot, objName, META_HANDLES));
	}

	public static void genSources(DustMachineTempSrcGen metaWriter) throws Exception {
		metaWriter.agentBegin();

		Map<Object, Object> units = Dust.access(MindAccess.Peek, null, APP_UNIT, MIND_ATT_UNIT_CONTENT);
		for (Map.Entry<Object, Object> ue : units.entrySet()) {
			Map<String, MindHandle> hU = DustUtils.simpleGet(ue.getValue(), MIND_ATT_UNIT_HANDLES);
			if ( null != hU ) {
				Dust.log(null, "Unit found", ue.getKey(), hU.size());
				metaWriter.unitToAdd = hU;
				metaWriter.agentProcess(MindAction.Process);
			}
		}

		metaWriter.agentEnd();
	}

	public static void initFromInterfaces(Class... ifClasses) throws IllegalAccessException {
		Map<String, MindHandle> parents = new TreeMap<>();
		Map<String, MindHandle> units = new TreeMap<>();

//		Dust.access(MindAccess.Set, TEXT_TAG_LANGUAGE_EN_US, null, TEXT_ATT_LANGUAGE_DEFAULT);

		Set<String> authors = new TreeSet<>();

		for (Class constClass : ifClasses) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if ( ch instanceof DustHandle ) {
					String name = f.getName();
					DustHandle hItem = (DustHandle) ch;
					hItem.setHint(name);
					Dust.access(MindAccess.Set, name, ch, DEV_ATT_HINT);

					String[] nn = name.split(DUST_SEP);
					if ( (2 == nn.length) && "UNIT".equals(nn[1])) {
						units.put(nn[0], hItem);
					}
//					String tokenVal = (2 == nn.length) ? nn[0] : name.substring(nn[0].length() + nn[1].length() + 2);

					String aId = hItem.getId().split(DUST_SEP_ID)[0];
					if ( authors.add(aId) ) {
						MindHandle hA = Dust.lookup(aId);
						Dust.access(MindAccess.Set, aId, hA, DEV_ATT_HINT);
						Dust.access(MindAccess.Set, MIND_ASP_AUTHOR, hA, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					}

					if ( "NAR".equals(nn[1]) || "ASP".equals(nn[1]) || "TAG".equals(nn[1]) ) {
						parents.put(name, (MindHandle) ch);
					}

//					Dust.log(null, name, " -> ", tokenVal);
				}
			}
		}

		for (Class constClass : ifClasses) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if ( ch instanceof MindHandle ) {
					String name = f.getName();
					String[] nn = name.split(DUST_SEP);
					MindHandle hPA = DustUtilsAttCache.getAtt(MachineAtts.PrimaryAspectNames, nn[1], null);

					Dust.access(MindAccess.Set, hPA, ch, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					MindHandle hUnit = units.get(nn[0]);
//					if ( (null != hUnit ) && (ch != hUnit) ) {
					if ( null != hUnit ) {
						Dust.access(MindAccess.Set, hUnit, ch, MIND_ATT_KNOWLEDGE_UNIT);						
					}

					if ( "ATT".equals(nn[1]) || "TAG".equals(nn[1]) ) {
						String pName = name.substring(0, nn[0].length() + nn[1].length() + nn[2].length() + 2);
						pName = pName.replace("_ATT_", "_ASP_");
						MindHandle hParent = parents.get(pName);

						if ( (hParent == ch) && (MIND_ASP_TAG == hPA) ) {
							continue;
						}
						Dust.access(MindAccess.Set, hParent, ch, MISC_ATT_CONN_PARENT);
					}
				}
			}
		}
	}
}
