package hu.sze.milab.dust.mvel;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.utils.DustUtils;

public class DustMvelUtils implements DustMvelConsts {
	public static final ParserContext MVEL_PCTX = new ParserContext();
	
	static {
		MVEL_PCTX.addImport(DustUtils.class.getSimpleName(), DustUtils.class);
		MVEL_PCTX.addImport(Dust.class.getSimpleName(), Dust.class);
	}
	
	public static Object compile(String expr) {
		return MVEL.compileExpression(expr, MVEL_PCTX);
	}
	
	public static Object eval(String expr, Object ctx) {
		Object o = compile(expr);
		return MVEL.executeExpression(o, ctx);
	}
	
}
