package hu.sze.milab.dust.stream.zip;

import java.io.File;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.utils.DustUtils;

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

		String fileName = System.getProperty("TestParam01");

		Dust.log(EVENT_TAG_TYPE_TRACE, "Accessing file", fileName);

		if ( !DustUtils.isEmpty(fileName) ) {
			File home = new File(System.getProperty("user.home"));
			File f = new File(home, fileName);

			if ( f.isFile() ) {
				Dust.log(EVENT_TAG_TYPE_TRACE, "Opening file", path = f.getCanonicalPath());

				long t = System.currentTimeMillis();
				zipFile = new ZipFile(f);
				zipEnum = zipFile.getEntries();
				ret = zipEnum.hasMoreElements() ? MIND_TAG_RESULT_READACCEPT : MIND_TAG_RESULT_PASS;
				openTime = System.currentTimeMillis() - t;
			}
		}

		return ret;
	}

	@Override
	public MindHandle agentProcess() throws Exception {
		zipArchiveEntry = zipEnum.nextElement();

		if ( !zipArchiveEntry.isDirectory() ) {
			++ count;
			String name = zipArchiveEntry.getName();
			String[] ss = DustUtils.cutPostfix(name, ".").split("-");

			if ( ss.length > 1 ) {
				Dust.log(EVENT_TAG_TYPE_TRACE, "multifile", ss[0]);
			}
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

		return MIND_TAG_RESULT_ACCEPT;
	}

}
