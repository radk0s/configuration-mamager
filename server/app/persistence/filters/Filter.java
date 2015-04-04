package persistence.filters;

import java.util.ArrayList;
import java.util.List;


public class Filter {
	private int start = -1;
	private int max = -1;
	private final List<Pair<String, Object>> andList = new ArrayList<Pair<String, Object>>();

	private Filter() {
	}

	public static Filter create() {
		return new Filter();
	}

	public int getStart() {
		return start;
	}

	public Filter start(int start) {
		if (start < 0)
			throw new IllegalArgumentException("Start must be greater or equal to 0.");
		this.start = start;
		return this;
	}

	public int getMax() {
		return max;
	}

	public Filter max(int max) {
		if (max < 0)
			throw new IllegalArgumentException("Max must be greater or equal to 0.");
		this.max = max;
		return this;
	}

	public Filter eqAttr(String attr, Object value) {
		if (attr == null) {
			throw new NullPointerException("attr is null");
		}
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		andList.add(new Pair<String, Object>(attr, value));
		return this;
	}

	/**
	* @return the andList
	*/
	public List<Pair<String, Object>> getAndList() {
		return andList;
	}

}