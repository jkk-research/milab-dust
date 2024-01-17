package hu.sze.milab.dust;

import java.util.Comparator;

public class DustDevUtils implements DustMetaConsts {
	
	public static final Comparator<String> ID_COMP = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			String[] s1 = o1.split(DUST_SEP_ID);
			String[] s2 = o2.split(DUST_SEP_ID);

			int ret = Integer.valueOf(s1[0]) - Integer.valueOf(s2[0]);

			if ( 0 == ret ) {
				ret = s1.length - s2.length;
				if ( (0 == ret) && (s1.length == 2) ) {
					ret = Integer.valueOf(s1[1]) - Integer.valueOf(s2[1]);
				}
			}

			return ret;
		}
	};



	public static void breakpoint(Object... objects) {
		Dust.log(EVENT_TAG_TYPE_BREAKPOINT, objects);
	}

}
