package net.kvallianatos.crawler;

import org.junit.Test;

import java.util.List;

public class TestRegexParsers {

	@Test
	public void testHrefParser() {
		String testString = "<a href=\"http://somedomain.net\" target=\"foo\"/><p>foo<a onmouseover=\"MM_swapImgRestore()\" href=\"http://anotherdomain.net\"></p>";
		List<String> matches = new HrefMatcher().getMatches(testString);
		System.out.println("Found matches: ");
		for(String match: matches) {
			System.out.println(match);
		}
		
	}

	@Test
	public void testSrcParser() {
		String testString = "<img src=\"images/logored.gif\" width=120px border=0 name=Image19 /></a></p>";
		List<String> matches = new SrcMatcher().getMatches(testString);
		System.out.println("Found matches: ");
		for(String match: matches) {
			System.out.println(match);
		}

	}
}
