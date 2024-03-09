package hu.sze.milab.dust.net.httpsrv;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.stream.DustStreamConsts;

class DustHttpServletDispatcher extends HttpServlet implements DustNetConsts, DustStreamConsts {
	private static final long serialVersionUID = 1L;

	MindHandle hSelf;

	public DustHttpServletDispatcher(MindHandle hSelf) {
		super();
		this.hSelf = hSelf;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long ts = System.currentTimeMillis();
		Throwable exception = null;
		String pathInfo = request.getPathInfo();

		if (pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}

		try {
			Dust.access(MindAccess.Set, hSelf, null, MIND_ATT_DIALOG_ACTIVEAGENT);

			Collection<Object> agents = Dust.access(MindAccess.Get, Collections.EMPTY_LIST, MindContext.Self,
					MISC_ATT_CONN_MEMBERARR);

			synchronized (agents) { // quick and dirty

				Object target = null;
				for (Object agent : agents) {
					String p = Dust.access(MindAccess.Get, "@@@", agent, TEXT_ATT_TOKEN);

					if (pathInfo.startsWith(p)) {
						target = agent;
						break;
					}
				}

				if (null == target) {
					response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
					return;
				}

				Dust.access(MindAccess.Set, pathInfo, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_PATHINFO);

				Dust.access(MindAccess.Set, request, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_REQUEST);
				Dust.access(MindAccess.Set, response, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_RESPONSE);

				Dust.access(MindAccess.Set, request.getMethod(), MindContext.Self, MISC_ATT_CONN_TARGET,
						NET_ATT_SRVCALL_METHOD);

				Enumeration<String> ee;
				String n = null;

				for (ee = request.getAttributeNames(); ee.hasMoreElements();) {
					n = ee.nextElement();
					optAdd(NET_ATT_SRVCALL_ATTRIBUTES, n, request.getAttribute(n));
				}

				for (ee = request.getParameterNames(); ee.hasMoreElements();) {
					n = ee.nextElement();
					optAdd(NET_ATT_SRVCALL_PAYLOAD, n, request.getParameter(n));
				}

				for (ee = request.getHeaderNames(); ee.hasMoreElements();) {
					n = ee.nextElement();
					optAdd(NET_ATT_SRVCALL_HEADERS, n, request.getHeader(n));
				}

				Dust.access(MindAccess.Commit, MindAction.Process, target);

				int status = Dust.access(MindAccess.Peek, HttpServletResponse.SC_OK, MindContext.Self, MISC_ATT_CONN_TARGET,
						NET_ATT_SRVCALL_STATUS);

				response.setStatus(status);
			}
		} catch (Throwable t) {
			exception = t;
		} finally {
			Dust.log(null, "Http request: " + pathInfo, "Process time: " + (System.currentTimeMillis() - ts) + " msec",
					(null == exception) ? "Success" : "error: " + exception);
		}

		if (null != exception) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void optAdd(Object kind, String name, Object val) {
		Dust.access(MindAccess.Set, val, MindContext.Self, MISC_ATT_CONN_TARGET, kind, name);
	}
}