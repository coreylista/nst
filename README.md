# nst

Pass accessToken and possibly url to get it goind. There are start()/stop() and eventMessage() methods that would be the only ones exposed.

accessToken = "Bearer c.LUZqOygAzXYT8Ts1DETCDVJDEuhz7URCq6fF1Lxh2DldMzMfBEftpgwfe3QuQtC3U5rasZcCXqkLDc1IjV67tenBwCqtlBje9yklX9N5JOz6dFHV7BPy6tZq1muj06WNPV8UYnnqWW9ix0XV"
String url = "https://developer-api.nest.com"
RestStreamClient rsc = new RestStreamClient()
rsc.start(url, accessToken)