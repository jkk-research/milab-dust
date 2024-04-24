package hu.sze.milab.dust.dev;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import hu.sze.milab.dust.utils.DustUtils;

public class DustDevCounter implements Iterable<Map.Entry<Object, Long>> {
	String head;
	Map<Object, Long> counts;

	public DustDevCounter(String head, boolean sorted) {
		this.head = head;
		counts = sorted ? new TreeMap<>() : new HashMap<>();
	}

	public void reset() {
		if ( null != counts ) {
			counts.clear();
		}
	}

	public void add(Object ob) {
		add(ob, 1L);
	}

	public void add(Object ob, long count) {
		Long l = counts.getOrDefault(ob, 0L);
		counts.put(ob, l + count);
	}

	@Override
	public Iterator<Entry<Object, Long>> iterator() {
		return counts.entrySet().iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = DustUtils.sbAppend(null, "", true, head, " {");
		for (Map.Entry<Object, Long> e : counts.entrySet()) {
			DustUtils.sbAppend(sb, "\t", true, "\n", e.getKey(), e.getValue());
		}
		
		sb.append("\n }");
		
		return sb.toString();
	}

	public boolean contains(Object ob) {
		return counts.containsKey(ob);
	}
	public Long peek(Object ob) {
		return counts.getOrDefault(ob, 0L);
	}
}
