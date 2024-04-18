package hu.sze.milab.dust.stream;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevProcMon;
import hu.sze.milab.dust.utils.DustUtils;

@SuppressWarnings("rawtypes")
public class DustStreamCsvSaxAgent extends DustAgent implements DustStreamConsts {

	@Override
	protected MindHandle agentBegin() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_PASS;

		Object hData = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA);
		Object hStream = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM);
		Object current = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);

		if ( null != current ) {
			Object s = Dust.access(MindAccess.Peek, null, hStream, DUST_ATT_IMPL_DATA);

			if ( s instanceof FileWriter ) {
				String sep = Dust.access(MindAccess.Peek, "\t", MIND_TAG_CONTEXT_SELF, MISC_ATT_GEN_SEP_ITEM);

				write(hData, sep, (FileWriter) s, true);

				ret = MIND_TAG_RESULT_READACCEPT;
			}
		}

		return ret;
	}

	@Override
	protected MindHandle agentProcess() throws Exception {
		Object hData = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA);
		Object hStream = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM);

		Object current = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_TARGET);

		if ( null != current ) {
			Object s = Dust.access(MindAccess.Peek, null, hStream, DUST_ATT_IMPL_DATA);
			String sep = Dust.access(MindAccess.Peek, "\t", MIND_TAG_CONTEXT_SELF, MISC_ATT_GEN_SEP_ITEM);

			if ( DustUtils.isEqual(hStream, current) ) {
				if ( s instanceof Reader ) {
					try (BufferedReader br = new BufferedReader((Reader) s)) {
						String line = br.readLine();
						String[] cols = line.split(sep);
						int len = cols.length;

						for (int i = len; i-- > 0;) {
							cols[i] = DustStreamUtils.csvOptUnEscape(cols[i], true);
							Dust.access(MindAccess.Set, cols[i], hData, MISC_ATT_CONN_MEMBERARR, i);
						}
						Dust.access(MindAccess.Commit, MIND_TAG_ACTION_BEGIN, hData);
						
						DustDevProcMon pmRead = new DustDevProcMon("CSV line", 100);
						
						for (line = br.readLine(); null != line; line = br.readLine()) {
							String[] values = line.split(sep);
							for (int i = len; i-- > 0;) {
								String val = DustStreamUtils.csvOptUnEscape(values[i], true);
								Dust.access(MindAccess.Set, val, hData, MISC_ATT_CONN_MEMBERMAP, cols[i]);
							}
							Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hData);
							pmRead.step();
						}

						Dust.access(MindAccess.Commit, MIND_TAG_ACTION_END, hData);
					}
				}
			} else {
//				FileWriter fw = Dust.access(hData, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_VARIANT_VALUE);
				write(hData, sep, (FileWriter) s, false);

			}
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	public void write(Object hData, String sep, FileWriter fw, boolean head) throws IOException {
		Collection cols = Dust.access(MindAccess.Peek, null, hData, MISC_ATT_CONN_MEMBERARR);
		int l = cols.size();
		int i = 0;
		for (Object ch : cols) {
			String val = head ? DustUtils.toString(ch) : Dust.access(MindAccess.Peek, null, hData, MISC_ATT_CONN_MEMBERMAP, ch);
			val = DustStreamUtils.csvOptEscape(val, sep);
			fw.write(val);
			fw.write(((++i) < l) ? sep : "\n");
		}
		fw.flush();
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_PASS;

		FileWriter fw = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA, DUST_ATT_IMPL_DATA);

		if ( null != fw ) {
			fw.flush();
			fw.close();
			Dust.access(MindAccess.Set, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_DATA, DUST_ATT_IMPL_DATA);
			ret = MIND_TAG_RESULT_ACCEPT;
		}

		return ret;
	}

}
