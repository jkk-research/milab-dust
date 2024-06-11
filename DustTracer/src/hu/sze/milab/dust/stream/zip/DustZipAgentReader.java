package hu.sze.milab.dust.stream.zip;

import java.io.File;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;

public class DustZipAgentReader extends DustAgent implements DustZipConsts {
	String path;

	ZipFile zipFile;
	Enumeration<ZipArchiveEntry> zipEnum;
	ZipArchiveEntry zipArchiveEntry;

	long openTime;
	long count;

	@Override
	protected MindHandle agentBegin() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_REJECT;
		
		Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_SOURCE);
		File f = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_SOURCE, DUST_ATT_IMPL_DATA);

		if ( f.isFile() ) {
			Dust.log(EVENT_TAG_TYPE_TRACE, "Opening file", path = f.getCanonicalPath());

			long t = System.currentTimeMillis();
			zipFile = new ZipFile(f);
			Dust.access(MindAccess.Set, zipFile, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET, DUST_ATT_IMPL_DATA);

			zipEnum = zipFile.getEntries();
			openTime = System.currentTimeMillis() - t;
			Dust.log(EVENT_TAG_TYPE_TRACE, path, "Open time", openTime);

			if ( zipEnum.hasMoreElements() ) {
				Dust.access(MindAccess.Commit, MIND_TAG_ACTION_BEGIN, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);
				ret = MIND_TAG_RESULT_READACCEPT;
			} else {
				ret = MIND_TAG_RESULT_PASS;
			}
		}

		return ret;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		zipArchiveEntry = zipEnum.nextElement();

		if ( !zipArchiveEntry.isDirectory() ) {
			++count;
			String name = zipArchiveEntry.getName();

			Dust.access(MindAccess.Set, zipArchiveEntry, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET, RESOURCE_ASP_STREAM);
			Dust.access(MindAccess.Set, name, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET, RESOURCE_ATT_URL_PATH);
			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);
		}
		return zipEnum.hasMoreElements() ? MIND_TAG_RESULT_READACCEPT : MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		zipArchiveEntry = null;

		if ( null != zipFile ) {
			zipFile.close();
			zipFile = null;
		}

		Dust.log(EVENT_TAG_TYPE_TRACE, path, "Open time", openTime, "File count", count);
		
		Dust.access(MindAccess.Commit, MIND_TAG_ACTION_END, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);

		return MIND_TAG_RESULT_ACCEPT;
	}

}
