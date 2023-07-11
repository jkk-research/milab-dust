package hu.sze.milab.dust.stream.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.brain.DustBrain;

public class DustStreamJsonApiAgentSerializer implements DustStreamJsonConsts, DustConsts.MindAgent {

	@SuppressWarnings("rawtypes")
	class JsonApiReader implements ContentHandler {

		private JsonApiMember topMember;
		private MindHandle hTarget;
		private LinkedList<Object> path;
		private Boolean isTopArr;

		@Override
		public void startJSON() throws ParseException, IOException {
			topMember = null;
			path = new LinkedList<>();
		}

		@Override
		public void endJSON() throws ParseException, IOException {
		}

		@Override
		public boolean startObjectEntry(String name) throws ParseException, IOException {
			if ( null == topMember ) {
				topMember = JsonApiMember.valueOf(name);
				if ( !JsonApiMember.TOP.contains(topMember) ) {
					DustException.wrap(null, "Invalid top memner", topMember);
				}

				if ( null == hTarget ) {
					hTarget = Dust.createHandle();
				} else {
					Dust.access(hTarget, MindAccess.Reset, null);
				}

				isTopArr = null;
			} else {
				path.add(name);
			}
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			if ( path.isEmpty() ) {
				if ( true != isTopArr ) {
					topItemComplete();
				}
				topMember = null;
			} else {
				path.removeLast();
			}
			return true;
		}

		public void topItemComplete() {
			switch ( topMember ) {
			case jsonapi:
				// verify version
				break;
			case data:
			case included:
				// update target knowledge
				break;
			case errors:
				// handle errors
				break;
			case meta:
			case links:
				// do nothing for now
				break;
			default:
				DustException.wrap(null, "Invalid top member", topMember);
				break;
			}

			DustBrain.dumpHandle(topMember + ": ", hTarget);
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			if ( null != topMember ) {
				if ( null == isTopArr ) {
					isTopArr = false;
				} else {
					store(new HashMap());
				}
			}
			return true;
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			if ( null != topMember ) {
				if ( isTopArr && path.isEmpty() ) {
					topItemComplete();
				}
			}
			return true;
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			if ( null == isTopArr ) {
				isTopArr = true;
			} else {
				store(new ArrayList());
				path.add(KEY_ADD);
			}
			return true;
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			if ( !path.isEmpty() ) {
				path.removeLast();
			}
			return true;
		}

		@Override
		public boolean primitive(Object value) throws ParseException, IOException {
			store(value);
			return true;
		}

		void store(Object value) {
			Object[] p = path.toArray();
			int last = p.length - 1;

			if ( (0 <= last) ) {
				Integer i = (p[last] instanceof Integer) ? (Integer) p[last] : null;
				if ( null != i ) {
					p[last] = KEY_ADD;
				}

				Dust.access(hTarget, MindAccess.Set, value, p);

				if ( null != i ) {
					path.set(last, i + 1);
				}
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
			File f = Dust.access(MIND_ATT_AGENT_SELF, MindAccess.Peek, null, STREAM_ATT_STREAM_FILE);
			JsonApiReader eventRelay = new JsonApiReader();
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
