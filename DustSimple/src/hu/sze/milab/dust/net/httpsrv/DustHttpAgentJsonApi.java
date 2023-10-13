package hu.sze.milab.dust.net.httpsrv;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.stream.json.DustStreamJsonConsts;

public class DustHttpAgentJsonApi implements DustStreamJsonConsts, DustNetConsts, DustConsts.MindAgent {

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		switch ( action ) {
		case Begin:
			break;
		case End:
			break;
		case Init:
			break;
		case Process:
			HttpServletResponse response = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, NET_ATT_SRVCALL_RESPONSE);

			if ( null != response ) {

				StringBuilder sb = new StringBuilder("<!doctype html>\n" + "<html lang=\"en\">\n" + "<head>\n<meta charset=\"utf-8\">\n<title>JSON:API</title>\n</head>\n" + "<body>");

				sb.append("<h2>JSON:API entry point</h2>");

				response.setContentType(MEDIATYPE_UTF8_HTML);
				PrintWriter out = response.getWriter();

				out.println(sb.toString());
			}
			break;
		case Release:
			break;
		}

		return MindStatus.Accept;
	}

}
