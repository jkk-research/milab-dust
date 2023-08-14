package hu.sze.milab.dust.net.httpsrv;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.net.DustNetConsts;

public abstract class DustHttpServerBase implements DustNetConsts, DustConsts.MindAgent {

	class ProcessorWrapperServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		String charset;
		String contentType;

		@Override
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			request.getServerPort();
			
			Object eProc = null;

			Enumeration<String> ee;
			String n = null;

			for (ee = request.getAttributeNames(); ee.hasMoreElements();) {
				n = ee.nextElement();
				optAdd(eProc, n, request.getAttribute(n));
			}

			for (ee = request.getParameterNames(); ee.hasMoreElements();) {
				n = ee.nextElement();
				optAdd(eProc, n, request.getParameter(n));
			}

			for (ee = request.getHeaderNames(); ee.hasMoreElements();) {
				n = ee.nextElement();
				optAdd(eProc, n, request.getHeader(n));
			}

			response.setCharacterEncoding(charset);
			response.setContentType(contentType);

//    request.getMethod();
//    response.getWriter();

			response.setStatus(HttpServletResponse.SC_OK);
		}

		private void optAdd(Object msg, String name, Object val) {
		}
	}

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		switch ( action ) {
		case Begin:
			break;
		case End:
			break;
		case Init:
			activeInit();
			break;
		case Process:
			break;
		case Release:
			activeRelease();
			break;
		}

		return MindStatus.Accept;
	}

	public void activeInit() throws Exception {
		initConnectors();
		initHandlers();
	}

	public void activeRelease() throws Exception {
	}

	public void initConnectors() throws Exception {
//        int pPub = DustUtils.getInt(ContextRef.self, DustNetAtts.NetServerPublicPort, NO_PORT_SET);
//        int pSsl = DustUtils.getInt(ContextRef.self, DustNetAtts.NetServerSslPort, NO_PORT_SET);
//
//        if (NO_PORT_SET != pPub) {
//            initConnectorPublic(pPub, pSsl);
//        }
//
//        if (NO_PORT_SET != pSsl) {
//            initConnectorSsl(pSsl);
//        }
	}

	protected void initHandlers() {
//        DustUtils.accessEntity(DataCommand.processRef, ContextRef.self, DustProcLinks.DispatcherTargets, new RefProcessor() {
//            @Override
//            public void processRef(DustRef ref) {
//                DustEntity sc = ref.get(RefKey.target);
//
//                String ctx = DustUtils.accessEntity(DataCommand.getValue, sc, DustGenericAtts.IdentifiedIdLocal);
//                addServlet("/" + ctx, new ProcessorWrapperServlet(sc));
//            }
//        });
	}

	protected abstract void initConnectorPublic(int portPublic, int portSsl);

	protected void initConnectorSsl(int portSsl) {
	}

	protected abstract void addServlet(String path, HttpServlet servlet);

}
