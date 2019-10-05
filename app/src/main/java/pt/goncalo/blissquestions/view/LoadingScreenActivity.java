package pt.goncalo.blissquestions.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.model.Client;

public class LoadingScreenActivity extends AppCompatActivity {
    private final String TAG = LoadingScreenActivity.class.getSimpleName();
    TextView statusTv;
    Button retryButton;
    ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        assignViews();

        Client.getInstance().getServerHealth().observe(this, this::onServerHealthChanged);
    }

    private void onServerHealthChanged(boolean isHealthy) {
        if (statusTv != null) {
            if (isHealthy) {
                statusTv.setText(getText(R.string.status_ok));
                statusTv.setTextColor(Color.GREEN);
                (new Handler()).postDelayed(this::startListActivity, 1500);
            } else {
                statusTv.setText(getText(R.string.status_nok));
                statusTv.setTextColor(Color.RED);
            }
            setRetryButtonVisibility(!isHealthy);
        }
    }

    private void setRetryButtonVisibility(boolean show){
        if (show) {
            retryButton.setVisibility(View.VISIBLE);
            loadingSpinner.setVisibility(View.INVISIBLE);
        } else {
            retryButton.setVisibility(View.INVISIBLE);
            loadingSpinner.setVisibility(View.VISIBLE);
        }
    }

    private void assignViews() {
        statusTv = findViewById(R.id.statusTv);
        retryButton = findViewById(R.id.retryButton);
        loadingSpinner = findViewById(R.id.loadingSpinner);

        retryButton.setOnClickListener(v -> {
            Client.getInstance().getServerHealth();
            setRetryButtonVisibility(false);
        });
    }

    private void startListActivity() {
        //TODO
    }
}
