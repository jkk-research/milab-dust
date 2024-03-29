package hu.sze.milab.dust.net.httpsrv;

import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.utils.DustUtils;

public class DustHttpServerJetty extends DustAgent implements DustNetConsts //, DustConsts.DustThreadOwner 
{
	enum Commands {
		stop, info,
	}

	Server jetty;
	HandlerList handlers;
	ServletContextHandler ctxHandler;

	@Override
	protected MindHandle agentInit() throws Exception {
		if (null == jetty) {
			jetty = new Server();
			handlers = new HandlerList();

			long port = Dust.access(MindAccess.Peek, 8080, MindContext.Self, NET_ATT_HOST_PORT);
			HttpConfiguration http = new HttpConfiguration();

			ServerConnector connector = new ServerConnector(jetty);
			connector.addConnectionFactory(new HttpConnectionFactory(http));
			connector.setPort((int) port);
			connector.setName("TEST");

			jetty.addConnector(connector);

			System.out.println("Connector: " + connector);

			Long sslPort = Dust.access(MindAccess.Peek, null, MindContext.Self, NET_ATT_SSLINFO_PORT);

			if (null != sslPort) {
				HttpConfiguration https = new HttpConfiguration();
				https.addCustomizer(new SecureRequestCustomizer());

				SslContextFactory sslContextFactory = new SslContextFactory();

				String str;
				str = Dust.access(MindAccess.Peek, null, MindContext.Self, NET_ATT_SSLINFO_STOREPATH);
				sslContextFactory.setKeyStorePath(ClassLoader.getSystemResource(str).toExternalForm());
				str = Dust.access(MindAccess.Peek, null, MindContext.Self, NET_ATT_SSLINFO_STOREPASS);
				sslContextFactory.setKeyStorePassword(str);
				str = Dust.access(MindAccess.Peek, null, MindContext.Self, NET_ATT_SSLINFO_KEYMANAGERPASS);
				sslContextFactory.setKeyManagerPassword(str);

				ServerConnector sslConnector = new ServerConnector(jetty,
						new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
				sslConnector.setPort(sslPort.intValue());

				jetty.addConnector(sslConnector);
			}

			ctxHandler = new ServletContextHandler();
			ctxHandler.setContextPath("/*");
			handlers.addHandler(ctxHandler);
			
			MindHandle hSelf = Dust.access(MindAccess.Peek, null, MindContext.Self, MIND_ATT_KNOWLEDGE_HANDLE);

			ctxHandler.addServlet(new ServletHolder(new DustHttpServletDispatcher(hSelf)), "/*");

			jetty.setHandler(handlers);
			jetty.start();
		}

		return MIND_TAG_RESULT_ACCEPT;
	}
	
	@Override
	protected MindHandle agentBegin() throws Exception {
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public final MindHandle agentProcess() throws Exception {
		Commands cmd = Commands.info;
		String str = Dust.access(MindAccess.Get, null, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_PATHINFO);
		if (!DustUtils.isEmpty(str)) {
			try {
				String sc = DustUtils.getPostfix(str, "/");
				cmd = Commands.valueOf(sc);
			} catch (Exception e) {
//							DustException.swallow(e);
			}
		}
		
		HttpServletResponse response;
		
		switch (cmd) {
		case stop:
			response = Dust.access(MindAccess.Peek, null, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_RESPONSE);

			if (null != response) {
				response.setContentType(MEDIATYPE_UTF8_HTML);
				PrintWriter out = response.getWriter();

				out.println("<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n<title>Server shutdown</title>\n</head>\n<body>\n");
				out.println("<h2>Server shutdown initiated.</h2>\n");
				out.println("</body></html>\n");
				
				out.flush();
			}
			
			new Thread() {
				@Override
				public void run() {
					try {
						Dust.log(EVENT_TAG_TYPE_TRACE, "Shutting down Jetty server...");
						release();
						Dust.log(EVENT_TAG_TYPE_INFO, "Jetty server shutdown OK.");
					} catch (Exception ex) {
						DustException.wrap(ex, "Failed to stop Jetty");
					}
				}
			}.start();
			break;
		case info:
			response = Dust.access(MindAccess.Peek, null, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_RESPONSE);

			if (null != response) {
				Properties pp = System.getProperties();

				StringBuilder sb = new StringBuilder("<!doctype html>\n" + "<html lang=\"en\">\n"
						+ "<head>\n<meta charset=\"utf-8\">\n<title>Hello World Server</title>\n</head>\n" + "<body>");

				sb.append("<h2>Server info</h2>");
				sb.append("<ul>");

				for (Object o : pp.keySet()) {
					String key = o.toString();
					sb.append("<li>" + key + ": " + pp.getProperty(key) + "</li>");
				}

				sb.append("</ul>");

				sb.append("<h2>Commands</h2>");
				sb.append("<ul>");
				for (Commands cc : Commands.values()) {
					sb.append("<li><a href=\"/admin/" + cc + "\">" + cc + "</a></li>");
				}
				sb.append("</ul>");
				
				sb.append("</body></html>");
				response.setContentType(MEDIATYPE_UTF8_HTML);
				PrintWriter out = response.getWriter();

				out.println(sb.toString());
			}
			break;
		}

		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	protected MindHandle agentRelease() throws Exception {
		release();
		return MIND_TAG_RESULT_ACCEPT;
	}

	private void release() throws Exception {
		if (null != jetty) {
			Server j = jetty;
			jetty = null;
			handlers = null;
			ctxHandler = null;

			j.stop();
		}
	}
}
