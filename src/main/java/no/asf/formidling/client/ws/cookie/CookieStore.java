package no.asf.formidling.client.ws.cookie;

/**
 * Lagrer en <i>Cookie</i> i trådens minne.
 */
public class CookieStore {

    private static ThreadLocal<Object> requestCookie = new ThreadLocal<Object>();

    public static void setCookie(Object cookie) {
        requestCookie.set(cookie);
    }

    public static Object getCookie() {
        return requestCookie.get();
    }

}
