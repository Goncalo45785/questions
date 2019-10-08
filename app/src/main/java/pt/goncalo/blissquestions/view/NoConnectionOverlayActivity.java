package pt.goncalo.blissquestions.view;

import android.content.Intent;
import android.os.Bundle;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.components.NetworkStatus;

public class NoConnectionOverlayActivity extends NetworkAwareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection_overlay);
        enableOverlayOnNetworkChange(NetworkStatus.CONNECTED);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
