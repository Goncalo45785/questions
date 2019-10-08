package pt.goncalo.blissquestions.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.components.NetworkStatus;
import pt.goncalo.blissquestions.utils.ActivityUtils;
import pt.goncalo.blissquestions.viewmodel.DetailViewModel;
import pt.goncalo.blissquestions.viewmodel.QuestionViewModel;

public class ShareActivity extends NetworkAwareActivity {
    private String urlToShare;
    private Button sendMailBtn, chooseAppBtn;
    private EditText emailEt;
    private QuestionViewModel questionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        assignViews();
        enableOverlayOnNetworkChange(NetworkStatus.DISCONNECTED);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(getString(R.string.share_url_extra_key))) {
            String url = intent.getStringExtra(getString(R.string.share_url_extra_key));
            if (!url.isEmpty()) {
                urlToShare = url;
            } else {
                finish();
            }
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.share_url_extra_key), urlToShare);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        urlToShare = savedInstanceState.getString(getString(R.string.share_url_extra_key));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void assignViews() {
        sendMailBtn = findViewById(R.id.share_send);
        chooseAppBtn = findViewById(R.id.share_pick);
        emailEt = findViewById(R.id.share_email);

        sendMailBtn.setOnClickListener(v -> questionViewModel.share(emailEt.getText().toString(),
                urlToShare).observe(this, success -> {
            handleSendMailResponse(success);
            sendMailBtn.setEnabled(false);
        }));

        chooseAppBtn.setOnClickListener(v -> ActivityUtils.shareUrlByMail(this, urlToShare));

        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ActivityUtils.isEmailValid(s.toString())) {
                    sendMailBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void handleSendMailResponse(Boolean success) {
        CharSequence text;
        if (success) {
            text = getString(R.string.share_email_sent);
            finish();
        } else {
            text = getString(R.string.share_email_failed);
            sendMailBtn.setEnabled(true);
        }
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }


}
