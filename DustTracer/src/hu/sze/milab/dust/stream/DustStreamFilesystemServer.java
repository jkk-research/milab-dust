package hu.sze.milab.dust.stream;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.utils.DustUtils;

public class DustStreamFilesystemServer extends DustAgent implements DustStreamConsts {

	@Override
	protected MindHandle agentBegin() throws Exception {
		return optSetStream();
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		return optSetStream();
	}

	public MindHandle optSetStream() throws Exception {

		Object val = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);

		if ( null == val ) {
//			File fDataRoot = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, DUST_ATT_IMPL_DATA);
//
//			if ( null == fDataRoot ) {
//				fDataRoot = DustStreamUtils.getFile(MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_URL_PATH);
//				Dust.access(MindAccess.Set, fDataRoot, MIND_TAG_CONTEXT_SELF, DUST_ATT_IMPL_DATA);
//			}
//
//			File fDir = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, DUST_ATT_IMPL_DATA);

			File f = getFile(MIND_TAG_CONTEXT_TARGET, MIND_TAG_CONTEXT_SELF, true);

			Object dir = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, MIND_ATT_KNOWLEDGE_TAGS, MISC_TAG_DIRECTION);

			boolean missing = !f.exists() || (0 == f.length());

			if ( missing && (MISC_TAG_DIRECTION_IN == dir) ) {
				return MIND_TAG_RESULT_REJECT;
			}

			Object type = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, MIND_ATT_KNOWLEDGE_TAGS, RESOURCE_TAG_STREAMTYPE);
			
			if (MISC_TAG_DIRECTION_IN == dir) {
				val = (RESOURCE_TAG_STREAMTYPE_TEXT == type) ? new FileReader(f) : new FileInputStream(f);
			} else {
				val = (RESOURCE_TAG_STREAMTYPE_TEXT == type) ? new PrintWriter(f) : new FileOutputStream(f);				
			}

			Dust.access(MindAccess.Set, val, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);
		}

		return MIND_TAG_RESULT_ACCEPT;
	}

	public File getFile(MindHandle h, MindHandle hParent, boolean file) throws IOException {

		File f = Dust.access(MindAccess.Peek, null, h, DUST_ATT_IMPL_DATA);

		if ( null == f ) {
			if ( null == hParent ) {
				hParent = Dust.access(MindAccess.Peek, null, h, MISC_ATT_CONN_PARENT);
			}

			File dirParent = (null == hParent) ? null : Dust.access(MindAccess.Peek, null, hParent, DUST_ATT_IMPL_DATA);
			if ( (null == dirParent) && (null != hParent) ) {
				dirParent = getFile(hParent, null, false);
			}

			String fName = Dust.access(MindAccess.Peek, null, h, TEXT_ATT_TOKEN);
			if ( DustUtils.isEmpty(fName) ) {
				fName = Dust.access(MindAccess.Peek, null, h, RESOURCE_ATT_URL_PATH);
				f = new File(fName);
			} else {
				f = new File(dirParent, fName);
				Dust.access(MindAccess.Set, f.getCanonicalPath(), MIND_TAG_CONTEXT_TARGET, RESOURCE_ATT_URL_PATH);
			}

			if ( null != dirParent ) {
				String cp = dirParent.getCanonicalPath();
				if ( !f.getCanonicalPath().startsWith(cp) ) {
					DustException.wrap(new AccessDeniedException(fName), "Requested file path not under root", cp);
				}
			}

			if ( !f.exists() ) {
				File d = file ? f.getParentFile() : f;
				d.mkdirs();
			}

			if ( f.exists() && !file ) {
				Dust.access(MindAccess.Set, f, h, DUST_ATT_IMPL_DATA);
			}
		}
		return f;
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_ACCEPT;

		Object val = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);

		if ( val instanceof Flushable ) {
			((Flushable) val).flush();
		}

		if ( val instanceof Closeable ) {
			((Closeable) val).close();
		}

		Dust.access(MindAccess.Set, null, MIND_TAG_CONTEXT_TARGET, DUST_ATT_IMPL_DATA);

		return ret;
	}

}
