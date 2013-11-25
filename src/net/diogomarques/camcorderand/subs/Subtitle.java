package net.diogomarques.camcorderand.subs;

import java.util.ArrayList;
import java.util.List;

public class Subtitle {

	private long startTimeMillis;
	private long endTimeMillies;
	private List<String> lines;

	public Subtitle(long startTimeMillis, long endTimeMillies, String... lines) {
		this.startTimeMillis = startTimeMillis;
		this.endTimeMillies = endTimeMillies;
		this.lines = new ArrayList<String>();
		for (String line : lines)
			this.lines.add(line);
	}

	public long getStartTimeMillis() {
		return startTimeMillis;
	}

	public void setStartTimeMillis(long startTimeMillis) {
		this.startTimeMillis = startTimeMillis;
	}

	public long getEndTimeMillies() {
		return endTimeMillies;
	}

	public void setEndTimeMillies(long endTimeMillies) {
		this.endTimeMillies = endTimeMillies;
	}

	public String[] getLines() {
		return (String[]) lines.toArray();
	}

	public void setLines(String... lines) {
		this.lines.clear();
		for(String line: lines)
			this.lines.add(line);
	}
	
	

}