package hu.sze.milab.dust.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.dev.DustDevUtils;
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

			if ( null == s ) {
				Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hStream);
				s = Dust.access(MindAccess.Peek, null, hStream, DUST_ATT_IMPL_DATA);
			}

			if ( s instanceof Writer ) {
				String sep = Dust.access(MindAccess.Peek, "\t", MIND_TAG_CONTEXT_SELF, MISC_ATT_GEN_SEP_ITEM);

				write(hData, sep, (Writer) s, true);

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
					ArrayList<String> items = new ArrayList<>();
					DustStreamUtils.CsvLineReader lineReader = new DustStreamUtils.CsvLineReader(sep, items);

					ArrayList<String> cols = null;
					try (BufferedReader br = new BufferedReader((Reader) s)) {
						for (String line = br.readLine(); null != line; line = br.readLine()) {
							if ( !lineReader.csvReadLine(line) ) {
								continue;
							}

							if ( null == cols ) {
								cols = new ArrayList<>(items.size());
								Dust.access(MindAccess.Reset, null, hData, MISC_ATT_CONN_MEMBERARR);
								for (String i : items) {
									String col = i.trim();
									cols.add(col);
									Dust.access(MindAccess.Insert, col, hData, MISC_ATT_CONN_MEMBERARR, KEY_ADD);
								}
								Dust.access(MindAccess.Commit, MIND_TAG_ACTION_BEGIN, hData);
							} else {
								Dust.access(MindAccess.Reset, null, hData, MISC_ATT_CONN_MEMBERMAP);
								for (int i = items.size(); i-- > 0;) {
									Dust.access(MindAccess.Set, items.get(i).trim(), hData, MISC_ATT_CONN_MEMBERMAP, cols.get(i));
								}
								Dust.access(MindAccess.Commit, MIND_TAG_ACTION_PROCESS, hData);
							}
							
							items.clear();
						}

						Dust.access(MindAccess.Commit, MIND_TAG_ACTION_END, hData);
					}
				}
			} else {
				write(hData, sep, (Writer) s, false);
			}
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	public void write(Object hData, String sep, Writer fw, boolean head) throws IOException {
		Collection cols = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, MISC_ATT_CONN_MEMBERARR);
		if ( null == cols ) {
			cols = Dust.access(MindAccess.Peek, null, hData, MISC_ATT_CONN_MEMBERARR);
		}

		boolean test = DustDevUtils.chkTag(MIND_TAG_CONTEXT_SELF, DEV_TAG_TEST);
		if ( test ) {
			fw = new StringWriter();
		}

		int l = cols.size();
		int i = 0;
		for (Object ch : cols) {
			String val = head ? DustUtils.toString(ch) : Dust.access(MindAccess.Peek, null, hData, MISC_ATT_CONN_MEMBERMAP, ch);
			val = DustStreamUtils.csvOptEscape(val, sep);
			fw.write(val);
			fw.write(((++i) < l) ? sep : "\n");
		}
		fw.flush();

		if ( test ) {
			System.out.println(((StringWriter) fw).toString());
		}
	}

	@Override
	protected MindHandle agentEnd() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_PASS;

		Object streamOb = Dust.access(MindAccess.Peek, null, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM, DUST_ATT_IMPL_DATA);

		if ( null != streamOb ) {
			Dust.access(MindAccess.Commit, MIND_TAG_ACTION_END, MIND_TAG_CONTEXT_SELF, RESOURCE_ATT_PROCESSOR_STREAM);
		}

		return ret;
	}

}
