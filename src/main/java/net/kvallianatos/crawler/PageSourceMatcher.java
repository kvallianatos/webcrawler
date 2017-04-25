package net.kvallianatos.crawler;

import java.util.List;

public interface PageSourceMatcher {

	List<String> getMatches(String source);
}
