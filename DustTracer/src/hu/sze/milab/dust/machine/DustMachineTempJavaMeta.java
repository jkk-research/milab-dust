package hu.sze.milab.dust.machine;

import java.io.File;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustMachineTempJavaMeta extends DustAgent implements DustMachineConsts {

	private static final String IF_FMT_BEGIN = "package {0};\n" + "\n" + "public interface {1} extends DustConsts '{'\n" + "";
	private static final String IF_FMT_LINE = "	public static MindHandle {0} = Dust.recall(\"{1}\");";
	private static final String IF_END = "\n}";

	String srcDir;
	String packageName;
	String interfaceName;

	Set<MindHandle> paToWrite = new HashSet<>();

	public MindHandle unitToAdd;

	private Map<String, MindHandle> items = new TreeMap<>(DustDevUtils.ID_COMP);
	private PrintWriter fw;

	public DustMachineTempJavaMeta(String srcDir, String packageName, String interfaceName, MindHandle... paToWrite) {
		this.srcDir = srcDir;
		this.packageName = packageName;
		this.interfaceName = interfaceName;
		for (MindHandle h : paToWrite) {
			this.paToWrite.add(h);
		}
	}

	@Override
	public MindHandle agentBegin() throws Exception {
		if ( null == fw ) {
			File f = new File(srcDir, packageName.replace(".", "/"));
			DustUtilsFile.ensureDir(f);

			fw = new PrintWriter(new File(f, interfaceName + DUST_EXT_JAVA));

			fw.print(MessageFormat.format(IF_FMT_BEGIN, packageName, interfaceName));
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	public MindHandle agentProcess() throws Exception {

		Map<String, MindHandle> ui = Dust.access(unitToAdd, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_UNIT_HANDLES);

		if ( null != ui ) {
			if ( paToWrite.contains(MIND_ASP_UNIT) ) {
				items.put(unitToAdd.getId(), unitToAdd);
			}
			for (Map.Entry<String, MindHandle> ue : ui.entrySet()) {
				MindHandle he = ue.getValue();
				MindHandle pa = Dust.access(he, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
				if ( paToWrite.contains(pa) ) {
					items.put(he.getId(), he);
				}
			}
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		if ( null != fw ) {
			MindHandle lastTagParent = null;
			MindHandle lastPA = null;
			for (Map.Entry<String, MindHandle> ue : items.entrySet()) {
				String id = ue.getKey();
				MindHandle he = ue.getValue();
				MindHandle pa = Dust.access(he, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

				if ( id.equals("1:26") ) {
					DustDevUtils.breakpoint();
				}

				boolean split = MIND_ASP_UNIT == pa;
				if ( !split && (lastPA != pa) ) {
					split = true;

					if ( MIND_ASP_ATTRIBUTE == pa ) {
						split = (MIND_ASP_ASPECT != lastPA);
					}

					lastPA = pa;
				}
				if ( MIND_ASP_TAG == pa ) {
					MindHandle tagParent = Dust.access(he, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_CONN_PARENT);
					if ( !split ) {
						split = !((tagParent == lastPA) || (tagParent == lastTagParent));
					}
					lastTagParent = (null == tagParent) ? he : tagParent;
				}

				String name = Dust.access(he, MIND_TAG_ACCESS_PEEK, null, DEV_ATT_HINT);
				String line = MessageFormat.format(IF_FMT_LINE, name, id);

				if ( split ) {
					fw.println();
				}
				fw.println(line);
			}

			fw.print(IF_END);
			fw.flush();
			fw.close();
			fw = null;
		}

		return MIND_TAG_RESULT_ACCEPT;
	}

}
