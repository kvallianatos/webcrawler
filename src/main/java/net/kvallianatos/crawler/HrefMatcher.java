package net.kvallianatos.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HrefMatcher implements PageSourceMatcher {

	@Override
	public List<String> getMatches(String source) {
		List<String> matches = new ArrayList<>();

		String patternString = "<a.+?href=\"(.+?)\"";
		Pattern pattern = Pattern.compile(patternString);
		Matcher m = pattern.matcher(source);
		while (m.find()) {
			matches.add(m.group(1));
		}

		return matches;
	}
}
