package hu.sze.milab.dust.machine;

import java.text.MessageFormat;

import hu.sze.milab.dust.dev.DustDevUtils;
import hu.sze.milab.dust.utils.DustUtils;

public class DustMachineTempMetaGenJava extends DustMachineTempSrcGen {

	private static final String IF_FMT_BEGIN = "package {0};\n" + "\n"
			+ "public interface {1} extends DustConsts '{'\n\t// Generated: {2}\n";
	private static final String IF_FMT_LINE = "	MindHandle {0} = Dust.lookup(\"{1}\");";
	private static final String IF_END = "\n}";

	String packageName;
	String interfaceName;

	public DustMachineTempMetaGenJava(String srcDir, String interfaceCanonicalName, MindHandle... paToWrite) {
		super(paToWrite);
		
		this.packageName = DustUtils.cutPostfix(interfaceCanonicalName, ".");
		this.interfaceName = DustUtils.getPostfix(interfaceCanonicalName, ".");
		
		init(srcDir, packageName.replace(".", "/"), interfaceName + DUST_EXT_JAVA);
	}

	@Override
	protected String getSrcLead() {
		return MessageFormat.format(IF_FMT_BEGIN, packageName, interfaceName, DustDevUtils.getTimeStr());
	}

	@Override
	protected String formatItem(String name, String id) {
		return  MessageFormat.format(IF_FMT_LINE, name, id);
	}

	@Override
	protected String getSrcTail() {
		return IF_END;
	}

}
