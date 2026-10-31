package com.bearly.qa.base;

/** All settings come from -D system properties or env vars, so no secrets live in the repo. */
public final class Config {
    private Config() {}

    public static String get(String key, String def) {
        String v = System.getProperty(key);
        if (v == null || v.isBlank()) v = System.getenv(key.toUpperCase().replace('.', '_'));
        return (v == null || v.isBlank()) ? def : v;
    }

    public static String baseUrl()   { return get("bearly.baseUrl", "https://bearly.ai"); }
    public static String appUrl()    { return get("bearly.appUrl", "https://app.bearly.ai"); }
    public static String email()     { return get("bearly.email", null); }
    public static String password()  { return get("bearly.password", null); }
    public static boolean headless() { return Boolean.parseBoolean(get("headless", "true")); }
    public static String chromeBin() { return get("chrome.bin", null); }
}
