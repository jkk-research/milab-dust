package hu.sze.milab.dust.net.httpsrv;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.stream.DustStreamUtils;
import hu.sze.milab.dust.stream.json.DustJsonConsts;
import hu.sze.milab.dust.utils.DustUtils;

public class DustHttpFileAgent extends DustAgent implements DustNetConsts, DustJsonConsts {

	@Override
	public MindHandle agentProcess() throws Exception {
		HttpServletResponse response = Dust.access(MindAccess.Peek, null, MindContext.Self, MISC_ATT_CONN_TARGET,
				NET_ATT_SRVCALL_RESPONSE);

		if (null != response) {
			String path = Dust.access(MindAccess.Get, null, MindContext.Self, MISC_ATT_CONN_TARGET, NET_ATT_SRVCALL_PATHINFO);
			
			if ( DustUtils.isEmpty(path) ) {
				 path = Dust.access(MindAccess.Get, null, MindContext.Self, RESOURCE_ATT_URL_PATH);
			}
			
			if (!DustStreamUtils.checkPathBound(path)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return MIND_TAG_RESULT_REJECT;
			}

			Collection<Object> roots = Dust.access(MindAccess.Get, Collections.EMPTY_LIST, MindContext.Self,
					MISC_ATT_CONN_MEMBERARR);

			File f = null;

			for (Object root : roots) {
				String p = Dust.access(MindAccess.Get, null, root, RESOURCE_ATT_URL_PATH);

				if (!DustUtils.isEmpty(p)) {
					String res = Dust.access(MindAccess.Get, path, root, MISC_ATT_CONN_MEMBERMAP, path);
					if (null != (f = DustStreamUtils.optGetFile(p, res))) {
						break;
					}
				}
			}

			if (null == f) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return MIND_TAG_RESULT_REJECT;
			}

			String ext = DustUtils.getPostfix(f.getName(), ".").toLowerCase();
			String ct = Dust.access(MindAccess.Get, MEDIATYPE_RAW, MindContext.Self, RESOURCE_ATT_STREAM_CTYPEMAP, ext);

			response.setContentType(ct);

			OutputStream out = response.getOutputStream();
			Files.copy(f.toPath(), out);
			out.flush();
		}

		return MIND_TAG_RESULT_ACCEPT;
	}

}
