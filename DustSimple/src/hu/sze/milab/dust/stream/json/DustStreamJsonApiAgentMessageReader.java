package hu.sze.milab.dust.stream.json;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.brain.DustBrain;

@SuppressWarnings("rawtypes")
public class DustStreamJsonApiAgentMessageReader implements DustStreamJsonConsts, DustConsts.MindAgent {

	Set<MindHandle> dialogs = new HashSet<>();

	@Override
	public MindStatus agentExecAction(MindAction action) throws Exception {
		switch ( action ) {
		case Begin:
			break;
		case End:
			for (MindHandle d : dialogs) {
				Dust.access(d, MindAccess.Commit, MindAction.Process);
			}
			break;
		case Init:
			break;
		case Process:
			MindHandle hMsg = Dust.access(MindContext.Message, MindAccess.Peek, null);
			DustBrain.dumpHandle(action.toString(), hMsg);
			String str = Dust.access(hMsg, MindAccess.Peek, null, JsonApiMember.type);
			MindHandle hType = Dust.resolveID(str, null);
			str = Dust.access(hMsg, MindAccess.Peek, null, JsonApiMember.id);

			MindHandle h = Dust.resolveID(str, hType);

			DustBrain.dumpHandle("target item ", h);

			if ( null != h ) {
				if ( hType == MIND_ASP_DIALOG ) {
					dialogs.add(h);
				}

				Map m = Dust.access(hMsg, MindAccess.Peek, Collections.EMPTY_MAP, JsonApiMember.attributes);
				for (Object e : m.entrySet()) {
					Map.Entry entry = (Map.Entry) e;
					String id = (String) entry.getKey();
					MindHandle hMember = Dust.resolveID(id, null);

					if ( null != hMember ) {
						Dust.access(h, MindAccess.Set, entry.getValue(), hMember);
					}
				}

				m = Dust.access(hMsg, MindAccess.Peek, Collections.EMPTY_MAP, JsonApiMember.relationships);
				for (Object e : m.entrySet()) {
					Map.Entry entry = (Map.Entry) e;
					String id = (String) entry.getKey();
					MindHandle hMember = Dust.resolveID(id, null);

					if ( null != hMember ) {
						Object data = Dust.access(entry.getValue(), MindAccess.Peek, null, JsonApiMember.data);
						if ( data instanceof Collection ) {
							for (Object d : (Collection) data) {
								loadRelation(h, hMember, d, KEY_ADD);
							}
						} else {
							loadRelation(h, hMember, data, null);
						}
					}
				}
			}
			break;
		case Release:
			break;
		}

		return MindStatus.Accept;
	}

	public void loadRelation(MindHandle h, MindHandle hMember, Object data, Object key) {
		String id = Dust.access(data, MindAccess.Peek, null, JsonApiMember.id);
		String type = Dust.access(data, MindAccess.Peek, null, JsonApiMember.type);
		MindHandle hAsp = Dust.resolveID(type, MIND_ASP_ASPECT);
		MindHandle hVal = Dust.resolveID(id, hAsp);

		if ( null != hVal ) {
			Object mk = Dust.access(data, MindAccess.Peek, key, JsonApiMember.meta, "key");

			if ( null == mk ) {
				Dust.access(h, MindAccess.Set, hVal, hMember);
			} else {
				Dust.access(h, MindAccess.Set, hVal, hMember, mk);
			}
		}
	}

}
