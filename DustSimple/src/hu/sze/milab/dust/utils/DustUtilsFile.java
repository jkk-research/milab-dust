package hu.sze.milab.dust.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import hu.sze.milab.dust.DustConsts;

public class DustUtilsFile extends DustUtils implements DustConsts {

	public static String optRemoveUpFromPath(String path) {
		String ret = path;

		if ( path.contains(".") ) {
			String[] items = path.split(File.separator);

			int ai = 0;

			for (int i = 0; i < items.length; ++i) {
				String p = items[i];

				switch ( p ) {
				case ".":
					break;
				case "..":
					--ai;
					break;
				default:
					if ( ai != i ) {
						items[ai] = items[i];
					}
					++ai;
					break;
				}
			}
			
			StringBuilder b = null;
			for (int i = 0; i < ai; ++i) {
				b = DustUtils.sbAppend(b, File.separator, true, items[i]);
			}
			ret = b.toString();
		}

		return ret;
	}

	public static String getHashName(String fileName) {
		int hash = fileName.hashCode();

		int mask = 255;
		int firstDir = hash & mask;
		int secondDir = (hash >> 8) & mask;

		return String.format("%02x%s%02x%s%s", firstDir, File.separator, secondDir, File.separator, fileName);
	}

	public static File getHashDir(File root, String dirName) {
		String path = getHashName(dirName);

		File f = new File(root, path);

		f.mkdirs();

		return f;
	}

	public static void unzip(File fromFile, File destDir) throws Exception {
		byte[] buffer = new byte[1024];

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fromFile))) {
			for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
				File newFile = new File(destDir, zipEntry.getName());

				String destDirPath = destDir.getCanonicalPath();
				String destFilePath = newFile.getCanonicalPath();

				if ( !destFilePath.startsWith(destDirPath + File.separator) ) {
					throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
				}

				if ( zipEntry.isDirectory() ) {
					if ( !newFile.isDirectory() && !newFile.mkdirs() ) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					File parent = newFile.getParentFile();
					if ( !parent.isDirectory() && !parent.mkdirs() ) {
						throw new IOException("Failed to create directory " + parent);
					}

					FileOutputStream oo = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						oo.write(buffer, 0, len);
					}
					oo.close();
				}
			}

			zis.closeEntry();
		}
	}
}
