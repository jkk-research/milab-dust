package hu.sze.milab.dust.dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DustDevIndexer<KeyType> {
	private Map<KeyType, Integer> indexes = new HashMap<>();
	private ArrayList<KeyType> idxArr = new ArrayList<>();

	public synchronized int getIndex(KeyType ob) {
		Integer ret = indexes.get(ob);

		if ( null == ret ) {
			ret = indexes.size();
			indexes.put(ob, ret);
			idxArr.add(ob);
		}

		return ret;
	}

	public int peekIndex(KeyType ob) {
		return indexes.getOrDefault(ob, -1);
	}

	public int getSize() {
		return indexes.size();
	}

	public KeyType getKey(int idx) {
		return idxArr.get(idx);
	}

	public Iterable<KeyType> keys() {
		return idxArr;
	}

	@Override
	public String toString() {
		return indexes.toString();
	}

	public void reset() {
		indexes.clear();
		idxArr.clear();
	}
}
