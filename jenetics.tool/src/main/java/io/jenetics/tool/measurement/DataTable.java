package io.jenetics.tool.measurement;

import java.util.ArrayList;
import java.util.List;

public class DataTable {

	private final List<String> _header = new ArrayList<>();
	private final List<String> _rows = new ArrayList<>();

	public List<String> header() {
		return _header;
	}

}
