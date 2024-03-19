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
import hu.sze.milab.dust.net.DustNetHandles;
import hu.sze.milab.dust.stream.DustStreamHandles;
import hu.sze.milab.dust.stream.json.DustJsonApiDomAgent;
import hu.sze.milab.dust.stream.json.DustJsonConsts;
import hu.sze.milab.dust.utils.DustUtils;
import hu.sze.milab.dust.utils.DustUtilsAttCache;
import hu.sze.milab.dust.utils.DustUtilsFile;

@SuppressWarnings({ "rawtypes" })
public class DustMachineTempUtils implements DustJsonConsts {

	private static final File MODULE_DIR = new File("work/json/");

	public static void test(Object... params) throws Exception {
		initFromInterfaces(DustUnitHandles.class, DustCoreHandles.class, DustNetHandles.class, DustStreamHandles.class);

//		dumpUnits();
		
//		Dust.log(EVENT_ASP_EVENT, NET_LOG_HTTPSRV);
//		readUnits();
		
		writeJavaMeta("giskard", "hu.sze.milab.dust.DustHandles");
	}

	public static void readUnits() throws Exception {
		File dir = new File(MODULE_DIR, "giskard");

		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				if (f.getName().toLowerCase().endsWith(DUST_EXT_JSON)) {
					DustJsonApiDomAgent.readUnit(f);
				}
			}
		}
	}

	public static void dumpUnits() throws Exception {

		Map<Object, MindHandle> units = Dust.access(MindAccess.Peek, null, APP_UNIT, MIND_ATT_UNIT_HANDLES);

		for (MindHandle h : units.values()) {
			if (null != Dust.access(MindAccess.Peek, null, h, MIND_ATT_UNIT_HANDLES)) {
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

	public static void writeJavaMeta(String authorID, String targetInterfaceName) throws Exception {
		DustMachineTempJavaMeta metaWriter = null;

		Map<Object, Object> units = Dust.access(MindAccess.Peek, null, APP_UNIT, MIND_ATT_UNIT_CONTENT);

//		Map units = Dust.access(MindAccess.Peek, null, APP_MACHINE_MAIN, DUST_ATT_MACHINE_AUTHORS, authorID, MIND_ATT_AUTHOR_UNITS);
//		Map units = Dust.access(MindAccess.Peek, null, null, DUST_ATT_MACHINE_UNITS);
		for (Map.Entry<Object, Object> ue : units.entrySet()) {
			if (null == metaWriter) {
				metaWriter = new DustMachineTempJavaMeta("gen", targetInterfaceName, MIND_ASP_UNIT, MIND_ASP_ASPECT,
						MIND_ASP_ATTRIBUTE, MIND_ASP_TAG, MIND_ASP_AGENT, MIND_ASP_LOGIC, DUST_ASP_MODULE, MIND_ASP_ASSEMBLY,
						DUST_ASP_MACHINE);
				metaWriter.agentBegin();
			}

			Map<String, MindHandle> hU = DustUtils.simpleGet(ue.getValue(), MIND_ATT_UNIT_HANDLES);
			if (null != hU) {
				Dust.log(null, "Unit found", ue.getKey(), hU.size());
				metaWriter.unitToAdd = hU;
				metaWriter.agentProcess(MindAction.Process);
			}
		}

		if (null != metaWriter) {
			metaWriter.agentEnd();
		}
	}

	public static void initFromInterfaces(Class... ifClasses) throws IllegalAccessException {
		Map<String, MindHandle> parents = new TreeMap<>();

//		Dust.access(MindAccess.Set, TEXT_TAG_LANGUAGE_EN_US, null, TEXT_ATT_LANGUAGE_DEFAULT);

		Set<String> authors = new TreeSet<>();
		
		for (Class constClass : ifClasses) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if (ch instanceof DustHandle) {
					String name = f.getName();
					DustHandle hItem = (DustHandle) ch;
					hItem.setHint(name);
					Dust.access(MindAccess.Set, name, ch, DEV_ATT_HINT);

					String[] nn = name.split(DUST_SEP);
					String tokenVal = (2 == nn.length) ? nn[0] : name.substring(nn[0].length() + nn[1].length() + 2);
					
					String aId = hItem.getId().split(DUST_SEP_ID)[0];
					if ( authors.add(aId) ) {
						MindHandle hA = Dust.lookup(aId);
						Dust.access(MindAccess.Set, aId, hA, DEV_ATT_HINT);
						Dust.access(MindAccess.Set, MIND_ASP_AUTHOR, hA, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
					}

					if ("ASP".equals(nn[1]) || "TAG".equals(nn[1])) {
						parents.put(name, (MindHandle) ch);
					}

//					Map token = DustMachineBoot.createKnowledge((DustHandle) L10N_UNIT, null, null);
//					token.put(TEXT_ATT_PLAIN_TEXT, tokenVal);
//					token.put(MIND_ATT_KNOWLEDGE_PRIMARYASPECT, TEXT_ASP_PLAIN);
//					token.put(TEXT_TAG_LANGUAGE, TEXT_TAG_LANGUAGE_EN_US);
//					token.put(MISC_ATT_CONN_OWNER, ch);

					Dust.log(null, name, " -> ", tokenVal);
				}
			}
		}

		for (Class constClass : ifClasses) {
			for (Field f : constClass.getDeclaredFields()) {
				Object ch = f.get(null);
				if (ch instanceof MindHandle) {
					String name = f.getName();
					String[] nn = name.split(DUST_SEP);
					MindHandle hPA = DustUtilsAttCache.getAtt(MachineAtts.PrimaryAspectNames, nn[1], null);

					Dust.access(MindAccess.Set, hPA, ch, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

					if ("ATT".equals(nn[1]) || "TAG".equals(nn[1])) {
						String pName = name.substring(0, nn[0].length() + nn[1].length() + nn[2].length() + 2);
						pName = pName.replace("_ATT_", "_ASP_");
						MindHandle hParent = parents.get(pName);

						if ( (hParent == ch) && (MIND_ASP_TAG == hPA ) ) {
							continue;
						}
						Dust.access(MindAccess.Set, hParent, ch, MISC_ATT_CONN_PARENT);
					}
				}
			}
		}
	}
}
