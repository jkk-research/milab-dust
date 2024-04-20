package hu.sze.milab.dust.stream.xml;

import java.io.InputStream;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

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
		Dust.log(EVENT_TAG_TYPE_TRACE, "Loading XML DOM", Dust.access(MindAccess.Peek, "?", MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM, RESOURCE_ATT_URL_PATH));

		InputStream is = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);

		try {
			Document doc = DustXmlUtils.parse(is);

			Element eHtml = doc.getDocumentElement();

			NamedNodeMap nnm = eHtml.getAttributes();

			for (int idx = 0; idx < nnm.getLength(); ++idx) {
				Attr a = (Attr) nnm.item(idx);
				if ( a.getName().startsWith("xmlns:") ) {
					String aVal = a.getValue();
					Dust.log(EVENT_TAG_TYPE_TRACE, "  Namespace", a.getName(), aVal);
				}
			}
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
