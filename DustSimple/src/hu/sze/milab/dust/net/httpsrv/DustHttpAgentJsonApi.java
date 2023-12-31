package hu.sze.milab.dust.net.httpsrv;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.stream.DustStreamUtils;
import hu.sze.milab.dust.stream.json.DustStreamJsonConsts;

public class DustHttpAgentJsonApi implements DustStreamJsonConsts, DustNetConsts, DustMetaConsts, DustConsts.MindAgent {
	
	private static final DustStreamUtils.TempFileFactory TTF = new DustStreamUtils.TempFileFactory("jsonOutQueue");
	
	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		switch ( action ) {
		case Begin:
			break;
		case End:
			break;
		case Init:
			break;
		case Process:
			HttpServletResponse response = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, NET_ATT_SRVCALL_RESPONSE);
			Object src = Dust.access(MindContext.Self, MindAccess.Peek, null, MISC_ATT_CONN_TARGET);

			if ( (null != src) && (null != response) ) {

				String prefix = Dust.access(MindContext.Self, MindAccess.Peek, null, TEXT_ATT_NAME);
				String path = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, NET_ATT_SRVCALL_PATHINFO);

				if ( path.startsWith(prefix) ) {
					path = path.substring(prefix.length() + 1);
				}

				Map<String, String> params = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, NET_ATT_SRVCALL_PAYLOAD);

				if ( null != params ) {
					Pattern pt = Pattern.compile("(?<key>\\w*)(\\[(?<par>.*)\\])?");

					for (Map.Entry<String, String> pe : params.entrySet()) {
						Matcher m = pt.matcher(pe.getKey());
						if ( m.matches() ) {
							String key = m.group("key");
							String val = pe.getValue();
							boolean add = false;

							switch ( key ) {
							case "include":
								for (String ii : val.split(",")) {
									Dust.access(MindContext.LocalCtx, MindAccess.Insert, ii.trim(), JsonApiMember.jsonapi, key, KEY_ADD);
								}
								break;
							case "filter":
							case "fields":
							case "page":
								add = true;
								break;
							}
							
							if ( add ) {
								String par = m.group("par");
								Dust.access(MindContext.LocalCtx, MindAccess.Set, val, JsonApiMember.jsonapi, key, (null == par) ? "" : par);
							}
						}
					}
				}

				response.setContentType(MEDIATYPE_JSONAPI);
//				PrintWriter out = response.getWriter();
				ServletOutputStream out = response.getOutputStream();

				out.println("{\n");
				Map<String, Object> fragment = new HashMap<>();
				
				for ( JsonApiMember hm : JsonApiMember.HEADER ) {
					Object hVal = Dust.access(src, MindAccess.Peek, null, MISC_ATT_CUSTOM, JsonApiMember.jsonapi + ":" + hm.name() );
					if ( null != hVal ) {
						Dust.access(fragment, MindAccess.Set, hVal, hm);
					}
				}

				if ( !fragment.isEmpty() ) {
					String str = JSONValue.toJSONString(fragment);
					str = str.replace("\\/", "/");
					out.print("\n   \"jsonapi\" : ");
					out.print(str);
					fragment.clear();
				}
				
				try (DustStreamUtils.PrintWriterProvider pwp = new DustStreamUtils.PrintWriterProvider(TTF, EXT_JSON) ){
					Dust.access(MindContext.LocalCtx, MindAccess.Set, pwp, JsonApiMember.jsonapi, STREAM_ATT_STREAM_PROVIDER);
					
					Dust.access(src, MindAccess.Commit, MindAction.Process);
					
					File f = pwp.getFile(JsonApiMember.data);
					if ( null != f ) {
						out.println(",\n   \"data\" : [");
						Files.copy(f.toPath(), out);
						out.print("\n   ]");
						out.flush();
					}
					
					f = pwp.getFile(JsonApiMember.included);
					if ( null != f ) {
						out.println(",\n   \"included\" : [");
						Files.copy(f.toPath(), out);
						out.print("\n   ]");
						out.flush();
					}
					
					Object cnt = Dust.access(MindContext.LocalCtx, MindAccess.Peek, null, JsonApiMember.jsonapi, MISC_ATT_COUNT);
					if ( null != cnt ) {
						Dust.access(fragment, MindAccess.Set, cnt, JsonApiMember.count);
						out.print(",\n   \"meta\" : ");
						String str = JSONValue.toJSONString(fragment);
						out.print(str);
						fragment.clear();
					}
					
				} catch ( Throwable e ) {
					Dust.access(fragment, MindAccess.Set, e.toString(), JsonApiMember.errors, JsonApiMember.title);
					for ( StackTraceElement ste: e.getStackTrace() ) {
						Dust.access(fragment, MindAccess.Insert, ste.toString(), JsonApiMember.errors, JsonApiMember.detail, KEY_ADD);
					}
					String str = JSONValue.toJSONString(fragment);
					out.print(str);
					fragment.clear();
				}
				
				out.println("\n}\n");

			}
			break;
		case Release:
			break;
		}

		return MindStatus.Accept;
	}

}
