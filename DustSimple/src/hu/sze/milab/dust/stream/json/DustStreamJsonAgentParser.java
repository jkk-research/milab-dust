package hu.sze.milab.dust.stream.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;

public class DustStreamJsonAgentParser implements DustStreamJsonConsts, DustConsts.MindAgent {

	class JsonEventRelay implements ContentHandler {

		private MindHandle hTarget;

		@Override
		public void startJSON() throws ParseException, IOException {
			hTarget = Dust.access(MindContext.Self, MindAccess.Get, null, MISC_ATT_CONN_TARGET);
			Dust.access(hTarget, MindAccess.Commit, MindAction.Init);
		}

		@Override
		public void endJSON() throws ParseException, IOException {
			Dust.access(hTarget, MindAccess.Commit, MindAction.Release);
			hTarget = null;
		}

		@Override
		public boolean startObjectEntry(String name) throws ParseException, IOException {
			Dust.access(hTarget, MindAccess.Set, name, TEXT_ATT_NAME);
			send(MindAction.Begin, null);
			Dust.access(hTarget, MindAccess.Set, null, TEXT_ATT_NAME);
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			send(MindAction.End, null);
			return true;
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			send(MindAction.Begin, MindColl.Map);
			return true;
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			send(MindAction.End, MindColl.Map);
			return true;
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			send(MindAction.Begin, MindColl.Arr);
			return true;
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			send(MindAction.End, MindColl.Arr);
			return true;
		}

		@Override
		public boolean primitive(Object value) throws ParseException, IOException {
			Dust.access(hTarget, MindAccess.Set, value, MISC_ATT_VARIANT_VALUE);
			Dust.access(hTarget, MindAccess.Commit, MindAction.Process);
			Dust.access(hTarget, MindAccess.Set, null, MISC_ATT_VARIANT_VALUE);

			return true;
		}

		void send(Object action, Object collType) {
			Dust.access(hTarget, MindAccess.Set, collType, MIND_ATT_KNOWLEDGE_TAGS);
			Dust.access(hTarget, MindAccess.Commit, action);
			if ( null != collType ) {
				Dust.access(hTarget, MindAccess.Set, null, MIND_ATT_KNOWLEDGE_TAGS);
			}
		}

	}

	JSONParser parser = null;

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		MindStatus ret = MindStatus.Accept;

		switch ( action ) {
		case Init:
			if ( null == parser ) {
				parser = new JSONParser();
			}
			break;
		case Begin:
			break;
		case Process:
			File f = Dust.access(MindContext.Self, MindAccess.Peek, null, STREAM_ATT_STREAM_FILE);
			JsonEventRelay eventRelay = new JsonEventRelay();
			parser.parse(new FileReader(f), eventRelay, true);
			break;
		case End:
			break;
		case Release:
			if ( null != parser ) {
				parser = null;
			}
			break;
		}

		return ret;
	}

}
