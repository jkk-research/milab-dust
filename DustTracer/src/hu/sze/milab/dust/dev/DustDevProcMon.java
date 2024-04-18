package hu.sze.milab.dust.dev;

public class DustDevProcMon {
	private String name;
	private long segment;

	private long tsStart;
	private long tsLast;
	
	private long count;
	
	public DustDevProcMon(String name, long segment) {
		reset(name, segment);
	}
	
	public void reset(String name, long segment) {
		this.name = name;
		this.segment = segment;
		tsStart = tsLast = System.currentTimeMillis();
	}
	
	public synchronized boolean step() {
		boolean ret = false;
		++count;
		
		if ( (0 != segment) && (0 == (count % segment)) ) {
			long ts = System.currentTimeMillis();
			System.out.println(name + " current count: " + count + " time: " + (ts - tsLast));
			tsLast = ts;
			ret = true;
		}
		
		return ret;
	}
	
	public long getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return name + " total count: " + count + " time: " + (System.currentTimeMillis() - tsStart);
	}
}
