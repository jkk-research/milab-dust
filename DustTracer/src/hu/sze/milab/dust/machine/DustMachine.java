package hu.sze.milab.dust.machine;

import hu.sze.milab.dust.Dust;
import hu.sze.milab.dust.DustConsts;
import hu.sze.milab.dust.utils.DustUtils;

//@SuppressWarnings({ "rawtypes", "unchecked" })
class DustMachine extends Dust.Machine implements DustMachineConsts, DustConsts.MindAgent {

	Dust.IdResolver idRes;
	final DustMachineDialog mainDialog;

	public DustMachine() {
		mainDialog = new DustMachineDialog();
	}

	@Override
	public DustHandle recall(String id) {
		return (DustHandle) idRes.recall(id);
	}

	@Override
	protected <RetType> RetType access(Object root, MindHandle cmd, Object val, Object... path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void log(MindHandle event, Object... params) {
		StringBuilder sb = DustUtils.sbAppend(null, ", ", false, params);

		if ( null != sb ) {
			System.out.println(sb);
		}
	}

	@Override
	public MindHandle agentInit() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentBegin() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentProcess() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentEnd() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MindHandle agentRelease() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
