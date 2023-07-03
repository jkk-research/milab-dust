package hu.sze.milab.dust.dev;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DustDevCounter implements Iterable<Map.Entry<Object, Long>> {
    Map<Object, Long> counts;
    
    public DustDevCounter(boolean sorted) {
        counts = sorted ? new TreeMap<>() : new HashMap<>();
    }
    
    public void reset() {
       if ( null != counts) {
      	 counts.clear();
       }
    }
    
    public void add(Object ob) {
        Long l = counts.getOrDefault(ob, 0L);
        counts.put(ob, l+1);
    }
    
    @Override
    public Iterator<Entry<Object, Long>> iterator() {
        return counts.entrySet().iterator();
    }
    
		public void dump() {
			for (Map.Entry<Object, Long> c : this) {
				System.out.println(c.getKey() + ": " + c.getValue());
			}
		}
}
