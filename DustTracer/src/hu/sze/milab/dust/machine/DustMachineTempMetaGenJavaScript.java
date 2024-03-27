package hu.sze.milab.dust.machine;

import java.text.MessageFormat;

import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.stream.json.DustJsonConsts;

public class DustMachineTempMetaGenJavaScript extends DustMachineTempSrcGen implements DustJsonConsts {

	private static final String IF_FMT_BEGIN = "\n"
			+ "// Generated: {1}\n"
			+ "if (!(\"{0}\" in window)) '{'\n"
			+ "\t{0} = '{'\n"
			+ "";
	private static final String IF_FMT_LINE = "\t\t{0} : \"{1}\",";
	private static final String IF_END = "	};\n}\n";

	String scriptName;

	public DustMachineTempMetaGenJavaScript(String srcDir, String scriptName, MindHandle... paToWrite) {
		super(paToWrite);
		
		this.scriptName = scriptName;
		
		init(srcDir, "", scriptName + DUST_EXT_JS);
	}

	@Override
	protected String getSrcLead() {
		return MessageFormat.format(IF_FMT_BEGIN, scriptName, DustDevUtils.getTimeStr());
	}

	@Override
	protected String formatItem(String name, String id) {
		return  MessageFormat.format(IF_FMT_LINE, name, id.replace(DUST_SEP_ID, JSONAPI_IDSEP));
	}

	@Override
	protected String getSrcTail() {
		return IF_END;
	}

}
