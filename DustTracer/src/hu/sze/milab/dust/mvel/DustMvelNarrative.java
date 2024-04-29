package hu.sze.milab.dust.mvel;

import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.stream.DustStreamConsts;
import hu.sze.milab.dust.utils.DustutilsFactory;

@SuppressWarnings({ "rawtypes", "unchecked" })
public interface DustMvelNarrative extends DustMvelConsts {

	static class Evaluator {
		ParserContext pctx;

		DustutilsFactory<String, Object> compExpr = new DustutilsFactory<String, Object>(new DustCreator<Object>() {
			@Override
			public Object create(Object key, Object... hints) {
				return MVEL.compileExpression((String) key, pctx);
			}
		});

		public Evaluator() {
			pctx = new ParserContext();

			Map<Object, Object> stat = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, EXPR_ATT_EXPRESSION_STATIC);

			for (Map.Entry<Object, Object> es : stat.entrySet()) {
				pctx.addImport((String) es.getKey(), (Class) es.getValue());
			}
		}

		public Object compile(String expr) {
			return compExpr.get(expr);
		}

		public <RetType> RetType eval(String expr, Object ctx) {
			Object o = compile(expr);
			return (RetType) MVEL.executeExpression(o, ctx);
		}
	}

	static abstract class ExprAgent extends DustAgent implements DustStreamConsts {

		@Override
		protected final MindHandle agentProcess() throws Exception {
			Evaluator eval = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, DUST_ATT_IMPL_DATA);

			if ( null == eval ) {
				eval = new Evaluator();
				Dust.access(MindAccess.Set, eval, MIND_TAG_CONTEXT_SELF, DUST_ATT_IMPL_DATA);
			}

			return doProcess(eval);
		}

		protected abstract MindHandle doProcess(Evaluator eval) throws Exception;
	}

	public static class PopulateAgent extends ExprAgent implements DustStreamConsts {
		@Override
		protected MindHandle doProcess(Evaluator eval) throws Exception {
			if ( MIND_TAG_ACTION_PROCESS == Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_ACTION) ) {
				Object hTo = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_TARGET);

				Object hFrom = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);
				Object rootAtt = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_GEN_TARGET_ATT);
				Object root = (null == rootAtt) ? hFrom : Dust.access(MindAccess.Peek, null, hFrom, rootAtt);

				Map<Object, Object> transfer = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_MEMBERMAP);
				for (Map.Entry<Object, Object> et : transfer.entrySet()) {
					Object key = et.getKey();
					Object val = et.getValue();

					if ( val instanceof String ) {
						String expr = (String) val;
						val = eval.eval(expr, root);
					} else if ( val instanceof MindHandle ) {
						val = Dust.access(MindAccess.Peek, null, hFrom, val);
					}

					Dust.access(MindAccess.Set, val, hTo, key);
				}

				Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hTo);
			}
			return MIND_TAG_RESULT_READACCEPT;
		}
	}

	public static class FilterAgent extends ExprAgent implements DustStreamConsts {
		@Override
		protected MindHandle doProcess(Evaluator eval) throws Exception {

			boolean ret = true;
			if ( MIND_TAG_ACTION_PROCESS == Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_ACTION) ) {
				Object hFrom = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);
				Object rootAtt = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_GEN_TARGET_ATT);
				Object root = (null == rootAtt) ? hFrom : Dust.access(MindAccess.Peek, null, hFrom, rootAtt);

				String expr = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, EXPR_ATT_EXPRESSION_STR);

				ret = eval.eval(expr, root);
			}
			return ret ? MIND_TAG_RESULT_ACCEPT : MIND_TAG_RESULT_REJECT;
		}
	}
}