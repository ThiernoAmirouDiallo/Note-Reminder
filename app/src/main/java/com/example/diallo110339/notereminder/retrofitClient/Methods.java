package com.example.diallo110339.notereminder.retrofitClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;

public class Methods {

    public static HashSet<String> getCookies(Context context) {
        SharedPreferences mcpPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return (HashSet<String>) mcpPreferences.getStringSet("cookies", new HashSet<String>());
    }

    public static boolean setCookies(Context context, HashSet<String> cookies) {
        SharedPreferences mcpPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mcpPreferences.edit();
        return editor.putStringSet("cookies", cookies).commit();
    }
}
