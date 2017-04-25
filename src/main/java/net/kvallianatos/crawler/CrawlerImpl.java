package net.kvallianatos.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class CrawlerImpl {

	private Set<String> domainURLs = new HashSet<>();
	private Set<String> externalURLs = new HashSet<>();
	private Set<String> staticContent = new HashSet<>();
	private Set<String> unvisitedLinks = new HashSet<>();

	private List<PageSourceMatcher> urlMatchers = new ArrayList<>();
	private List<PageSourceMatcher> staticContentMatchers = new ArrayList<>();

	String domainString;

	//This would normally be handled using dependency injection. Hard coded into constructor for simplicity
	public CrawlerImpl() {
		urlMatchers.add(new HrefMatcher());
		staticContentMatchers.add(new SrcMatcher());
	}

	public CrawlerImpl(PageSourceMatcher hrefMatcher, PageSourceMatcher srcMatcher) {
		urlMatchers.add(hrefMatcher);
		staticContentMatchers.add(srcMatcher);
	}

	public static void main (String[] args) {
		new CrawlerImpl().crawl(args[0]);
	}

	//This is the top-level method to invoke when integrating
	public void crawl(String baseURLString) {
		setDomainString(baseURLString);
		domainURLs.add(baseURLString);
		unvisitedLinks.add(baseURLString);

		while(!unvisitedLinks.isEmpty()) {
			String link = null;
			Iterator<String> iterator = unvisitedLinks.iterator();
			if (iterator.hasNext()) {
				link = iterator.next();
				String pageSource = getPageSource(link);

				handleLinks(pageSource);
				handleStaticContentReferences(pageSource);
			}
			if (link != null) {
				unvisitedLinks.remove(link);
			}
		}

		generateOutput();
	}

	String getPageSource(String urlString) {
		System.out.println("Retrieving source for: " + urlString);

		StringBuilder a = new StringBuilder();
		URLConnection connection = null;

		try {
			URL baseURL = new URL(urlString);
			connection = baseURL.openConnection();
			connection.connect();
		} catch (MalformedURLException e) {
			System.out.println("URL provided is malformed: " + urlString);
			throw new RuntimeException("Operation aborted");
		} catch (IOException e) {
			System.out.println("Cannot connect to " + urlString + ". Crawl operation aborted.");
			throw new RuntimeException("Operation aborted");
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				a.append(inputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
			//throw new RuntimeException("Operation aborted");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("Operation aborted");
				}
			}
		}

		//System.out.println("PAGE SOURCE: \n" + a.toString());
		return a.toString();
	}

	/*
	 * Handles parsing for links. If a link contains the domain base URL it is added as an internal link and also crawled.
	 * Otherwise the link is treated as an external link.
	 */
	void handleLinks(String source) {
		for(PageSourceMatcher matcher: urlMatchers) {
			List<String> matches = matcher.getMatches(source);
			for(String match: matches) {
				if(match.startsWith("mailto") || match.startsWith("javascript")) {
					continue;
				} else if(match.contains("#")) {
					continue;
				} else if(match.startsWith("http")) {
					if (match.contains(domainString)) {
						if (!domainURLs.contains(match)) {
							domainURLs.add(match);
							unvisitedLinks.add(match);
						}
					} else {
						externalURLs.add(match);
					}
				} else {
					StringBuffer buffer = new StringBuffer("http://").append(domainString);
					if (!match.startsWith("/")) {
						buffer.append("/");
					}
					buffer.append(match);
					String fullURL = buffer.toString();
					if(!domainURLs.contains(fullURL)) {
						domainURLs.add(fullURL);
						unvisitedLinks.add(fullURL);
					}
				}
			}
		}
	}

	/*
	 * Handles parsing for static content, notably images.
	 */
	void handleStaticContentReferences(String source) {
		for(PageSourceMatcher matcher: staticContentMatchers) {
			List<String> matches = matcher.getMatches(source);
			for(String match: matches) {
				if(match.startsWith("http")) {
					staticContent.add(match);
				} else {
					StringBuffer buffer = new StringBuffer("http://").append(domainString);
					if (!match.startsWith("/")) {
						buffer.append("/");
					}
					buffer.append(match);
					staticContent.add(buffer.toString());
				}
			}
		}
	}

	void generateOutput() {
		System.out.println("Site links:");
		for(String link: domainURLs) {
			System.out.println("\t" + link);
		}

		System.out.println("Static content:");
		for(String link: staticContent) {
			System.out.println("\t" + link);
		}

		System.out.println("External links:");
		for(String link: externalURLs) {
			System.out.println("\t" + link);
		}
	}

	void setDomainString(String urlString) {
		try {
			URL baseURL = new URL(urlString);
			System.out.println("Domain = " + baseURL.getHost());
			domainString = baseURL.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException("Operation aborted");
		}
	}
}
