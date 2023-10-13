package hu.sze.milab.dust.brain;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustMetaConsts;
import hu.sze.milab.dust.dev.DustDevAgentDump;
import hu.sze.milab.dust.dev.DustDevConsts;
import hu.sze.milab.dust.net.DustNetConsts;
import hu.sze.milab.dust.net.httpsrv.DustHttpAgentDirectFile;
import hu.sze.milab.dust.net.httpsrv.DustHttpAgentJsonApi;
import hu.sze.milab.dust.net.httpsrv.DustHttpServerJetty;
import hu.sze.milab.dust.stream.DustStreamConsts;
import hu.sze.milab.dust.stream.json.DustStreamJsonApiAgentMessageReader;
import hu.sze.milab.dust.stream.json.DustStreamJsonAgentParser;
import hu.sze.milab.dust.stream.json.DustStreamJsonApiAgentSerializer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DustBrainUtils implements DustBrainConsts, DustStreamConsts, DustDevConsts, DustNetConsts {
	DustBrain brain;

	public void loadConstsFrom(Class constClass) {
		Map<DustBrainHandle, String> handleToName = new HashMap<>();

		for (Field f : constClass.getDeclaredFields()) {
			try {
				Object bh = f.get(null);
				if ( bh instanceof MindHandle ) {
					String name = f.getName();
					handleToName.put((DustBrainHandle) bh, name);
					String[] nameParts = name.split(STR_IDSEP);

					if ( 2 == nameParts.length ) {
						String unitID = PREFIX_MILAB + nameParts[0];
						brain.access(DustBrain.brainRoot, MindAccess.Set, bh, DUST_ATT_BRAIN_UNITS, unitID);
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		for (Map.Entry<DustBrainHandle, String> ke : handleToName.entrySet()) {
			DustBrainHandle bh = ke.getKey();
			String name = ke.getValue();
			Map k = DustBrain.resolveKnowledge(bh, true);

			k.put(TEXT_ATT_NAMED_NAME, name);

			String[] nameParts = name.split(STR_IDSEP);

			String unitID = PREFIX_MILAB + nameParts[0];
			MindHandle hUnit = brain.access(DustBrain.brainRoot, MindAccess.Peek, null, DUST_ATT_BRAIN_UNITS, unitID);
			k.put(MIND_ATT_KNOWLEDGE_UNIT, hUnit);

			String itemName = (nameParts.length > 2) ? name.substring(nameParts[0].length() + nameParts[1].length() + 2) : null;

			MindHandle hAsp = null;
			switch ( nameParts[1] ) {
			case STR_ASPID_UNIT:
				hAsp = MIND_ASP_UNIT;
				break;
			case STR_ASPID_ASP:
				hAsp = MIND_ASP_ASPECT;
				break;
			case STR_ASPID_ATT:
				hAsp = MIND_ASP_ATTRIBUTE;
				break;
			case STR_ASPID_TAG:
				hAsp = MIND_ASP_TAG;
			case STR_ASPID_LOG:
				hAsp = MIND_ASP_LOGIC;
				break;
			}

			if ( null != hAsp ) {
				k.put(MIND_ATT_KNOWLEDGE_PRIMARYASPECT, hAsp);
			}

			if ( null != itemName ) {
				brain.access(DustBrain.brainRoot, MindAccess.Set, bh, DUST_ATT_BRAIN_UNITS, unitID, MISC_ATT_CONN_MEMBERMAP, itemName);
			}
		}
	}

	void initBrain(DustBrain dustBrain) {
		this.brain = dustBrain;

		loadConstsFrom(DustMetaConsts.class);
		loadConstsFrom(DustDevConsts.class);
		loadConstsFrom(DustStreamConsts.class);
		loadConstsFrom(DustNetConsts.class);
		
				
		brain.access(DEV_LOG_DUMP, MindAccess.Set, DustDevAgentDump.class.getCanonicalName(), DUST_ATT_NATIVE_IMPLEMENTATION);

		brain.access(STREAM_LOG_JSONPARSER, MindAccess.Set, DustStreamJsonAgentParser.class.getCanonicalName(), DUST_ATT_NATIVE_IMPLEMENTATION);
		brain.access(STREAM_LOG_JSONAPISERIALIZER, MindAccess.Set, DustStreamJsonApiAgentSerializer.class.getCanonicalName(), DUST_ATT_NATIVE_IMPLEMENTATION);
		brain.access(STREAM_LOG_JSONAPIREADER, MindAccess.Set, DustStreamJsonApiAgentMessageReader.class.getCanonicalName(), DUST_ATT_NATIVE_IMPLEMENTATION);
		
		brain.access(NET_LOG_SRVJETTY, MindAccess.Set, DustHttpServerJetty.class.getCanonicalName(), DUST_ATT_NATIVE_IMPLEMENTATION);
		brain.access(NET_LOG_SVCFILES, MindAccess.Set, DustHttpAgentDirectFile.class.getCanonicalName(), DUST_ATT_NATIVE_IMPLEMENTATION);
		brain.access(NET_LOG_SVCJSONAPI, MindAccess.Set, DustHttpAgentJsonApi.class.getCanonicalName(), DUST_ATT_NATIVE_IMPLEMENTATION);
		
	}

	public void loadConfigs() throws Exception {
		MindHandle hRead = null;

		String cfgFile;
		for (int i = 0; null != (cfgFile = brain.access(MindContext.Dialog, MindAccess.Peek, null, MIND_ATT_DIALOG_LAUNCHPARAMS, i)); ++i) {

			File fIn = new File(cfgFile);
			
			if ( !fIn.isFile() ) {
				Dust.dump(" ", false, "Config file not found", fIn.getCanonicalPath());
				continue;
			}

			if ( null == hRead ) {
				hRead = Dust.resolveID(null, null);
				Dust.access(hRead, MindAccess.Set, STREAM_LOG_JSONAPISERIALIZER, MIND_ATT_AGENT_LOGIC);
				Dust.access(hRead, MindAccess.Set, hRead, MIND_ATT_KNOWLEDGE_LISTENERS);

				MindHandle target = Dust.resolveID(null, null);
				Dust.access(hRead, MindAccess.Set, target, MISC_ATT_CONN_TARGET);

				MindHandle listener = Dust.resolveID(null, null);
				Dust.access(listener, MindAccess.Set, STREAM_LOG_JSONAPIREADER, MIND_ATT_AGENT_LOGIC);
				Dust.access(target, MindAccess.Set, listener, MIND_ATT_KNOWLEDGE_LISTENERS);
			}
			
			Dust.access(hRead, MindAccess.Set, fIn, STREAM_ATT_STREAM_FILE);
			Dust.access(hRead, MindAccess.Commit, MindAction.Process);
		}

	}
}
