package com.example.brecho;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS_NAME = "brecho_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_JWT = "jwt";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String username, String jwtToken) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_JWT, jwtToken)
                .apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getJwt() {
        return prefs.getString(KEY_JWT, null);
    }

    public boolean isLoggedIn() {
        return getJwt() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}

