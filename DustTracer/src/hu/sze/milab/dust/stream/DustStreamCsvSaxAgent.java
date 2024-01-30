package hu.sze.milab.dust.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustAgent;
import hu.sze.milab.dust.utils.DustUtils;

@SuppressWarnings("rawtypes")
public class DustStreamCsvSaxAgent extends DustAgent implements DustStreamConsts {

	@Override
	public MindHandle agentBegin() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_PASS;

		Object hData = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, null, RESOURCE_ATT_PROCESSOR_DATA);
		Object hStream = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, null, RESOURCE_ATT_PROCESSOR_STREAM);
		Object current = Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_PEEK, null);

		if ( null != current ) {
			Object s = Dust.access(hStream, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_VARIANT_VALUE);

			if ( s instanceof File ) {
				File f = (File) s;
				
				String sep = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, "\t", RESOURCE_ATT_CSVSAX_SEP);

				FileWriter fw = new FileWriter(f);
				Dust.access(hData, MIND_TAG_ACCESS_SET, fw, MISC_ATT_VARIANT_VALUE);
				
				write(hData, sep, fw, true);
				
				ret = MIND_TAG_RESULT_READACCEPT;
			}
		}

		return ret;
	}

	@Override
	public MindHandle agentProcess() throws Exception {
		Object hData = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, null, RESOURCE_ATT_PROCESSOR_DATA);
		Object hStream = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, null, RESOURCE_ATT_PROCESSOR_STREAM);

		Object current = Dust.access(MIND_TAG_CONTEXT_TARGET, MIND_TAG_ACCESS_PEEK, null);

		if ( null != current ) {
			Object s = Dust.access(hStream, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_VARIANT_VALUE);

			if ( s instanceof File ) {
				File f = (File) s;
				String sep = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, "\t", RESOURCE_ATT_CSVSAX_SEP);

				if ( DustUtils.isEqual(hStream, current) ) {
					if ( f.isFile() ) {
						try (FileReader fr = new FileReader(f)) {
							try ( BufferedReader br = new BufferedReader(fr)) {
							
								String line = br.readLine();
								String[] cols = line.split(sep);
								int len = cols.length;
								
								for ( int i = len; i-->0; ) {
									cols[i] = DustStreamUtils.csvOptUnEscape(cols[i], true);
									Dust.access(hData, MIND_TAG_ACCESS_SET, cols[i], MISC_ATT_CONN_MEMBERARR, i);
								}
								Dust.access(hData, MIND_TAG_ACCESS_COMMIT, MIND_TAG_ACTION_BEGIN);
								
								for ( line = br.readLine(); null != line; ) {
									String[] values = line.split(sep);
									for ( int i = len; i-->0; ) {
										String val = DustStreamUtils.csvOptUnEscape(values[i], true);
										Dust.access(hData, MIND_TAG_ACCESS_SET, val, MISC_ATT_CONN_MEMBERMAP, cols[i]);
									}
									Dust.access(hData, MIND_TAG_ACCESS_COMMIT, MIND_TAG_ACTION_PROCESS);
								}
								
								Dust.access(hData, MIND_TAG_ACCESS_COMMIT, MIND_TAG_ACTION_END);
							}
						}
					}
				} else {
					FileWriter fw = Dust.access(hData, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_VARIANT_VALUE);
					write(hData, sep, fw, false);
				}
			}
		}

		return MIND_TAG_RESULT_READACCEPT;
	}

	public void write(Object hData, String sep, FileWriter fw, boolean head) throws IOException {
		Collection cols = Dust.access(hData, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_CONN_MEMBERARR);
		int l = cols.size();
		int i = 0;
		for (Object ch : cols) {
			String val = head ? DustUtils.toString(ch) : Dust.access(hData, MIND_TAG_ACCESS_PEEK, null, MISC_ATT_CONN_MEMBERMAP, ch);
			val = DustStreamUtils.csvOptEscape(val, sep);
			fw.write(val);
			fw.write( ((++i) < l ) ? sep : "\n");
		}
		fw.flush();
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		MindHandle ret = MIND_TAG_RESULT_PASS;
		
		FileWriter fw = Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_PEEK, null, RESOURCE_ATT_PROCESSOR_DATA, MISC_ATT_VARIANT_VALUE);
		
		if ( null != fw ) {
			fw.flush();
			fw.close();
			Dust.access(MIND_TAG_CONTEXT_SELF, MIND_TAG_ACCESS_SET, null, RESOURCE_ATT_PROCESSOR_DATA, MISC_ATT_VARIANT_VALUE);
			ret = MIND_TAG_RESULT_ACCEPT;
		}

		return ret;
	}

}
