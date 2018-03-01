# nst

Pass accessToken and possibly url to get it goind. There are start()/stop() and eventMessage() methods that would be the only ones exposed.

accessToken = "Bearer fake7BPy6tZq1muj06WNPV8UYnnqWW9ix0XV"
String url = "https://developer-api.nest.com"

RestStreamClient rsc = new RestStreamClient()
rsc.start(url, accessToken)
