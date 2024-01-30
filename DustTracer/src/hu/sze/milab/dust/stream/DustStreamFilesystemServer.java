package hu.sze.milab.dust.stream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustStreamFilesystemServer implements DustStreamConsts, DustConsts.MindServer {
	
	@Override
	public MindHandle agentInit() throws Exception {
		File fDataRoot = DustStreamUtils.getFile(MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_URL_PATH);
		DustUtilsFile.ensureDir(fDataRoot);
		
		Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_SET, fDataRoot, MISC_ATT_VARIANT_VALUE);

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	public MindHandle agentBegin() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;

		setStreamValue();

		return ret;
	}

	@Override
	public MindHandle agentProcess() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;
		
		setStreamValue();
		
		return ret;
	}

	public void setStreamValue() throws IOException {
		File fDir = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_VARIANT_VALUE);
		String fName = Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_PEEK, null, RESOURCE_ATT_URL_PATH);
		
		File f = new File(fDir, fName);
		Object val = f;
		
		Object type = Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_KNOWLEDGE_TAGS, RESOURCE_TAG_STREAMTYPE);
		if ( RESOURCE_TAG_STREAMTYPE_TEXT == type ) {
			Object dir = Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_PEEK, null, MIND_ATT_KNOWLEDGE_TAGS, MISC_TAG_DIRECTION);
			if ( MISC_TAG_DIRECTION_OUT == dir ) {
				val = new FileWriter(f);
			}
		}
		
		Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_SET, val, MISC_ATT_VARIANT_VALUE);
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;
		
		Object val = Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_VARIANT_VALUE);
		
		if ( val instanceof FileWriter ) {
			FileWriter fw = (FileWriter) val;
			fw.flush();
			fw.close();
		}
		
		Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_SET, null, MISC_ATT_VARIANT_VALUE);
		
		return ret;
	}

	@Override
	public MindHandle agentRelease() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}
}
