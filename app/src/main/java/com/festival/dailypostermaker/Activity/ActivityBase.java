package com.festival.dailypostermaker.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.MyUtils.LocaleHelper;
import com.festival.dailypostermaker.MyUtils.NetworkChangeReceiver;
import com.festival.dailypostermaker.MyUtils.NetworkUtil;
import com.festival.dailypostermaker.R;

public class ActivityBase extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {

    private Activity activity;
    private NetworkChangeReceiver networkReceiver;
    private Dialog noInternetDialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity = this;

        networkReceiver = new NetworkChangeReceiver(this);

        if (!NetworkUtil.isNetworkConnected(this)) {
            showNoInternetDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (isConnected) {
            dismissNoInternetDialog();
        } else {
            showNoInternetDialog();
        }
    }

    private void showNoInternetDialog() {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
        if (noInternetDialog != null && noInternetDialog.isShowing()) return;

        noInternetDialog = new Dialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_no_internet, null);
        noInternetDialog.setContentView(view);
        noInternetDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        noInternetDialog.getWindow().setGravity(Gravity.CENTER);
        noInternetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        noInternetDialog.setCancelable(false);

        TextView tryAgainTxt = view.findViewById(R.id.tryAgainTxt);
        tryAgainTxt.setOnClickListener(v -> {
            if (NetworkUtil.isNetworkConnected(activity)) {
                dismissNoInternetDialog();
            } else {
                Toast.makeText(activity, "No Internet Found", Toast.LENGTH_SHORT).show();
            }
        });

        noInternetDialog.show();
    }

    private void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
        initializeAppData();
    }

    private void initializeAppData() {
        MyApplication.pName = getString(R.string.your_name);
        MyApplication.pAboutYou = getString(R.string.about_you);
        MyApplication.pAddress = getString(R.string.your_address);
        MyApplication.number = getString(R.string._123_456_7890);
        MyApplication.userName = getString(R.string.username_123456);
        MyApplication.bName = getString(R.string.business_name);
        MyApplication.bAbout = getString(R.string.about_business);
        MyApplication.bAddress = getString(R.string.business_address);
    }

    public void openLinkInBrowser(Context context, String url) {
        if (url.isEmpty()) {
            Toast.makeText(context, "URL is Invalid or empty", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyStatusBarGradient() {
        Window window = getWindow();
        if (window == null) return;

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        Drawable background = ContextCompat.getDrawable(this, R.drawable.bg_gradient_status_bar);
        if (background != null) {
            window.setBackgroundDrawable(background);
        }

        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            uiFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(uiFlags);
    }

    public void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rateApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(intent);
        }
    }
}
