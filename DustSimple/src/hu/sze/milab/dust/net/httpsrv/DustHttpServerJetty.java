package hu.sze.milab.dust.net.httpsrv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
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

public class DustHttpServerJetty extends DustHttpServerBase {
	enum Commands {
		stop, info,
	}

	Server jetty;
	HandlerList handlers;
	ServletContextHandler ctxHandler;

//    HashSessionIdManager sessionIdManager;

	public void activeInit() throws Exception {
		jetty = new Server();
		handlers = new HandlerList();

		super.activeInit();

		jetty.setHandler(handlers);
		jetty.start();
	}

	public void activeRelease() throws Exception {
		if ( null != jetty ) {
			super.activeRelease();

			Server j = jetty;
			jetty = null;
			handlers = null;
			ctxHandler = null;

//            sessionIdManager = null;

			j.stop();
		}
	}

	@Override
	protected void initConnectorSsl(int portSsl) {
		HttpConfiguration https = new HttpConfiguration();
		https.addCustomizer(new SecureRequestCustomizer());

		// Configuring SSL
		SslContextFactory sslContextFactory = new SslContextFactory();

//        String str;
//        str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSslInfoStorePath, false);
//        sslContextFactory.setKeyStorePath(ClassLoader.getSystemResource(str).toExternalForm());
//        str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSslInfoStorePass, false);
//        sslContextFactory.setKeyStorePassword(str);
//        str = DustUtils.getCtxVal(ContextRef.self, DustNetAtts.NetSslInfoManagerPass, false);
//        sslContextFactory.setKeyManagerPassword(str);

		ServerConnector sslConnector = new ServerConnector(jetty, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
		sslConnector.setPort(portSsl);

		jetty.addConnector(sslConnector);
	}

	@Override
	protected void initConnectorPublic(int portPublic, int portSsl) {
		HttpConfiguration http = new HttpConfiguration();

		if ( NO_PORT_SET != portSsl ) {
			http.addCustomizer(new SecureRequestCustomizer());
			http.setSecurePort(portSsl);
			http.setSecureScheme("https");
		}

		ServerConnector connector = new ServerConnector(jetty);
		connector.addConnectionFactory(new HttpConnectionFactory(http));
		connector.setPort(portPublic);

		jetty.addConnector(connector);
	}

	public void initConnectors() throws Exception {
		initConnectorPublic(8080, NO_PORT_SET);
	}

	protected void initHandlers() {
		addServlet("/admin/*", new ProcessorWrapperServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				super.service(request, response);
				String str;
//    			str = request.getQueryString();
//    			str = request.getContextPath();
//    			str = request.getPathTranslated();
//    			str = request.getRequestURI();
//    			str = request.getServletPath();
//    			str = request.getRequestURL().toString();
				str = request.getPathInfo();
				if ( null != str ) {
					int idx = str.lastIndexOf("/");
					if ( -1 != idx ) {
						str = str.substring(idx+1).toLowerCase();
					}
				}

				Commands cmd;
				try {
					cmd = Commands.valueOf(str);
				} catch (Throwable e) {
					cmd = Commands.info;
				}

				switch ( cmd ) {
				case stop:
					new Thread() {
						@Override
						public void run() {
							try {
								activeRelease();
//  								DustGenLog.log("Shutting down Jetty server...");
//  								server.stop();
//  								DustGenLog.log("Jetty server shutdown OK.");
							} catch (Exception ex) {
//  								DustGenLog.log(DustEventLevel.ERROR, "Failed to stop Jetty");
							}
						}
					}.start();
					break;
				case info:
					Properties pp = System.getProperties();

					StringBuilder sb = new StringBuilder("<!doctype html>\n" + "<html lang=\"en\">\n" + "<head>\n<meta charset=\"utf-8\">\n<title>Hello World Server</title>\n</head>\n" + "<body>");

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

					PrintWriter out = response.getWriter();

					out.println(sb.toString());

					break;

				}

			}
		});

		addServlet("/*", new ProcessorWrapperServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				super.service(request, response);

				response.getOutputStream();
			}
		});
	}

	@Override
	protected void addServlet(String path, HttpServlet servlet) {
		if ( null == ctxHandler ) {
			ctxHandler = new ServletContextHandler();
			ctxHandler.setContextPath("/*");
			handlers.addHandler(ctxHandler);
		}

		ctxHandler.addServlet(new ServletHolder(servlet), path);
	}
	
//	public static void main(String[] args) throws Exception {
//		DustHttpServerJetty srv = new DustHttpServerJetty();
//		srv.activeInit();
//	}

	// private HashSessionIdManager getSessionIdManager() {
	// if (null == sessionIdManager) {
	// sessionIdManager = new HashSessionIdManager();
	// jetty.setSessionIdManager(sessionIdManager);
	// }
	//
	// return sessionIdManager;
	// }

	// public void initHandlers() throws Exception {
	// AbstractHandler h = new AbstractHandler() {
	// public void handle(String target, Request baseRequest, HttpServletRequest
	// request, HttpServletResponse response)
	// throws IOException, ServletException {
	//
	// response.setCharacterEncoding(CHARSET_UTF8);
	// response.setContentType(CONTENT_JSON);
	//
	// response.setStatus(HttpServletResponse.SC_OK);
	// baseRequest.setHandled(true);
	//
	// DustPersistentStorageJsonSingle st = new
	// DustPersistentStorageJsonSingle(null);
	//
	// st.writer = response.getWriter();
	//
	// DustPersistence.commit(st);
	//
	//// InputStream is = new FileInputStream("output/temp/TestSingle.json");
	////
	//// OutputStream outStream = response.getwOutputStream();
	////
	//// byte[] buffer = new byte[8 * 1024];
	//// int bytesRead;
	//// while ((bytesRead = is.read(buffer)) != -1) {
	//// outStream.write(buffer, 0, bytesRead);
	//// }
	////
	//// is.close();
	// }
	// };
	//
	// handlers.addHandler(h);
	// }

}
