package hu.sze.milab.dust.stream.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.utils.DustUtils;

@SuppressWarnings("rawtypes")
public class DustJsonDomAgent extends DustAgent implements DustJsonConsts {

	@Override
	protected MindHandle agentBegin() throws Exception {
		// TODO Auto-generated method stub
		return super.agentBegin();
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		Object hData = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA);
		Object hStream = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM);

		Object current = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);

		if ( null != current ) {
			Object s = Dust.access(MindAccess.Peek, null, hStream, DUST_ATT_IMPL_DATA);
			Object d = Dust.access(MindAccess.Peek, null, hData, MISC_ATT_VARIANT_VALUE);

			if ( s instanceof File ) {
				File f = (File) s;

				if ( DustUtils.isEqual(hData, current) ) {
					try (FileWriter fw = new FileWriter(f)) {
						if ( d instanceof Map ) {
							JSONObject.writeJSONString((Map) d, fw);
						}
						fw.flush();
						fw.close();
					}
				} else {
					if ( f.isFile() ) {
						try (FileReader fr = new FileReader(f)) {
							JSONParser parser = new JSONParser();
							d = parser.parse(fr);
						}
					}

					Dust.access(MindAccess.Set, d, hData, MISC_ATT_VARIANT_VALUE);
				}
			}
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		// TODO Auto-generated method stub
		return super.agentEnd();
	}

}
