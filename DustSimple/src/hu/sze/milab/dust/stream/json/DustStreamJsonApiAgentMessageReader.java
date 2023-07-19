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
			MindHandle h = resolveDataItem(hMsg);

			DustBrain.dumpHandle("target item ", h);

			if ( null != h ) {
				processContent(h, hMsg, JsonApiMember.attributes);
				processContent(h, hMsg, JsonApiMember.relationships);

				if ( Boolean.TRUE.equals(Dust.access(h, MindAccess.Check, MIND_ASP_DIALOG, MIND_ATT_KNOWLEDGE_PRIMARYASPECT)) ) {
					dialogs.add(h);
				}
			}
			break;
		case Release:
			break;
		}

		return MindStatus.Accept;
	}

	protected MindHandle resolveID(String dataId, MindHandle hType) {
		return Dust.resolveID(dataId, hType);
	}

	protected MindHandle resolveDataItem(Object data) {
		String dataType = Dust.access(data, MindAccess.Peek, null, JsonApiMember.type);
		String dataId = Dust.access(data, MindAccess.Peek, null, JsonApiMember.id);

		MindHandle hType = resolveID(dataType, MIND_ASP_ASPECT);
		MindHandle h = resolveID(dataId, hType);

		return h;
	}

	protected void processContent(MindHandle hTarget, MindHandle hSource, JsonApiMember member) {
		Map m = Dust.access(hSource, MindAccess.Peek, Collections.EMPTY_MAP, member);
		for (Object e : m.entrySet()) {
			Map.Entry entry = (Map.Entry) e;
			String id = (String) entry.getKey();
			MindHandle hMember = Dust.resolveID(id, MIND_ASP_ATTRIBUTE);

			if ( null != hMember ) {
				if ( JsonApiMember.attributes == member ) {
					Dust.access(hTarget, MindAccess.Set, entry.getValue(), hMember);
				} else {
					Object data = Dust.access(entry.getValue(), MindAccess.Peek, null, JsonApiMember.data);
					if ( data instanceof Collection ) {
						for (Object d : (Collection) data) {
							setRelation(hTarget, hMember, d, KEY_ADD);
						}
					} else {
						setRelation(hTarget, hMember, data, null);
					}
				}
			}
		}
	}

	protected void setRelation(MindHandle h, MindHandle hMember, Object data, Object key) {
		MindHandle hVal = resolveDataItem(data);

		if ( null != hVal ) {
			Object refKey = Dust.access(data, MindAccess.Peek, null, JsonApiMember.meta, "refKey");
			if ( null != refKey ) {
				key = resolveDataItem(refKey);
			} else {
				key = Dust.access(data, MindAccess.Peek, key, JsonApiMember.meta, "key");
			}
			
			if ( null == key ) {
				Dust.access(h, MindAccess.Set, hVal, hMember);
			} else {
				Dust.access(h, MindAccess.Set, hVal, hMember, key);
			}
		}
	}
}
