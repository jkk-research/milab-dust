package hu.sze.milab.dust.machine;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtilsFile;

public abstract class DustMachineTempSrcGen extends DustAgent implements DustMachineConsts {

	private String srcDir;
	private String path;
	private String fileName;

	private Set<MindHandle> paToWrite = new HashSet<>();

	public Map<String, MindHandle> unitToAdd;

	private Map<String, MindHandle> items = new TreeMap<>(DustDevUtils.ID_COMP);
	private PrintWriter fw;

	public DustMachineTempSrcGen(MindHandle... paToWrite) {
		for (MindHandle h : paToWrite) {
			this.paToWrite.add(h);
		}
	}

	protected void init(String srcDir, String path, String fileName) {
		this.srcDir = srcDir;
		this.path = path;
		this.fileName = fileName;
	}

	protected abstract String getSrcLead();

	protected abstract String formatItem(String name, String id);

	protected abstract String getSrcTail();

	@Override
	protected MindHandle agentBegin() throws Exception {
		if ( null == fw ) {
			File f = new File(srcDir, path);
			DustUtilsFile.ensureDir(f);

			fw = new PrintWriter(new File(f, fileName));

			fw.print(getSrcLead());
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		for (Map.Entry<String, MindHandle> ue : unitToAdd.entrySet()) {
			MindHandle he = ue.getValue();
			MindHandle pa = Dust.access(MindAccess.Peek, null, he, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);
			if ( paToWrite.contains(pa) ) {
				items.put(he.getId(), he);
			}
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		if ( null != fw ) {
			MindHandle lastTagParent = null;
			MindHandle lastPA = null;
			for (Map.Entry<String, MindHandle> ue : items.entrySet()) {
				String id = ue.getKey();
				MindHandle he = ue.getValue();
				MindHandle pa = Dust.access(MindAccess.Peek, null, he, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

				boolean split = MIND_ASP_UNIT == pa;
				if ( !split && (lastPA != pa) ) {
					split = true;

					if ( MIND_ASP_ATTRIBUTE == pa ) {
						split = !((MIND_ASP_ASPECT == lastPA) || (MIND_ASP_AGENT == lastPA));
					}
				}
				lastPA = pa;

				if ( MIND_ASP_TAG == pa ) {
					MindHandle tagParent = Dust.access(MindAccess.Peek, null, he, MISC_ATT_CONN_PARENT);
					if ( !split ) {
						split = !((tagParent == lastPA) || (tagParent == lastTagParent));
					}
					lastTagParent = (null == tagParent) ? he : tagParent;
				}

				String name = Dust.access(MindAccess.Peek, null, he, DEV_ATT_HINT);
				String line = formatItem(name, id);

				if ( split ) {
					fw.println();
				}
				fw.println(line);
			}

			fw.print(getSrcTail());
			fw.flush();
			fw.close();
			fw = null;
		}

		return MIND_TAG_RESULT_ACCEPT;
	}

}
