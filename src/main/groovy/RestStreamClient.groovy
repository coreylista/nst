package nstmanager

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Authenticator
import okhttp3.Route
import okio.Buffer

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RestStreamClient {
    private static final Long DEFAULT_BYTE_COUNT = 2048L

    String accessToken = 'c.vjRP7z59RvyiGpGtI8d8nsAoppVopHdWuufQNiecZNhKAZOFaQ1uLvRMZkXm3T9lVQ6Ef7sHbajdBqUmq5vNBhnncu18E5a'
    String nestApiUrl = "https://developer-api.nest.com"
    private OkHttpClient httpClient
    private final ExecutorService executorService = Executors.newSingleThreadExecutor()

    static void main(String[] args) {
        new RestStreamClient().start()
    }

    void start() {
        httpClient = new OkHttpClient().newBuilder().authenticator(new Authenticator() {
            @Override
            Request authenticate(Route route, Response response) {
                return response.request().newBuilder().header("Authorization", accessToken).build()
            }
        }).connectTimeout(10, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build()

        executorService.execute(new RestStreamClient.Reader())
    }

    private class Reader implements Runnable {
        @Override
        void run() {
            Response response = null
            Request request = new Request.Builder().url(nestApiUrl).addHeader("Accept", "text/event-stream").build()
            try {
                response = httpClient.newCall(request).execute()
                Buffer buffer = new Buffer()
                while (!response.body().source().exhausted()) {
                    long count = response.body().source().read(buffer, DEFAULT_BYTE_COUNT)
                    if (count > 0) {
                        String msg = segment(buffer.readUtf8())
                        parse(msg)
                    }
                }
            } catch(Exception ex) {
                ex.printStackTrace()
            } finally {
                if (response != null) response.body().close()
            }
        }

        private void parse(String msg) throws Exception {
            if (msg == null) return
            String[] lines = msg.split("\n")
            int i = 0
            while(i < lines.length) {
                String currentLine = lines[i]
                if (currentLine.startsWith("{\"error\":")) {
                    System.out.println("An error occurred! " +  currentLine)
                } else if (currentLine.startsWith("event:") && lines.length > i + 1) {
                    String nextLine = lines[i + 1]
                    if (currentLine.length() <= 8) {
                        throw new Exception("Unexpected length of event line.")
                    }
                    if (nextLine.length() <= 7) {
                        throw new Exception("Unexpected length of data line.")
                    }
                    String eventType = currentLine.substring(7) //7 = length of("event: ")
                    String json = nextLine.substring(6) //6 = length of("data: ")
                    System.out.println("\nevent: " +  eventType)
                    System.out.println("data: " +  json)
                }
                i++
            }
        }

        private String segment(String buf) {
            if (buf.endsWith("\n") || buf.endsWith("}")) {
                String msg = accumulator + buf
                accumulator = ""
                return msg
            } else accumulator = accumulator + buf
            return null
        }

        private String accumulator = ""
    }
}