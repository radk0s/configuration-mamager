package persistence.filters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class Filter {

	private final List<Pair<String, Object>> andList = new ArrayList<Pair<String, Object>>();

	private Filter() {
		
	}

	public static Filter create() {
		return new Filter();
	}

	public Filter eqAttr(String attr, Object value) {
		if (attr == null) {
			throw new NullPointerException("attr is null");
		}

		andList.add(Pair.of(attr, value));
		return this;
	}

	public List<Pair<String, Object>> getAndList() {
		return andList;
	}
}