package hu.sze.milab.dust.dev;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;

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

	public static MindHandle registerHandle(String unit, MindHandle hPrimaryAspect) {
		MindHandle h = Dust.lookup(unit + DUST_SEP_ID);

		Dust.access(MindAccess.Set, hPrimaryAspect, h, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		return h;
	};

	public static MindHandle registerHandle(MindHandle hUnit, MindHandle hPrimaryAspect) {
		MindHandle h = Dust.lookup(hUnit, "");

		Dust.access(MindAccess.Set, hPrimaryAspect, h, MIND_ATT_KNOWLEDGE_PRIMARYASPECT);

		return h;
	};

	public static MindHandle registerAgent(String unit, MindHandle hLogic) {
		MindHandle hAgent = registerHandle(unit, MIND_ASP_AGENT);

		Dust.access(MindAccess.Set, hLogic, hAgent, MIND_ATT_AGENT_LOGIC);

		return hAgent;
	};

	public static MindHandle registerLogic(String unit, String nativeClassName) {
		MindHandle hLogic = registerHandle(unit, MIND_ASP_LOGIC);
		MindHandle hNative = registerHandle(unit, DUST_ASP_NATIVELOGIC);

		Dust.access(MindAccess.Set, hLogic, hNative, DUST_ATT_NATIVELOGIC_LOGIC);
		Dust.access(MindAccess.Set, nativeClassName, hNative, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);

		Dust.access(MindAccess.Set, hNative, APP_MODULE_MAIN, DUST_ATT_MODULE_NATIVELOGICS, KEY_ADD);

		return hLogic;
	}

	public static MindHandle registerNative(MindHandle hLogic, MindHandle hUnit, String nativeClassName) {
		return registerNative(hLogic, hUnit, nativeClassName, false);
	}

	public static MindHandle registerNative(MindHandle hLogic, MindHandle hUnit, String nativeClassName, boolean srv) {
		MindHandle hNative = registerHandle(hUnit, DUST_ASP_NATIVELOGIC);

		Dust.access(MindAccess.Set, hLogic, hNative, DUST_ATT_NATIVELOGIC_LOGIC);
		Dust.access(MindAccess.Set, nativeClassName, hNative, DUST_ATT_NATIVELOGIC_IMPLEMENTATION);

		Dust.access(MindAccess.Set, hNative, APP_MODULE_MAIN, DUST_ATT_MODULE_NATIVELOGICS, KEY_ADD);
		if ( srv ) {
			Dust.access(MindAccess.Set, true, hNative, MIND_ATT_KNOWLEDGE_TAGS, DUST_TAG_NATIVELOGIC_SERVER);
		}

		return hLogic;
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