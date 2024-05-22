package hu.sze.milab.dust.mvel;

import org.mvel2.MVEL;

@SuppressWarnings("unchecked")
public class DustMvelUtils implements DustMvelConsts {
	public static Object compile(String expr) {
		return MVEL.compileExpression(expr);
	}
	
	public static <RetType> RetType evalCompiled(Object o , Object ctx) {
		return (RetType) MVEL.executeExpression(o, ctx);
	}
}
