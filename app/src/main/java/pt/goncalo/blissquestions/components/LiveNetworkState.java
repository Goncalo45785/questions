package pt.goncalo.blissquestions.viewmodel.components;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest.Builder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;

public class LiveNetworkState extends LiveData<Boolean> {
    public static final String TAG = "LiveNetworkState";

    private ConnectivityManager connManager;
    private NetworkCallback networkCallback = new NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            postValue(true);
        }

        @Override
        public void onLost(Network network) {
            postValue(false);
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            Log.i(TAG, String.format("Losing network connection in %d milliseconds.", maxMsToLive));
        }
    };

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public LiveNetworkState(Application application) {
        connManager = (ConnectivityManager) application.getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onActive() {
        super.onActive();

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

        postValue(activeNetwork.isConnected());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            Builder requestBuilder = new Builder();
            connManager.registerNetworkCallback(requestBuilder.build(), networkCallback);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        connManager.unregisterNetworkCallback(networkCallback);
    }
}