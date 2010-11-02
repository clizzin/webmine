(ns webmine.feeds-test
  (:require [clj-time.core :as time])
  (:use clojure.test
        webmine.feeds
        webmine.urls))

(deftest is-link-an-rss-feed
  (is (not (rss-suffix? "http://www.foo.com")))
  (is (rss-suffix? "http://www.foo.com/feed.xml"))
  (is (rss-suffix? "http://www.foo.com/feed.xml/"))
  (is (not (rss-suffix? "http://www.foo.com/feed.html"))))

(deftest find-rss-feeds
  (is (= (set ["http://www.huffingtonpost.com/feeds/original_posts/index.xml"
               "http://feeds.huffingtonpost.com/FeaturedPosts"
               "http://feeds.huffingtonpost.com/huffingtonpost/raw_feed"
               "http://feeds.huffingtonpost.com/huffingtonpost/TheBlog"
               "http://feeds.huffingtonpost.com/huffingtonpost/LatestNews"
               "http://www.huffingtonpost.com/wires/full_index.rdf"])
         (set (host-rss-feeds (url "http://www.huffingtonpost.com"))))))

(deftest canonical-rss-feed
  (is (= ["http://feeds.huffingtonpost.com/FeaturedPosts"]
	 (canonical-feeds [(url "http://www.huffingtonpost.com")])))
  (is (= ["http://feeds.huffingtonpost.com/FeaturedPosts"]
	 (canonical-feeds [(url "http://www.huffingtonpost.com")
			   (url "http://www.huffingtonpost.com")]))))

(def good-tc
{:content "<p><img src=\"http://tctechcrunch.files.wordpress.com/2010/11/dashboard.jpg\" />Computer power management software company, <a href=\"http://www.1e.com/index.aspx\">1E</a> has released a new version of its marquee product, <a href=\"http://www.1e.com/softwareproducts/nightwatchman/index.aspx\">NightWatchman</a>.</p> <p>Like its predecessors, version 6.0, helps corporations manage their network of computers to optimize energy efficiency. It gives IT managers the ability to remotely power down computers and establish energy-saving settings (ie. automatic shutdown of desktops during the weekend).</p> <p>In the latest version, <a href=\"http://www.crunchbase.com/company/1e\">1E</a> has added three key features: a new web-based dashboard (to help managers oversee the entire company&#8217;s computer power usage), improved diagnostic tools to determine why a computer hasn&#8217;t properly powered down, and tariff calculations based on location.</p> <p>Given how the price of energy can fluctuate significantly on a region by region basis, the new location-based calculations will help companies more accurately assess how much they&#8217;re saving on energy usage. Energy efficiency as it relates to IT management is becoming an increasingly important field, and according to Gartner Research, in two years more than half of mid and large-sized businesses will centrally manage their desktops&#8217; energy consumption.</p> <p>Although the average NightWatchman PC only saves $36 a year in energy costs, those incremental savings yield significant sums in aggregate. Several of 1E&#8217;s clients are large corporations with massive IT operations, such as AT&amp;T, Ford and Dell. According to 1E, NightWatchman has 4.6 million licensed users around the world, a group that has collectively saved $530 million in energy costs.</p> <div class=\"cbw snap_nopreview\"><div class=\"cbw_header\"><script src=\"http://www.crunchbase.com/javascripts/widget.js\" type=\"text/javascript\"></script><div class=\"cbw_header_text\"><a href=\"http://www.crunchbase.com/\">CrunchBase Information</a></div></div><div class=\"cbw_content\"><div class=\"cbw_subheader\"><a href=\"http://www.crunchbase.com/company/1e\">1E</a></div><div class=\"cbw_subcontent\"><script src=\"http://www.crunchbase.com/cbw/company/1e.js\" type=\"text/javascript\"></script></div><div class=\"cbw_footer\">Information provided by <a href=\"http://www.crunchbase.com/\">CrunchBase</a></div></div></div> <br /> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gocomments/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/comments/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/godelicious/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/delicious/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gofacebook/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/facebook/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gotwitter/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/twitter/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gostumble/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/stumble/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/godigg/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/digg/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/goreddit/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/reddit/tctechcrunch.wordpress.com/238571/\" /></a> <img alt=\"\" border=\"0\" src=\"http://stats.wordpress.com/b.gif?host=techcrunch.com&amp;blog=11718616&amp;post=238571&amp;subd=tctechcrunch&amp;ref=&amp;feed=1\" width=\"1\" height=\"1\" /> <p><a href=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/0/da\"><img src=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/0/di\" border=\"0\" ismap=\"true\"></img></a><br/> <a href=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/1/da\"><img src=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/1/di\" border=\"0\" ismap=\"true\"></img></a></p><div class=\"feedflare\"> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:2mJPEYqXBVI\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=2mJPEYqXBVI\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:7Q72WNTAKBA\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=7Q72WNTAKBA\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:yIl2AUoC8zA\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=yIl2AUoC8zA\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:-BTjWOF_DHI\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?i=tEFtaTZo8jI:MRP7yJsevfA:-BTjWOF_DHI\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:D7DqB2pKExk\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?i=tEFtaTZo8jI:MRP7yJsevfA:D7DqB2pKExk\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:qj6IDK7rITs\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=qj6IDK7rITs\" border=\"0\"></img></a> </div><img src=\"http://feeds.feedburner.com/~r/Techcrunch/~4/tEFtaTZo8jI\" height=\"1\" width=\"1\"/>", :des "<img src=\"http://tctechcrunch.files.wordpress.com/2010/11/watchman.jpg\" />Computer power management software company, <a href=\"http://www.1e.com/index.aspx\">1E</a> has released a new version of its marquee product, <a href=\"http://www.1e.com/softwareproducts/nightwatchman/index.aspx\">NightWatchman</a>. Like its predecessors, version 6.0, helps corporations manage their network of computers to optimize energy efficiency. It gives IT managers the ability to remotely power down computers and establish energy-saving settings (ie. automatic shutdown of desktops during the weekend). In the latest version, <a href=\"http://www.crunchbase.com/company/1e\">1E</a> has added three key features.<img alt=\"\" border=\"0\" src=\"http://stats.wordpress.com/b.gif?host=techcrunch.com&amp;blog=11718616&amp;post=238571&amp;subd=tctechcrunch&amp;ref=&amp;feed=1\" width=\"1\" height=\"1\" />"})

(def bad-tc
{:content "<p><img src=\"http://tctechcrunch.files.wordpress.com/2010/11/dashboard.jpg\" />Computer power management software company, <a href=\"http://www.1e.com/index.aspx\">1E</a> has released a new version of its marquee product, <a href=\"http://www.1e.com/softwareproducts/nightwatchman/index.aspx\">NightWatchman</a>.</p> <p>Like its predecessors, version 6.0, helps corporations manage their network of computers to optimize energy efficiency. It gives IT managers the ability to remotely power down computers and establish energy-saving settings (ie. automatic shutdown of desktops during the weekend).</p> <p>In the latest version, <a href=\"http://www.crunchbase.com/company/1e\">1E</a> has added three key features: a new web-based dashboard (to help managers oversee the entire company&#8217;s computer power usage), improved diagnostic tools to determine why a computer hasn&#8217;t properly powered down, and tariff calculations based on location.</p> <p>Given how the price of energy can fluctuate significantly on a region by region basis, the new location-based calculations will help companies more accurately assess how much they&#8217;re saving on energy usage. Energy efficiency as it relates to IT management is becoming an increasingly important field, and according to Gartner Research, in two years more than half of mid and large-sized businesses will centrally manage their desktops&#8217; energy consumption.</p> <p>Although the average NightWatchman PC only saves $36 a year in energy costs, those incremental savings yield significant sums in aggregate. Several of 1E&#8217;s clients are large corporations with massive IT operations, such as AT&amp;T, Ford and Dell. According to 1E, NightWatchman has 4.6 million licensed users around the world, a group that has collectively saved $530 million in energy costs.</p> <div class=\"cbw snap_nopreview\"><div class=\"cbw_header\"><script src=\"http://www.crunchbase.com/javascripts/widget.js\" type=\"text/javascript\"></script><div class=\"cbw_header_text\"><a href=\"http://www.crunchbase.com/\">CrunchBase Information</a></div></div><div class=\"cbw_content\"><div class=\"cbw_subheader\"><a href=\"http://www.crunchbase.com/company/1e\">1E</a></div><div class=\"cbw_subcontent\"><script src=\"http://www.crunchbase.com/cbw/company/1e.js\" type=\"text/javascript\"></script></div><div class=\"cbw_footer\">Information provided by <a href=\"http://www.crunchbase.com/\">CrunchBase</a></div></div></div> <br /> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gocomments/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/comments/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/godelicious/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/delicious/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gofacebook/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/facebook/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gotwitter/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/twitter/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/gostumble/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/stumble/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/godigg/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/digg/tctechcrunch.wordpress.com/238571/\" /></a> <a rel=\"nofollow\" href=\"http://feeds.wordpress.com/1.0/goreddit/tctechcrunch.wordpress.com/238571/\"><img alt=\"\" border=\"0\" src=\"http://feeds.wordpress.com/1.0/reddit/tctechcrunch.wordpress.com/238571/\" /></a> <img alt=\"\" border=\"0\" src=\"http://stats.wordpress.com/b.gif?host=techcrunch.com&amp;blog=11718616&amp;post=238571&amp;subd=tctechcrunch&amp;ref=&amp;feed=1\" width=\"1\" height=\"1\" /> <p><a href=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/0/da\"><img src=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/0/di\" border=\"0\" ismap=\"true\"></img></a><br/> <a href=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/1/da\"><img src=\"http://feedads.g.doubleclick.net/~at/w3bq_Lf15MuCCqeCkVfh9eMYs7g/1/di\" border=\"0\" ismap=\"true\"></img></a></p><div class=\"feedflare\"> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:2mJPEYqXBVI\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=2mJPEYqXBVI\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:7Q72WNTAKBA\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=7Q72WNTAKBA\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:yIl2AUoC8zA\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=yIl2AUoC8zA\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:-BTjWOF_DHI\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?i=tEFtaTZo8jI:MRP7yJsevfA:-BTjWOF_DHI\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:D7DqB2pKExk\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?i=tEFtaTZo8jI:MRP7yJsevfA:D7DqB2pKExk\" border=\"0\"></img></a> <a href=\"http://feeds.feedburner.com/~ff/Techcrunch?a=tEFtaTZo8jI:MRP7yJsevfA:qj6IDK7rITs\"><img src=\"http://feeds.feedburner.com/~ff/Techcrunch?d=qj6IDK7rITs\" border=\"0\"></img></a> </div><img src=\"http://feeds.feedburner.com/~r/Techcrunch/~4/tEFtaTZo8jI\" height=\"1\" width=\"1\"/>"})

(def bad-tc-fixed
     (merge bad-tc
	    {:des
	     "Computer power management software company, 1E has released a new version of its marquee product, NightWatchman."}))

(deftest des-tests
  (is (= bad-tc-fixed
	 (mk-des bad-tc)))
  (is (= good-tc
	 (mk-des good-tc))))

(deftest date-parsing
  ;; Ensure that the datetime key strings are canonicalized to the
  ;; respective datetime value string.
  (let [datetimes {"2010-11-01T15:03:12.760Z" "2010-11-01T15:03:12.760Z"
                   "Sun, 31 Oct 2010 03:03:00 EDT" "2010-10-31T07:03:00.000Z"
                   "Sun, 31 Oct 10 03:03:00 EDT" "2010-10-31T07:03:00.000Z"
                   "Mon, 1 Nov 2010 12:24:00 PDT" "2010-11-01T19:24:00.000Z"
                   "Mon, 01 Nov 2010 12:24:00 PDT" "2010-11-01T19:24:00.000Z"
                   "1996-12-19T16:39:57-08:00" "1996-12-20T00:39:57.000Z"}]
    (doseq [[dt canonical-dt] datetimes]
      (is (= canonical-dt (@#'webmine.feeds/compact-date-time dt))))))
