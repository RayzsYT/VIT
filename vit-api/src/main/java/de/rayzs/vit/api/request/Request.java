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
     * Set the access token. This can only be called
     * once and cannot be used again, since the access token
     * has been set.
     * <p>
     * Access token must be unset first using the {@link #unsetAccessToken()} method
     * in order for it to be set again.
     * <p>
     * The access token is required for creating {@link RequestDest#LOCAL},
     * {@link RequestDest#PD}, {@link RequestDest#GLZ},
     * and {@link RequestDest#SHARED} requests.
     *
     * @param accessToken Access token.
     */
    public static void setAccessToken(final String accessToken) {

        if (ACCESS_TOKEN != null) {
            throw new IllegalStateException("Access token is already set!");
        }

        ACCESS_TOKEN = accessToken;

    }

    /**
     * Unsets the access token. Can only be
     * done when it's set.
     */
    public static void unsetAccessToken() {

        if (ACCESS_TOKEN == null) {
            throw new IllegalStateException("Access token is not set!");
        }

        ACCESS_TOKEN = null;

    }


    /**
     * Set the headers. This can only be called
     * once and cannot be used again, since the access token
     * has been set.
     * <p>
     * Access token must be unset first using the {@link #unsetAccessToken()} method
     * in order for it to be set again.
     * <p>
     * The headers are required for creating {@link RequestDest#PD},
     * {@link RequestDest#GLZ}, and {@link RequestDest#SHARED}
     * requests.
     *
     * @param entitlementToken Entitlement token.
     * @param clientPlatform Client platform.
     * @param currentVersion Version.
     */
    public static void setHeaders(
            final String entitlementToken,
            final String clientPlatform,
            final String currentVersion
    ) {

        if (ENTITLEMENT_TOKEN != null) {
            throw new IllegalStateException("Header 'Entitlement Token' is already set!");
        }

        if (CLIENT_PLATFORM != null) {
            throw new IllegalStateException("Header 'Client Platform' is already set!");
        }

        if (CURRENT_VERSION != null) {
            throw new IllegalStateException("Header 'Current Version' is already set!");
        }

        ENTITLEMENT_TOKEN = entitlementToken;
        CLIENT_PLATFORM = clientPlatform;
        CURRENT_VERSION = currentVersion;
    }

    /**
     * Unsets all the headers. Can only be
     * done when they are all set.
     */
    public static void unsetHeaders() {
        if (ENTITLEMENT_TOKEN == null) {
            throw new IllegalStateException("Header 'Entitlement Token' is not set!");
        }

        if (CLIENT_PLATFORM == null) {
            throw new IllegalStateException("Header 'Client Platform' is not set!");
        }

        if (CURRENT_VERSION == null) {
            throw new IllegalStateException("Header 'Current Version' is not set!");
        }

        ENTITLEMENT_TOKEN = null;
        CLIENT_PLATFORM = null;
        CURRENT_VERSION = null;
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
            if (ACCESS_TOKEN == null) {
                throw new NullPointerException("Cannot create " + dest.name() + " request because the Access Token is not set!");
            }

            builder.header("Authorization", "Bearer " + ACCESS_TOKEN);


            if (dest != RequestDest.LOCAL) {
                if (ENTITLEMENT_TOKEN == null || CLIENT_PLATFORM == null || CURRENT_VERSION == null) {
                    throw new NullPointerException("Cannot create " + dest.name() + " request because headers are not set!");
                }

                builder.header("X-Riot-Entitlements-JWT", ENTITLEMENT_TOKEN)
                        .header("X-Riot-ClientPlatform", CLIENT_PLATFORM)
                        .header("X-Riot-ClientVersion", CURRENT_VERSION);
            }
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
