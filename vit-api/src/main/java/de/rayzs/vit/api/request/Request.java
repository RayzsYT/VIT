package de.rayzs.vit.api.request;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Optional;

public class Request {

    private static SSLContext SSL_CONTEXT;

    static {
        // Creating a dummy TrustManager to bypass SSL certified connections.
        final TrustManager[] dummy = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] xcs, String string) {
                    }

                    public void checkServerTrusted(X509Certificate[] xcs, String string) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };

        try {
            // Applying custom TrustManager to create SSL connections.
            SSL_CONTEXT = SSLContext.getInstance("TLS");
            SSL_CONTEXT.init(null, dummy, new SecureRandom());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private static String ACCESS_TOKEN, ENTITLEMENT_TOKEN, CLIENT_PLATFORM, CURRENT_VERSION;

    /**
     * Set the variables. This can only be done once,
     * since this should not be changed during runtime.
     * It's required for headers when creating a request.
     *
     * @param accessToken Access token.
     * @param entitlementToken Entitlement token.
     * @param clientPlatform Client platform.
     * @param currentVersion Version.
     */
    public static void setVariables(
            final String accessToken,
            final String entitlementToken,
            final String clientPlatform,
            final String currentVersion
    ) {

        if (ACCESS_TOKEN != null) {
            throw new IllegalStateException("Variables are already set!");
        }

        ACCESS_TOKEN = accessToken;
        ENTITLEMENT_TOKEN = entitlementToken;
        CLIENT_PLATFORM = clientPlatform;
        CURRENT_VERSION = currentVersion;
    }


    /**
     * Create a HttpClient.
     *
     * @return Created HttpClient.
     */
    public static HttpClient createClient() {
        return HttpClient.newBuilder()
                .sslContext(SSL_CONTEXT)
                .build();
    }


    /**
     * Creates a Request object without a body.
     *
     * @param method RequestMethod.
     * @param dest RequestDest.
     * @param urlPath URL.
     * @return Request.
     */
    public static Request createRequest(
            final RequestMethod method,
            final RequestDest dest,
            final String urlPath
    ) {
        return new Request(
                method,
                dest,
                urlPath,
                ""
        );
    }

    /**
     * Creates a Request object.
     *
     * @param method RequestMethod.
     * @param dest RequestDest.
     * @param urlPath URL.
     * @param body Body.
     * @return Request.
     */
    public static Request createRequest(
            final RequestMethod method,
            final RequestDest dest,
            final String urlPath,
            final String body
    ) {
        return new Request(
                method,
                dest,
                urlPath,
                body
        );
    }


    private final HttpRequest request;

    private Request(
            final RequestMethod method,
            final RequestDest dest,
            final String urlPath,
            final String body
    ) {
        final HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(dest.from(urlPath)));

        if (dest != RequestDest.API) {
            builder.header("Authorization", "Bearer " + ACCESS_TOKEN)
                    .header("X-Riot-Entitlements-JWT", ENTITLEMENT_TOKEN)
                    .header("X-Riot-ClientPlatform", CLIENT_PLATFORM)
                    .header("X-Riot-ClientVersion", CURRENT_VERSION);
        }

        switch (method) {
            case GET -> builder.GET();

            case POST -> builder
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body));

            case PUT -> builder
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body));
        }

        request = builder.build();
    }

    /**
     * Sends the request and returns the body of the
     * request if successful.
     *
     * @param client HttpClient.
     * @return Body.
     */
    public Optional<String> sendAndGet(final HttpClient client) {
        try {
            final HttpResponse<String> contentResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (contentResponse.statusCode() != 200) {
                return Optional.empty();
            }

            return Optional.of(contentResponse.body());

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return Optional.empty();
    }
}
