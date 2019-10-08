package pt.goncalo.blissquestions.components;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import pt.goncalo.blissquestions.view.NoConnectionOverlayActivity;

public class ConnectivityStatusReceiver extends BroadcastReceiver {

    private NetworkStatus triggerStatus;

    public ConnectivityStatusReceiver(NetworkStatus triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TESTE", "TESTE");
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null && triggerStatus == NetworkStatus.CONNECTED) {
            Toast.makeText(context, activeNetworkInfo.getTypeName() + " connected", Toast.LENGTH_SHORT).show();
            Activity activity = (Activity) context;
            activity.finish();
        } else if (activeNetworkInfo == null && triggerStatus == NetworkStatus.DISCONNECTED){
            intent = new Intent(context, NoConnectionOverlayActivity.class);
            context.startActivity(intent);
            Toast.makeText(context, "No Internet or Network connection available", Toast.LENGTH_LONG).show();
        }
    }

}
