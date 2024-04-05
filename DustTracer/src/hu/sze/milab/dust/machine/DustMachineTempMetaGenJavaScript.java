package hu.sze.milab.dust.machine;

import java.text.MessageFormat;
import java.util.Map;

import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.stream.json.DustJsonConsts;
import hu.sze.milab.dust.utils.DustUtils;

public class DustMachineTempMetaGenJavaScript extends DustMachineTempSrcGen implements DustJsonConsts {

	private static final String FMT_BEGIN = "\n"
			+ "// Generated: {1}\n"
			+ "if (!(\"{0}\" in window)) '{'\n"
			+ "\t{0} = '{'\n"
			+ "";
	private static final String FMT_LINE = "\t\t{0} : \"{1}\",";
	private static final String END = "	};\n}\n";

	String scriptName;

	public DustMachineTempMetaGenJavaScript(String srcDir, String scriptName, MindHandle... paToWrite) {
		super(paToWrite);
		
		this.scriptName = scriptName;
		
		init(srcDir, "", scriptName + DUST_EXT_JS);
	}

	@Override
	protected String getSrcLead() {
		return fmtLead(scriptName);
	}

	@Override
	protected String formatItem(String name, String id) {
		return fmtItem(name, id);
	}

	@Override
	protected String getSrcTail() {
		return END;
	}

	public static String fmtLead(String scriptName) {
		return MessageFormat.format(FMT_BEGIN, scriptName, DustDevUtils.getTimeStr());
	}

	public static String fmtItem(String name, String id) {
		return  MessageFormat.format(FMT_LINE, name, id.replace(DUST_SEP_ID, JSONAPI_IDSEP));
	}

	public static String genJSObject(String script, Map<String, MindHandle> members ) {
		String obName = DustUtils.cutPostfix(script, ".");
		obName = DustUtils.getPostfix(obName, "/");
		StringBuilder sbRet = new StringBuilder(fmtLead(obName));
		
		for (Map.Entry<String, MindHandle> m : members.entrySet() ) {
			DustUtils.sbAppend(sbRet, "\n", true, fmtItem(m.getKey(), m.getValue().getId()));
		}
		DustUtils.sbAppend(sbRet, "\n", true, END);
		
		return sbRet.toString();
	}
}
