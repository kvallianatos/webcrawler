# webcrawler

Usage: run CrawlerImpl.main() passing the URL for crawling as a single argument.
Output (sitemap) is printed on stdout.

The implementation has been kept simple to meet the given time constraints. The
mechanism for matching is extensible by implementing additional matcher (PageSourceMatcher)
classes and adding them to the appropriate matcher list (links and static content). For
this exercise, one of each has been provided:

- Link matcher uses a regex to match \"<a href=\"match\">\" and excludes mailto and javascript hrefs.
- Static content matcher uses a regex to match \"<img src=\"match\">\"