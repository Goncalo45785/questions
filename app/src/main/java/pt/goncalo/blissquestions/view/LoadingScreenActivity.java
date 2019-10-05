package pt.goncalo.blissquestions.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.model.Client;
import pt.goncalo.blissquestions.viewmodel.ConnectivityViewModel;

public class LoadingScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);


        Client.getInstance().getServerHealth().observe(this, this::onServerHealthChanged);
    }

    private void onServerHealthChanged(boolean isHealthy) {
        TextView statusTv = findViewById(R.id.statustv);
        if (statusTv != null) {
            if (isHealthy) {
                statusTv.setText(getText(R.string.status_ok));
                statusTv.setTextColor(Color.GREEN);
            } else {
                statusTv.setText(getText(R.string.status_nok));
                statusTv.setTextColor(Color.RED);
            }

        }
    }
}
