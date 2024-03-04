package hu.sze.milab.dust;

import java.io.PrintStream;

import hu.sze.milab.dust.utils.DustUtils;

public final class DustException extends RuntimeException implements DustHandles {
	private static final long serialVersionUID = 1L;

	static PrintStream DUMP_STACK_TRACE = System.err;

	private DustException(Throwable src, Object... params) {
		super(DustUtils.sbAppend(null, ",", false, params).toString(), src);

		log(true, src, getMessage());
	}

	public static void swallow(Throwable src, Object... params) {
		log(true, src, DustUtils.sbAppend(null, ",", false, params).toString());
	}

	public static <FakeRet> FakeRet wrap(Throwable src, Object... params) {
		if ( src instanceof DustException ) {
			throw (DustException) src;
		}

		throw new DustException(src, params);
	}

	private static void log(boolean thrown, Throwable src, String msg) {
		Dust.log(thrown ? EVENT_TAG_TYPE_EXCEPTIONTHROWN : EVENT_TAG_TYPE_EXCEPTIONSWALLOWED, src, msg);

		if ( (null != DUMP_STACK_TRACE) && (null != src) ) {
			src.printStackTrace(DUMP_STACK_TRACE);
		}
	}
}