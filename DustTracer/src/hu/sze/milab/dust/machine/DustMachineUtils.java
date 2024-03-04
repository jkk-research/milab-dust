package hu.sze.milab.dust.machine;

import java.io.File;

import hu.sze.milab.dust.utils.DustUtilsFile;

public class DustMachineUtils implements DustMachineConsts {

	private static final File MODULE_DIR = new File("work/json/");

	public static File getUnitFile(MindHandle unit) throws Exception {
		String[] ids = unit.getId().split(DUST_SEP_ID);

		File dir = new File(MODULE_DIR, ids[0]);
		DustUtilsFile.ensureDir(dir);

		File f = new File(dir, ids[1] + DUST_EXT_JSON);

		return f;
	}

}
