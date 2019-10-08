package pt.goncalo.blissquestions.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.components.NetworkStatus;
import pt.goncalo.blissquestions.model.Client;
import pt.goncalo.blissquestions.viewmodel.QuestionViewModel;

public class LoadingScreenActivity extends NetworkAwareActivity {
    private final String TAG = LoadingScreenActivity.class.getSimpleName();
    private QuestionViewModel questionVm;
    private TextView statusTv;
    private Button retryButton;
    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        enableOverlayOnNetworkChange(NetworkStatus.DISCONNECTED);
        assignViews();

        questionVm = ViewModelProviders.of(this).get(QuestionViewModel.class);

        questionVm.getServiceReadyState().observe(this, this::onServiceReadyStateChanged);
    }

    private void onServiceReadyStateChanged(boolean isHealthy) {
        if (statusTv != null) {
            if (isHealthy) {
                statusTv.setText(getText(R.string.status_ok));
                statusTv.setTextColor(Color.GREEN);
                startListActivity();
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
            questionVm.getServiceReadyState();
            setRetryButtonVisibility(false);
        });
    }

    private void startListActivity() {
        Intent intent = new Intent(this, QuestionListActivity.class);
        startActivity(intent);
        finish();
    }
}
