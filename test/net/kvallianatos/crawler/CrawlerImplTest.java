package net.kvallianatos.crawler;

import org.junit.Before;
import org.junit.Test;

public class CrawlerImplTest {

	private CrawlerImpl crawler;

	@Before
	public void setup() {
		crawler = new CrawlerImpl(new HrefMatcher(), new SrcMatcher());
		crawler.setDomainString("http://mydomain.net");
	}

	@Test
	public void testHandleLinks() {
		String testSource = "<html>" +
				"<a href=\"http://mydomain.net/page1\">" +
				"<a href=\"/page2\">" +
				"<a href=\"page3\">" +
				"<a href=\"http://anotherdomain.net/page1\">" +
				"</html>";

		crawler.handleLinks(testSource);
		crawler.generateOutput();
	}

	@Test
	public void testHandleStaticContent() {
		String testSource = "<html>" +
				"<a href=\"http://mydomain.net/page1\">" +
				"<img src=\"logo.png\">" +
				"<a href=\"page2\">" +
				"<script src=\"myScript.js\"" +
				"<a href=\"http://anotherdomain.net/page1\">" +
				"</html>";

		crawler.handleStaticContentReferences(testSource);
		crawler.generateOutput();
	}

}