package hu.sze.milab.dust.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.dev.DustDevFolderCoverage;

public interface DustUtilsConsts extends DustConsts {
	public enum StringMatch {
		Equals, Contains, EndsWith, StartsWith,
	}
	
	public interface DustCloseableWalker<ItemType> extends Iterable<ItemType>, Iterator<ItemType>, Closeable {
		
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

	public static class DustUrlResolver {
		File root;
		public final Map<String, String> uriRewrite = new TreeMap<>();

		DustDevFolderCoverage folderCoverage;

		public DustUrlResolver(File root) {
			setRoot(root);
		}
		
		public File getRoot() {
			return root;
		}

		public void setRoot(File root) {
			this.root = root;
		}
		
		public String optLocalizeUrl(String href, String url) {
			String refUrl = href;

			if ( !href.contains(":") ) {
				refUrl = DustUtils.replacePostfix(url, "/", href);
			} else {
				for (Map.Entry<String, String> e : uriRewrite.entrySet()) {
					String prefix = e.getKey();
					if ( url.startsWith(prefix) ) {
						File f = new File(root, e.getValue());
						f = new File(f, url.substring(prefix.length()));
						try {
							refUrl = f.toURI().toURL().toString();
						} catch (Throwable e1) {
							e1.printStackTrace();
						}
						break;
					}
				}
			}

			refUrl = DustUtilsFile.optRemoveUpFromPath(refUrl);

			return refUrl;
		}

	}
}
