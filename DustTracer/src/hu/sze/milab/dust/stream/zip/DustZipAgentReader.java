package hu.sze.milab.dust.stream.zip;

import java.io.File;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustZipAgentReader extends DustAgent implements DustZipConsts {
	String path;

	ZipFile zipFile;
	Enumeration<ZipArchiveEntry> zipEnum;
	ZipArchiveEntry zipArchiveEntry;

	long openTime;
	long count;

	@Override
	public MindHandle agentBegin() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_REJECT;

		File f = DustUtilsFile.getFile(MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_SOURCE, RESOURCE_ATT_URL_PATH);

		if ( f.isFile() ) {
			Dust.log(EVENT_TAG_TYPE_TRACE, "Opening file", path = f.getCanonicalPath());

			long t = System.currentTimeMillis();
			zipFile = new ZipFile(f);
			Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_SET, zipFile, MISC_ATT_CONN_TARGET, MISC_ATT_VARIANT_VALUE);

			zipEnum = zipFile.getEntries();
			openTime = System.currentTimeMillis() - t;

			if ( zipEnum.hasMoreElements() ) {
				Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_COMMIT, MIND_TAG_ACTION_BEGIN, MISC_ATT_CONN_TARGET);
				ret = MIND_TAG_RESULT_READACCEPT;
			} else {
				ret = MIND_TAG_RESULT_PASS;
			}
		}

		return ret;
	}

	@Override
	public MindHandle agentProcess() throws Exception {
		zipArchiveEntry = zipEnum.nextElement();

		if ( !zipArchiveEntry.isDirectory() ) {
			++count;
			String name = zipArchiveEntry.getName();

			Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_SET, zipArchiveEntry, MISC_ATT_CONN_TARGET, RESOURCE_ASP_STREAM);
			Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_SET, name, MISC_ATT_CONN_TARGET, RESOURCE_ATT_URL_PATH);
			Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_COMMIT, MIND_TAG_ACTION_PROCESS, MISC_ATT_CONN_TARGET);
		}
		return zipEnum.hasMoreElements() ? MIND_TAG_RESULT_READACCEPT : MIND_TAG_RESULT_ACCEPT;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		zipArchiveEntry = null;

		if ( null != zipFile ) {
			zipFile.close();
			zipFile = null;
		}

		Dust.log(EVENT_TAG_TYPE_TRACE, path, "Open time", openTime, "File count", count);
		
		Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_COMMIT, MIND_TAG_ACTION_END, MISC_ATT_CONN_TARGET);

		return MIND_TAG_RESULT_ACCEPT;
	}

}
