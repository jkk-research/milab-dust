package hu.sze.milab.dust.dev;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustHandles;

public class DustDevUtils implements DustHandles {

	public static final Comparator<String> ID_COMP = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			String[] s1 = o1.split(DUST_SEP_ID);
			String[] s2 = o2.split(DUST_SEP_ID);

			int ret = s1[0].compareTo(s2[0]);
			int m1 = s1.length - 1;
			int m2 = s2.length - 1;

			for (int i = 1; (0 == ret) && (i < 3); ++i) {
				if (i > m1) {
					ret = -1;
				} else if (i > m2) {
					ret = 1;
				} else {
					ret = Integer.valueOf(s1[i]) - Integer.valueOf(s2[i]);
				}
			}

			return ret;
		}
	};

	public static MindHandle newHandle(MindHandle hUnit, MindHandle hPrimaryAspect) {
		return newHandle(hUnit.getId(), hPrimaryAspect);
	};

	public static MindHandle newHandle(String unit, MindHandle hPrimaryAspect) {
		MindHandle h = Dust.lookup(unit + DUST_SEP_ID + ITEMID_NEW);

		Dust.access(MindAccess.Set, hPrimaryAspect, h, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		return h;
	};

	public static MindHandle registerAgent(MindHandle hUnit, MindHandle hLogic) {
		MindHandle hAgent = newHandle(hUnit, MIND_ASP_AGENT);

		Dust.access(MindAccess.Set, hLogic, hAgent, MIND_ATT_AGENT_LOGIC);

		return hAgent;
	};

	public static MindHandle registerLogic(MindHandle hUnit, String nativeClassName) {
		MindHandle hLogic = newHandle(hUnit, MIND_ASP_LOGIC);
		MindHandle hNative = newHandle(hUnit, DUST_ASP_NATIVELOGIC);

		Dust.access(MindAccess.Set, hLogic, hNative, DUST_ATT_NATIVELOGIC_LOGIC);
		Dust.access(MindAccess.Set, nativeClassName, hNative, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);

		Dust.access(MindAccess.Set, hNative, APP_MODULE_MAIN, DUST_ATT_MODULE_NATIVELOGICS, KEY_ADD);

		return hLogic;
	}

	public static void registerNative(MindHandle hLogic, MindHandle hUnit, MindHandle hModule, String nativeClassName) {
		registerNative(hLogic, hUnit, hModule, nativeClassName, false);
	}

	public static void registerNative(MindHandle hLogic, MindHandle hUnit, MindHandle hModule, String nativeClassName, boolean srv) {
		MindHandle hNative = newHandle(hUnit.getId(), DUST_ASP_NATIVELOGIC);

		Dust.access(MindAccess.Set, hLogic, hNative, DUST_ATT_NATIVELOGIC_LOGIC);
		Dust.access(MindAccess.Set, nativeClassName, hNative, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);

		Dust.access(MindAccess.Set, hNative, hModule, DUST_ATT_MODULE_NATIVELOGICS, KEY_ADD);
		if (srv) {
			Dust.access(MindAccess.Set, true, hNative, MIND_ATT_KNOWLEDGE_TAGS, DUST_TAG_NATIVELOGIC_SERVER);
		}
	}

	public static void setTag(MindHandle hItem, MindHandle hTag, MindHandle hParent) {
		Dust.access(MindAccess.Set, hTag, hItem, MIND_ATT_KNOWLEDGE_TAGS, hParent);
	}

	public static void setTag(MindHandle hItem, MindHandle hTag) {
		setTag(hItem, hTag, hTag);
	}

	public static MindHandle setText(MindHandle hItem, MindHandle hTxtType, MindHandle hLang, String txt) {
		MindHandle hTxt = newHandle(L10N_UNIT, TEXT_ASP_PLAIN);
		
		Dust.access(MindAccess.Set, txt, hItem, TEXT_ATT_PLAIN_TEXT);
		setTag(hItem, hTxtType, TEXT_TAG_TYPE);
		setTag(hItem, hLang, TEXT_TAG_LANGUAGE);
		
		return hTxt;
	}

	public static void breakpoint(Object... objects) {
		Dust.log(EVENT_TAG_TYPE_BREAKPOINT, objects);
	}

	private static ThreadLocal<SimpleDateFormat> TS_FMT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DUST_FMT_TIMESTAMP);
		}
	};

	public static String getTimeStr() {
		return getTimeStr(null);
	};

	public static String getTimeStr(Date d) {
		return TS_FMT.get().format((null == d) ? new Date() : d);
	}

}
