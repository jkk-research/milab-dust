package hu.sze.milab.dust.net.httpsrv;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.net.DustNetUtils;
import hu.sze.milab.dust.stream.DustStreamConsts;
import hu.sze.milab.dust.utils.DustUtils;

public class DustHttpAgentDirectFile implements DustNetConsts, DustStreamConsts, DustConsts.MindAgent {

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		MindStatus ret = MindStatus.Accept;

		switch ( action ) {
		case Init:
			break;
		case Begin:
			break;
		case Process:
			HttpServletResponse response = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, NET_ATT_SRVCALL_RESPONSE);

			if ( null != response ) {
				File f = null;
				
				String reqPath = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, NET_ATT_SRVCALL_PATHINFO);
				Map<String, String> alias = Dust.access(MindContext.Self, MindAccess.Peek, Collections.EMPTY_MAP, MISC_ATT_ALIAS);
				reqPath = alias.getOrDefault(reqPath, reqPath);

				String path = Dust.access(MindContext.Self, MindAccess.Peek, null, STREAM_ATT_STREAM_PATH);
				if ( !DustUtils.isEmpty(path) ) {
					File rootDir = new File(path);
					if ( rootDir.isDirectory() ) {
						f = new File(rootDir, reqPath);

						if ( !f.getCanonicalPath().startsWith(rootDir.getCanonicalPath()) ) {
							Dust.access(MindContext.LocalCtx, MindAccess.Set, HttpServletResponse.SC_FORBIDDEN, NET_ATT_SRVCALL_STATUS);
							return MindStatus.Reject;
						}
					}
				}

				if ( (null != f) && f.isFile() ) {
					Dust.access(MindContext.LocalCtx, MindAccess.Set, HttpServletResponse.SC_OK, NET_ATT_SRVCALL_STATUS);
					
					String ct = DustNetUtils.getContentType(f);
					if ( !DustUtils.isEmpty(ct)) {
						response.setContentType(ct);
					}
					
					OutputStream out = response.getOutputStream();
					Files.copy(f.toPath(), out);
					out.flush();

				} else {
					Integer nf = HttpServletResponse.SC_NOT_FOUND;
					Dust.access(MindContext.LocalCtx, MindAccess.Set, nf, NET_ATT_SRVCALL_STATUS);

					Collection<Object> agents = Dust.access(MindContext.Self, MindAccess.Peek, Collections.EMPTY_LIST, MISC_ATT_CONN_MEMBERARR);
					for (Object agent : agents) {
						Dust.access(agent, MindAccess.Commit, MindAction.Process);
						
						if ( !nf.equals(Dust.access(MindContext.LocalCtx, MindAccess.Set, null, NET_ATT_SRVCALL_STATUS) ) ) {
							break;
						}
					}
				}
			}
			break;
		case End:
			break;
		case Release:
			break;
		}

		return ret;
	}
}