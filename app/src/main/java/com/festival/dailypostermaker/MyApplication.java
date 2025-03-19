package com.festival.dailypostermaker;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.festival.dailypostermaker.AdPlacement.AppOpenManager;
import com.festival.dailypostermaker.Preference.MyPreference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication mInstance;
    public static boolean canRequestAds = false;
    public static boolean isShowingAd = false;
    public Activity currentActivity;
    public AppOpenManager appOpenManager = null;

    public static MyApplication getInstance() {
        return mInstance;
    }

    static {
        System.loadLibrary("native-lib");
    }

    public static native String getAppBaseUrl();

    public static native String getAppDecryptKey();

    public static String pName = "Your Name";
    public static String pAboutYou = "About You";
    public static String pAddress = "Your Address";
    public static String number = "(123) 456–7890";
    public static String userName = "username_123456";
    public static String bName = "Business Name";
    public static String bAbout = "About Business";
    public static String bAddress = "Business Address";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        canRequestAds = false;

        setupLifecycleObservers();
        initializeFirebase();
    }

    private void setupLifecycleObservers() {
        try {
            registerActivityLifecycleCallbacks(MyApplication.this);
            new Handler(Looper.getMainLooper()).post(() -> {
                ProcessLifecycleOwner.get().getLifecycle().addObserver(MyApplication.this);
            });
        } catch (Exception e) {
            Log.e(TAG, "Lifecycle Setup Error: ", e);
        }
    }

    private void initializeFirebase() {
        try {
            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(executorService, task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    MyPreference.set_Token(token);
                    Log.e(TAG, "onNewToken: " + token);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Firebase Initialization Error: ", e);
        }
    }


    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        Log.e(TAG, "onStart: " + currentActivity);
        try {
            if (!MyPreference.get_IsPremium()) {
                if (appOpenManager != null && currentActivity != null && canRequestAds && MyPreference.get_Ad() != 0) {
                    appOpenManager.showAdIfAvailable(currentActivity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStop(owner);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}
