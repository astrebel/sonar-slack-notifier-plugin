package com.astrebel.sonarslack;

import java.util.ArrayList;
import java.util.List;

public enum Severity {
	INFO,
	MINOR,
	MAJOR,
	CRITICAL,
	BLOCKER,
	NONE;
	
	public static List<String> getOptions() {
		List<String> options = new ArrayList<>(Severity.values().length);
		for(Severity value : Severity.values()) {
			options.add(value.toString());
		}
		return options;
	}
	
	public static Severity valueOrDefault(String option)
	{
		for (Severity value : Severity.values()) {
			if (value.name().equalsIgnoreCase(option)) {
				return value;
			}
		}
		return INFO;
	}
	
	public static Severity messageSeverity(
			int countBlockers,
			int countCriticals,
			int countMajors,
			int countMinors,
			int countInfos ) {
		
		if (countBlockers > 0) { return BLOCKER; }
		if (countCriticals > 0) { return CRITICAL; }
		if (countMajors > 0) { return MAJOR; }
		if (countMinors > 0) { return MINOR; }
		if (countInfos > 0) { return INFO; }
		return NONE;
	}
}
