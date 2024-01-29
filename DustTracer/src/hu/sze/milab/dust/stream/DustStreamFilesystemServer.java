package hu.sze.milab.dust.stream;

import java.io.File;

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
		
		return ret;
	}

	@Override
	public MindHandle agentProcess() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;
		
		File fDir = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_VARIANT_VALUE);
		String fName = Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_PEEK, null, RESOURCE_ATT_URL_PATH);
		
		File f = new File(fDir, fName);
		Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_SET, f, MISC_ATT_VARIANT_VALUE);
		
		return ret;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;
		
		return ret;
	}

	@Override
	public MindHandle agentRelease() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}
}
