package hu.sze.milab.dust.stream.json;

import java.io.PrintStream;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.utils.DustUtils;

public class DustStreamJsonAgentWriter implements DustMetaConsts, DustStreamJsonConsts, DustConsts.MindAgent {

	public MindHandle hTarget;
	public PrintStream ps;

	private String indent = "  ";
	private StringBuilder rowPrefix = new StringBuilder();

	private boolean first;

	private void optCloseElement() {
		if ( first ) {
			first = false;
		} else {
			ps.println(",");
			ps.print(rowPrefix);
		}
	}

	private void indent(boolean up) {
		if ( up ) {
			rowPrefix.append(indent);
		} else {
			rowPrefix.setLength(rowPrefix.length() - indent.length());
		}
	}

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		MindColl coll = Dust.access(hTarget, MindAccess.Peek, MindColl.One, MIND_ATT_KNOWLEDGE_TAG);
		String line = null;

		switch ( action ) {
		case Init:
			first = true;
			break;
		case Release:
			ps.println();
			break;
		case Begin:
			optCloseElement();
			
			switch ( coll ) {
			case One:
				line = "\"" + Dust.access(hTarget, MindAccess.Peek, MindColl.One, TEXT_ATT_NAMED_NAME) + "\": ";
				break;
			case Arr:
			case Set:
				line = "[\n";
				indent(true);
				line += rowPrefix;
				break;
			case Map:
				line = "{\n";
				indent(true);
				line += rowPrefix;
				break;
			}
			first = true;
			
			ps.print(line);

			break;
		case Process:
			optCloseElement();
			Object val = Dust.access(hTarget, MindAccess.Peek, null, MISC_ATT_VARIANT_VALUE);

			if ( null == val ) {
				line = JSONCONST_NULL;
			} else if ( val instanceof Number ) {
				line = ((Number)val).toString();
			} else if (val instanceof Boolean) {
				line = ((Boolean)val) ? JSONCONST_TRUE : JSONCONST_FALSE;
			} else {
				line  = DustUtils.toString(val);
				line = "\"" + JSONValue.escape(line).replaceAll("\\\\/", "/") + "\"";
			}
			
			ps.print(line);
			break;
		case End:
			switch ( coll ) {
			case One:
				break;
			case Arr:
			case Set:
				line = "]";
				break;
			case Map:
				line = "}";
				break;
			}
			
			if ( null != line ) {
				indent(false);
				ps.print("\n" + rowPrefix + line);
			}
			break;
		default:
			break;
		}
		
		return MindStatus.ReadAccept;
	}
}
