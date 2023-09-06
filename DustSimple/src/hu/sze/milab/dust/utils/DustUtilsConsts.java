package hu.sze.milab.dust.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Set;
import java.util.TreeSet;

import hu.sze.milab.dust.DustConsts;

public interface DustUtilsConsts extends DustConsts {
	public enum StringMatch {
		Equals, Contains, EndsWith, StartsWith,
	}

	public static class DustFileFilter implements FileFilter, FilenameFilter {
		boolean ignoreCase;
		StringMatch matchMode;

		Set<String> matches = new TreeSet<>();

		public DustFileFilter(boolean ignoreCase, StringMatch matchMode, String... options) {
			this.ignoreCase = ignoreCase;
			this.matchMode = matchMode;

			for (String o : options) {
				o = o.trim();
				if ( ignoreCase ) {
					o = o.toLowerCase();
				}
				matches.add(o);
			}
		}

		@Override
		public boolean accept(File f) {
			return accept(f.getParentFile(), f.getName());
		}

		@Override
		public boolean accept(File dir, String n) {
			
			if ( ignoreCase ) {
				n = n.toLowerCase();
			}

			if ( matchMode == StringMatch.Equals ) {
				return matches.contains(n);
			} else {
				for (String o : matches) {
					boolean found = false;
					
					switch ( matchMode ) {
					case Contains:
						found = n.contains(o);
						break;
					case EndsWith:
						found = n.endsWith(o);
						break;
					case Equals:
						// done
						break;
					case StartsWith:
						found = n.startsWith(o);
						break;
					}

					if ( found ) {
						return true;
					}
				}
			}
			return false;
		}

	}
}
