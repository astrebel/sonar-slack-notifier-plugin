package com.astrebel.sonarslack;

import static org.junit.Assert.*;

import org.junit.Test;

public class SeverityTest {

	@Test
	public void ValueOrDefault_KnownOptionIgnoreCase_Value() {
		assertEquals(Severity.NONE, Severity.valueOrDefault("NonE"));
		assertEquals(Severity.BLOCKER, Severity.valueOrDefault("blOcKer"));
		assertEquals(Severity.CRITICAL, Severity.valueOrDefault("CrIticAL"));
		assertEquals(Severity.MAJOR, Severity.valueOrDefault("mAJOr"));
		assertEquals(Severity.MINOR, Severity.valueOrDefault("MiNoR"));
		assertEquals(Severity.INFO, Severity.valueOrDefault("InFO"));
	}
	
	@Test
	public void ValueOrDefault_UnknownOption_DefaultInfo() {
		assertEquals(Severity.INFO, Severity.valueOrDefault("Unknown"));
	}
	
	@Test
	public void Ordinal_AssertOrder() {
		assertTrue(Severity.NONE.compareTo(Severity.BLOCKER) > 0);
		assertTrue(Severity.BLOCKER.compareTo(Severity.CRITICAL) > 0);
		assertTrue(Severity.CRITICAL.compareTo(Severity.MAJOR) > 0);
		assertTrue(Severity.MAJOR.compareTo(Severity.MINOR) > 0);
		assertTrue(Severity.MINOR.compareTo(Severity.INFO) > 0);
	}

	@Test
	public void MessageSeverity_NoIssues_None() {
		assertEquals(Severity.NONE, Severity.messageSeverity(0, 0, 0, 0, 0));
	}
	
	@Test
	public void MessageSeverity_OnlyInfos_Info() {
		assertEquals(Severity.INFO, Severity.messageSeverity(0, 0, 0, 0, 1));
	}
	
	@Test
	public void MessageSeverity_MinorsAndInfos_Minors() {
		assertEquals(Severity.MINOR, Severity.messageSeverity(0, 0, 0, 1, 1));
	}
	
	@Test
	public void MessageSeverity_MajorsAndSomeSmaller_Major() {
		assertEquals(Severity.MAJOR, Severity.messageSeverity(0, 0, 1, 0, 1));
	}
	
	@Test
	public void MessageSeverity_CriticalsAndSomeSmaller_Critical() {
		assertEquals(Severity.CRITICAL, Severity.messageSeverity(0, 1, 1, 1, 0));
	}
	
	@Test
	public void MessageSeverity_BlockerAndSomeSmaller_Blocker() {
		assertEquals(Severity.BLOCKER, Severity.messageSeverity(1, 0, 1, 0, 1));
	}
}
