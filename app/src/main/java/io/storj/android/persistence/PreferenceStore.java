package io.storj.android.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class PreferenceStore {

    @RootContext
    public Context context;

    private static SharedPreferences settings;

    private SharedPreferences getSettings() {
        if (settings == null) {
            settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        }

        return settings;
    }

    public String getString(String key) {
        return getSettings().getString(key, null);
    }

    public void setString(String key, String value) {
        final SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(key, value);
        editor.apply();
    }
}
