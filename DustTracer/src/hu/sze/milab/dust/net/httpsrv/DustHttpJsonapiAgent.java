package hu.sze.milab.dust.net.httpsrv;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.stream.json.DustJsonConsts;
import hu.sze.milab.dust.stream.json.DustJsonUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustHttpJsonapiAgent extends DustAgent implements DustNetConsts, DustJsonConsts {

	@Override
	public MindHandle agentProcess() throws Exception {
		Dust.log(EVENT_TAG_TYPE_TRACE, "\n\n*** Hello, world! ***\n\n", MIND_TAG_ACTION_PROCESS);

		HttpServletResponse response = Dust.access(MindAccess.Peek, null, MindContext.Self, MISC_ATT_CONN_TARGET,
				NET_ATT_SRVCALL_RESPONSE);

		if (null != response) {

			String m = Dust.access(MindAccess.Get, null, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_METHOD);

			StringBuilder sb = new StringBuilder("<!doctype html>\n" + "<html lang=\"en\">\n"
					+ "<head>\n<meta charset=\"utf-8\">\n<title>DustTracer JSON:API</title>\n</head>\n" + "<body>");

			sb.append("<h2>JSONAPI</h2>");
			String pi = Dust.access(MindAccess.Get, null, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_PATHINFO);

			String[] path = pi.split("/");

			if (1 == path.length) {
				sb.append("<p>Method: " + m + "</p>");
				sb.append("<p>PathInfo: " + pi + "</p>");

				response.setContentType(MEDIATYPE_UTF8_HTML);
				PrintWriter out = response.getWriter();

				out.println(sb.toString());
			} else {
				String filter = DustJsonUtils.jsonKeyToId(path[1]);

				MindHandle checkAspect = (3 == filter.split(DUST_SEP_ID).length) ? Dust.lookup(filter) : null;

				ArrayList data = new ArrayList();

				Map<Object, MindHandle> units = Dust.access(MindAccess.Get, null, APP_UNIT, MIND_ATT_UNIT_HANDLES);

				for (MindHandle h : units.values()) {
					if ((null == checkAspect) && !h.getId().startsWith(filter)) {
						continue;
					}

					Map<Object, MindHandle> uh = Dust.access(MindAccess.Get, null, h, MIND_ATT_UNIT_HANDLES);
					if (null != uh) {
						for (MindHandle ih : uh.values()) {
							boolean match = (null == checkAspect)
									|| (boolean) Dust.access(MindAccess.Check, checkAspect, ih, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

							if (match) {
								Map im = DustJsonUtils.knowledgeToMap(h, ih, null);
								if (null != im) {
									data.add(im);
								}
							}
						}
						Dust.log(EVENT_TAG_TYPE_TRACE, uh);
					}
				}
				
				Map json = DustJsonUtils.toJsonApiMap(data);

				response.setCharacterEncoding(DUST_CHARSET_UTF8);
				response.setContentType(MEDIATYPE_JSONAPI);
				PrintWriter out = response.getWriter();
				JSONValue.writeJSONString(json, out);
				out.flush();
			}
		}

		return MIND_TAG_RESULT_ACCEPT;
	}
}
