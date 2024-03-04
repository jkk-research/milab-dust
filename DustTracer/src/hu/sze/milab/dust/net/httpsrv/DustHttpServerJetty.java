package hu.sze.milab.dust.net.httpsrv;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
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
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.utils.DustUtils;

public class DustHttpServerJetty extends DustAgent
		implements DustNetConsts, DustConsts.MindAgent, DustConsts.DustThreadOwner {
	enum Commands {
		stop, info,
	}

	Server jetty;
	HandlerList handlers;
	ServletContextHandler ctxHandler;

	Set<String> ownPrefixes = new HashSet<>();

//
//	@Override
//	public MindHandle agentProcess(MindAction action) throws Exception {
//		
//		switch ( action ) {
//		case Begin:
//			break;
//		case End:
//			break;
//		case Init:
//			init();
//			break;
//		case Process:
//			Commands cmd = Commands.info;
//
//			String str = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, NET_ATT_SRVCALL_PATHINFO);
//			if ( !DustUtils.isEmpty(str) ) {
//				try {
//					String sc = DustUtils.getPostfix(str, "/");
//					cmd = Commands.valueOf(sc);
//				} catch (Exception e) {
////					DustException.swallow(e);
//				}
//			}
//
//			process(cmd);
//
//			break;
//		case Release:
//			release();
//			break;
//		}
//
//		return MindStatus.Accept;
//	}

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

			ctxHandler.addServlet(new ServletHolder(new DustHttpServletDispatcher()), "/*");

			jetty.setHandler(handlers);
			jetty.start();
		}
		
		return MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public final MindHandle agentProcess() throws Exception {
		Commands cmd = Commands.info;
		String str = Dust.access(MindAccess.Peek, null, MindContext.Target, NET_ATT_SRVCALL_PATHINFO);
		if (!DustUtils.isEmpty(str)) {
			try {
				String sc = DustUtils.getPostfix(str, "/");
				cmd = Commands.valueOf(sc);
			} catch (Exception e) {
//							DustException.swallow(e);
			}
		}
		switch (cmd) {
		case stop:
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
			HttpServletResponse response = Dust.access(MindAccess.Peek, null, MindContext.Target, NET_ATT_SRVCALL_RESPONSE);

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

	@Override
	public boolean isCurrentThreadOwned() {
		if (null != jetty) {
			String[] tns;

			Connector[] conns = jetty.getConnectors();
			int cl = conns.length;
			if (cl != ownPrefixes.size()) {
				ownPrefixes.clear();

				Set<String> postfixes = new HashSet<>(cl);
				for (Connector c : conns) {
					postfixes.add(c.toString());
				}

				for (Thread t : Thread.getAllStackTraces().keySet()) {
					String tn = t.getName();
					if (tn.contains("acceptor")) {
						for (String pf : postfixes) {
							if (tn.endsWith(pf)) {
								tns = tn.split("-");
								ownPrefixes.add(tns[0]);
								break;
							}
						}
					}
				}
			}
			tns = Thread.currentThread().getName().split("-");
			return ownPrefixes.contains(tns[0]);
		}
		return false;
	}

}
