package com.festival.dailypostermaker.MyUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.lang.ref.WeakReference;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private WeakReference<NetworkChangeListener> listenerRef;

    public NetworkChangeReceiver(NetworkChangeListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            NetworkChangeListener listener = listenerRef.get();
            if (listener != null) {
                listener.onNetworkChange(NetworkUtil.isNetworkConnected(context));
            }
        }
    }

    public interface NetworkChangeListener {
        void onNetworkChange(boolean isConnected);
    }
}
