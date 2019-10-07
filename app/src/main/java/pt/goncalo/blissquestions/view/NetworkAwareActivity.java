package pt.goncalo.blissquestions.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pt.goncalo.blissquestions.components.NetworkStatus;
import pt.goncalo.blissquestions.components.LiveNetworkState;

public abstract class NetworkAwareActivity extends AppCompatActivity {
    private LiveNetworkState networkState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void enableOverlayOnNetworkChange(NetworkStatus triggerStatus) {
        if (networkState == null) {
            networkState = new LiveNetworkState(this.getApplication());
            networkState.observe(this, connected -> actOnTrigger(triggerStatus, connected));
        }
    }

    private void actOnTrigger(NetworkStatus trigger, boolean isConnected) {
        if (trigger == NetworkStatus.CONNECTED && isConnected) {
            Log.wtf("LiveNetworkState", "FINISHING ACT");
            finish();
        } else if (trigger == NetworkStatus.DISCONNECTED && !isConnected) {
            Intent intent = new Intent(this, NoConnectionOverlayActivity.class);
            startActivity(intent);
        }
    }
}
