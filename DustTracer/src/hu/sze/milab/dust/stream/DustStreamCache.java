package hu.sze.milab.dust.stream;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustStreamCache extends DustAgent implements DustStreamConsts {

	@Override
	protected MindHandle agentProcess() throws Exception {
		
		MindHandle hCacheItem = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);

		String fileName = Dust.access(MindAccess.Peek, null, hCacheItem, TEXT_ATT_TOKEN);
		MindHandle hStream = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);
		
		if ( (boolean) Dust.access(MindAccess.Check, MISC_TAG_DBLHASH, MIND_TAG_CONTEXT_SELF, MIND_ATT_KNOWLEDGE_TAGS, MISC_TAG_DBLHASH)) {
			fileName = DustUtilsFile.addHash2(fileName);
		}
		
		Dust.access(MindAccess.Set, fileName, hStream, TEXT_ATT_TOKEN);
		
		Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hStream);
		
		if ( null == Dust.access(MindAccess.Peek, null, hStream, DUST_ATT_IMPL_DATA) ) {
			String url = Dust.access(MindAccess.Peek, null, hCacheItem, RESOURCE_ATT_URL_PATH);
			MindHandle hRequest = Dust.access(MindAccess.Peek, url, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_CACHE_REQUEST);
			Dust.access(MindAccess.Set, url, hRequest, RESOURCE_ATT_URL_PATH);
			Dust.access(MindAccess.Set, hStream, hRequest, MISC_ATT_CONN_TARGET);
			
//			DustDevUtils.setTag(hStream, MISC_TAG_DIRECTION_OUT, MISC_TAG_DIRECTION);
			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hRequest);
//			DustDevUtils.setTag(hStream, MISC_TAG_DIRECTION_IN, MISC_TAG_DIRECTION);
			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hStream);
		}

		return ( null == Dust.access(MindAccess.Peek, null, hStream, DUST_ATT_IMPL_DATA) ) ? MIND_TAG_RESULT_PASS : MIND_TAG_RESULT_READACCEPT;
	}

}
