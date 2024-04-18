package hu.sze.milab.dust.stream;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustStreamFilesystemServer extends DustAgent implements DustStreamConsts {
	
	@Override
	protected MindHandle agentInit() throws Exception {
		File fDataRoot = DustStreamUtils.getFile(MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_URL_PATH);
		DustUtilsFile.ensureDir(fDataRoot);
		
		Dust.access(MindAccess.Set, fDataRoot, MIND_TAG_CONTEXT_SELF, DUST_ATT_IMPL_DATA);

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentBegin() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;

		setStreamValue();

		return ret;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;
		
		setStreamValue();
		
		return ret;
	}

	public void setStreamValue() throws IOException {
		File fDir = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, DUST_ATT_IMPL_DATA);
		String fName = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, RESOURCE_ATT_URL_PATH);
		
		File f = new File(fDir, fName);
		Object val = f;
		
		Object type = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, MIND_ATT_KNOWLEDGE_TAGS, RESOURCE_TAG_STREAMTYPE);
		if ( RESOURCE_TAG_STREAMTYPE_TEXT == type ) {
			Object dir = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, MIND_ATT_KNOWLEDGE_TAGS, MISC_TAG_DIRECTION);
			if ( MISC_TAG_DIRECTION_OUT == dir ) {
				val = new FileWriter(f);
			} else if ( MISC_TAG_DIRECTION_IN == dir ) {
				val = new FileReader(f);
			} 
		}
		
		Dust.access(MindAccess.Set, val, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;
		
		Object val = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);
		
		if ( val instanceof Flushable ) {
			((Flushable) val).flush();
		}
		
		if ( val instanceof Closeable ) {
			((Closeable) val).close();
		}
		
		Dust.access(MindAccess.Set, null, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);
		
		return ret;
	}

	@Override
	protected MindHandle agentRelease() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}
}
