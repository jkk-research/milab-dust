package hu.sze.milab.dust.net;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.utils.DustUtils;

public class DustNetDownloadAgent extends DustAgent implements DustNetConsts {

	@Override
	protected MindHandle agentProcess() throws Exception {
		MindHandle hRequest = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);

		String myUrl = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_URL_PATH);
		String url = DustUtils.isEmpty(myUrl) ? Dust.access(MindAccess.Peek, null, hRequest, RESOURCE_ATT_URL_PATH) : myUrl + "/" + Dust.access(MindAccess.Peek, null, hRequest, TEXT_ATT_TOKEN);

		Set<String> headers = new HashSet<>();

		Collection<String> h = Dust.access(MindAccess.Peek, Collections.EMPTY_SET, hRequest, NET_ATT_SRVCALL_HEADERS);
		headers.addAll(h);
		h = Dust.access(MindAccess.Peek, Collections.EMPTY_SET, MIND_TAG_CONTEXT_SELF, NET_ATT_SRVCALL_HEADERS);
		headers.addAll(h);
		
		String filePath = Dust.access(MindAccess.Peek, null, hRequest, MISC_ATT_CONN_SOURCE, RESOURCE_ATT_URL_PATH);

		MindHandle hStream = Dust.access(MindAccess.Peek, null, hRequest, MISC_ATT_CONN_TARGET);
		Dust.access(MindAccess.Set, filePath, hStream, RESOURCE_ATT_URL_PATH);
		Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hStream);

		OutputStream os = Dust.access(MindAccess.Peek, null, hStream, DUST_ATT_IMPL_DATA);
		
		DustNetUtils.download(url, os, headers);

		return MIND_TAG_RESULT_READACCEPT;
	}

}
