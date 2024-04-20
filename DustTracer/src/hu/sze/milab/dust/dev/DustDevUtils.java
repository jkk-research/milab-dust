package hu.sze.milab.dust.dev;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustHandles;
import hu.sze.milab.dust.utils.DustUtils;

public class DustDevUtils implements DustHandles {

	public static final Comparator<String> ID_COMP = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			if ( o1.equals(o2) ) {
				return 0;
			}

			String[] s1 = o1.split(DUST_SEP_ID);
			String[] s2 = o2.split(DUST_SEP_ID);

			int ret = s1[0].compareTo(s2[0]);
			int m1 = s1.length - 1;
			int m2 = s2.length - 1;

			for (int i = 1; (0 == ret) && (i < 3); ++i) {
				if ( i > m1 ) {
					ret = -1;
				} else if ( i > m2 ) {
					ret = 1;
				} else {
					ret = Integer.valueOf(s1[i]) - Integer.valueOf(s2[i]);
				}
			}

			return ret;
		}
	};

	public static MindHandle newHandle(MindHandle hUnit, MindHandle hPrimaryAspect, String hint) {
		return newHandle(hUnit.getId(), hPrimaryAspect, hint);
	};

	public static MindHandle newHandle(String unit, MindHandle hPrimaryAspect, String hint) {
		MindHandle h = Dust.lookup(unit + DUST_SEP_ID + ITEMID_NEW);

		Dust.access(MindAccess.Set, hPrimaryAspect, h, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		if ( !DustUtils.isEmpty(hint) ) {
			Dust.access(MindAccess.Set, hint, h, DEV_ATT_HINT);
		}
		
		return h;
	};

	public static MindHandle registerAgent(MindHandle hUnit, MindHandle hLogic) {
		return registerAgent(hUnit, hLogic, hLogic.getId());
	};

	public static MindHandle registerAgent(MindHandle hUnit, MindHandle hLogic, String hint) {
		MindHandle hAgent = newHandle(hUnit, MIND_ASP_AGENT, hint);

		Dust.access(MindAccess.Set, hLogic, hAgent, MIND_ATT_AGENT_NARRATIVE);

		return hAgent;
	};

	public static MindHandle registerLogic(MindHandle hUnit, String nativeClassName, String hint) {
		MindHandle hLogic = newHandle(hUnit, MIND_ASP_NARRATIVE, hint + "_logic");
		MindHandle hNative = newHandle(hUnit, DUST_ASP_IMPL, hint + "_impl");

		Dust.access(MindAccess.Set, hLogic, hNative, DUST_ATT_IMPL_NARRATIVE);
		Dust.access(MindAccess.Set, nativeClassName, hNative, TEXT_ATT_TOKEN);

		Dust.access(MindAccess.Set, hNative, APP_MODULE_MAIN, DUST_ATT_MODULE_NARRATIVEIMPLS, KEY_ADD);

		return hLogic;
	}

	public static void registerNative(MindHandle hLogic, MindHandle hUnit, MindHandle hModule, String nativeClassName) {
		registerNative(hLogic, hUnit, hModule, nativeClassName, false);
	}

	public static void registerNative(MindHandle hLogic, MindHandle hUnit, MindHandle hModule, String nativeClassName, boolean srv) {
		MindHandle hNative = newHandle(hUnit.getId(), DUST_ASP_IMPL, DustUtils.getPostfix(nativeClassName, "."));

		Dust.access(MindAccess.Set, hLogic, hNative, DUST_ATT_IMPL_NARRATIVE);
		Dust.access(MindAccess.Set, nativeClassName, hNative, TEXT_ATT_TOKEN);

		Dust.access(MindAccess.Set, hNative, hModule, DUST_ATT_MODULE_NARRATIVEIMPLS, KEY_ADD);
		if ( srv ) {
			setTag(hNative, DUST_TAG_NATIVE_SERVER);
		}
	}

	public static void setTag(MindHandle hItem, MindHandle hTag, MindHandle hParent) {
		Dust.access(MindAccess.Set, hTag, hItem, MIND_ATT_KNOWLEDGE_TAGS, hParent);
	}

	public static void setTag(MindHandle hItem, MindHandle hTag) {
		setTag(hItem, hTag, hTag);
	}

	public static boolean chkTag(MindHandle hItem, MindHandle hTag, MindHandle hParent) {
		return (boolean) Dust.access(MindAccess.Check, hTag, hItem, MIND_ATT_KNOWLEDGE_TAGS, hParent);
	}

	public static boolean chkTag(MindHandle hItem, MindHandle hTag) {
		return chkTag(hItem, hTag, hTag);
	}

	public static MindHandle setText(MindHandle hItem, MindHandle hTxtType, MindHandle hLang, String txt) {
		MindHandle hTxt = newHandle(L10N_UNIT, TEXT_ASP_PLAIN, null);

		Dust.access(MindAccess.Set, txt, hTxt, TEXT_ATT_PLAIN_TEXT);
		Dust.access(MindAccess.Set, hItem, hTxt, MISC_ATT_CONN_OWNER);
		setTag(hTxt, hTxtType, TEXT_TAG_TYPE);
		setTag(hTxt, hLang, TEXT_TAG_LANGUAGE);

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
