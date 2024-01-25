package hu.sze.milab.dust.utils;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import hu.sze.milab.dust.Dust;

public class DustUtilsApache implements DustUtilsConsts {
	
	public static void unzipEntry(ZipFile zipFile, ZipArchiveEntry zipEntry, File toFile) throws Exception {
		DustUtilsFile.ensureDir(toFile.getParentFile());
		try (OutputStream o = Files.newOutputStream(toFile.toPath())) {
			Dust.log(EVENT_TAG_TYPE_TRACE, "Apache unzip", zipEntry.getName(), toFile.getCanonicalPath());
			IOUtils.copy(zipFile.getInputStream(zipEntry), o);
		}
	}

}
