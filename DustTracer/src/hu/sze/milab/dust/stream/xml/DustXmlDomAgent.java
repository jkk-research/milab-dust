package hu.sze.milab.dust.stream.xml;

import java.io.InputStream;

import org.w3c.dom.Document;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;

public class DustXmlDomAgent extends DustAgent implements DustXmlConsts {

	@Override
	protected MindHandle agentBegin() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;

	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		Object filePath = Dust.access(MindAccess.Peek, "?", MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM, RESOURCE_ATT_URL_PATH);
		Dust.log(EVENT_TAG_TYPE_TRACE, "Loading XML DOM", filePath);

		InputStream is = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);

		try {
			Document doc = DustXmlUtils.parse(is);
			
			Dust.access(MindAccess.Set, filePath, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA, RESOURCE_ATT_URL_PATH);
			Dust.access(MindAccess.Set, doc, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA, DUST_ATT_IMPL_DATA);
			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA);

		} catch (Throwable t) {
			DustException.swallow(t, "reading xml", Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, RESOURCE_ATT_URL_PATH));
			return MIND_TAG_RESULT_REJECT;
		}

		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	protected MindHandle agentEnd() throws Exception {

		return MIND_TAG_RESULT_ACCEPT;
	}

}
