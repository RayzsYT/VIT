package de.rayzs.vit.api.request;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

public class Request {

    // Constant client platform. Will transform using Base64 right below.
    // Just some basic info pretending to be Windows 11 hehe.
    private static String CLIENT_PLATFORM =  "{\"platformType\":\"PC\",\"platformOS\":\"Windows\",\"platformOSVersion\":\"11\",\"platformChipset\":\"Intel\"}";
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


            CLIENT_PLATFORM = Base64.getEncoder().encodeToString(CLIENT_PLATFORM.getBytes(StandardCharsets.UTF_8));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private static String AUTH_TOKEN, ACCESS_TOKEN, ENTITLEMENT_TOKEN, CURRENT_VERSION;


    /**
     * Set the auth token. This can only be called
     * once and cannot be used again, except when {@link #unsetAuthToken()}
     * method has been called.
     * <p>
     * The auth token is required for creating {@link RequestDest#LOCAL},
     * {@link RequestDest#PD}, {@link RequestDest#GLZ},
     * and {@link RequestDest#SHARED} requests.
     *
     * @param authToken Auth token.
     */
    public static void setAuthToken(final String authToken) {

        if (AUTH_TOKEN != null) {
            throw new IllegalStateException("Auth token is already set!");
        }

        AUTH_TOKEN = authToken;
    }

    /**
     * Unsets the auth token, in case it has been set already.
     */
    public static void unsetAuthToken() {

        if (AUTH_TOKEN == null) {
            throw new IllegalStateException("Auth token is not set!");
        }

        AUTH_TOKEN = null;
    }

    /**
     * If the auth token has been set already.
     *
     * @return True if the auth token is set. False otherwise.
     */
    public static boolean isAuthTokenSet() {
        return AUTH_TOKEN != null;
    }


    /**
     * Set the headers. This can only be called
     * once and cannot be used again, since the access token
     * has been set.
     * <p>
     * Access token must be unset first using the {@link #unsetHeaders()} method
     * in order for it to be set again.
     * <p>
     * The headers are required for creating {@link RequestDest#PD},
     * {@link RequestDest#GLZ}, and {@link RequestDest#SHARED}
     * requests.
     *
     * @param accessToken Access token.
     * @param entitlementToken Entitlement token.
     * @param currentVersion Version.
     */
    public static void setHeaders(
            final String accessToken,
            final String entitlementToken,
            final String currentVersion
    ) {

        if (ACCESS_TOKEN != null) {
            throw new IllegalStateException("Access token is already set!");
        }

        if (ENTITLEMENT_TOKEN != null) {
            throw new IllegalStateException("Header 'Entitlement Token' is already set!");
        }

        if (CURRENT_VERSION != null) {
            throw new IllegalStateException("Header 'Current Version' is already set!");
        }

        ACCESS_TOKEN = accessToken;
        ENTITLEMENT_TOKEN = entitlementToken;
        CURRENT_VERSION = currentVersion;
    }

    /**
     * Unsets all the headers. Can only be
     * done when they are all set.
     */
    public static void unsetHeaders() {

        if (ACCESS_TOKEN == null) {
            throw new IllegalStateException("Header 'Access token' is not set!");
        }

        if (ENTITLEMENT_TOKEN == null) {
            throw new IllegalStateException("Header 'Entitlement Token' is not set!");
        }

        if (CURRENT_VERSION == null) {
            throw new IllegalStateException("Header 'Current Version' is not set!");
        }

        ACCESS_TOKEN = null;
        ENTITLEMENT_TOKEN = null;
        CURRENT_VERSION = null;
    }

    /**
     * Returns if all the headers have been set or not.
     *
     * @return True if all headers are set. False otherwise.
     */
    public static boolean areHeadersSet() {
        return ACCESS_TOKEN != null && ENTITLEMENT_TOKEN != null && CURRENT_VERSION != null;
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
    private final RequestDest dest;

    private int statusCode;

    private Request(
            final RequestMethod method,
            final RequestDest dest,
            final String urlPath,
            final String body
    ) {
        this.dest = dest;

        final HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(dest.from(urlPath)));


        if (dest != RequestDest.API && dest != RequestDest.UPDATE) {

            if (dest == RequestDest.LOCAL) {

                if (AUTH_TOKEN == null) {
                    throw new NullPointerException("Cannot create " + dest.name() + " request because the Access Token is not set!");
                }

                builder.header("Authorization", "Basic " + AUTH_TOKEN);

            } else {

                if (ACCESS_TOKEN == null || ENTITLEMENT_TOKEN == null || CLIENT_PLATFORM == null || CURRENT_VERSION == null) {
                    throw new NullPointerException("Cannot create " + dest.name() + " request because headers are not set!");
                }

                builder.header("Authorization", "Bearer " + ACCESS_TOKEN)
                        .header("X-Riot-Entitlements-JWT", ENTITLEMENT_TOKEN)
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

            final HttpResponse<String> contentResponse = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

            statusCode = contentResponse.statusCode();


            if (statusCode == 1015) {
                return Optional.of("{\"error\": \"rate-limited-1015\"}");
            }


            if (statusCode != 200) {
                System.err.println("Request denied! (" + request.uri() + ")");
                System.err.println("Response: " + contentResponse.body());

                return Optional.empty();
            }

            return Optional.of(contentResponse.body());

        } catch (Exception exception) {

            System.err.println("Request failed! (" + request.uri() + ")");

            // Only printing the exception when the destination
            // is not LOCAL, since the presence check, which
            // uses the LOCAL url, will fail when VALORANT
            // isn't open. So for the sake of a clean console,
            // it's just going to be ignored.
            if (dest != RequestDest.LOCAL) {
                exception.printStackTrace();
            }

        }

        return Optional.empty();
    }
}
