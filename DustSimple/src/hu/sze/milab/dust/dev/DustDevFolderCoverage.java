package hu.sze.milab.dust.dev;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import hu.sze.milab.dust.Dust;

public class DustDevFolderCoverage {
	private Set<String> allFiles = new TreeSet<>();

	public DustDevFolderCoverage(File root) {
		addFolder(root);
	}
	
	void addFolder(File dir) {
		for (File f : dir.listFiles()) {
			if ( f.isFile() ) {
				if ( f.getName().startsWith(".") ) {
					continue;
				}
				try {
					allFiles.add(f.getCanonicalPath());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else if ( f.isDirectory() ) {
				addFolder(f);
			}
		}
	}

	public int countFilesToVisit() {
		return allFiles.size();
	}

	public void setSeen(File... files) throws Exception {
		for (File f : files) {
			allFiles.remove(f.getCanonicalPath());
		}
	}

	public void dump() throws Exception {
		if ( allFiles.isEmpty() ) {
			Dust.dumpObs("All files visited");
		} else {
			Dust.dumpObs("Unseen files");

			for (String s : allFiles) {
				Dust.dumpObs("   ", s);
			}
		}
	}
}
