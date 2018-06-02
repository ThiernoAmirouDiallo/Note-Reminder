package com.example.diallo110339.notereminder.retrofitClient;

import android.app.Application;
import android.content.Context;

//cette classe permet de recuperer un context dans une classe de facon static
//cela est important pour parametrage des cookies dans le client retrofit ou meme l'appel de l'api dans
//ou encore pour l'affichage des toast dans l'adaptateur qui n'a pas automatiquement accès au context
public class MyApplication extends Application {
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
    public static Context getAppContext() {
        return appContext;
    }
}
