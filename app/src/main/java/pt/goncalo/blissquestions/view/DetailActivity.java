package pt.goncalo.blissquestions.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.util.List;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.model.entity.Choice;
import pt.goncalo.blissquestions.model.entity.Question;
import pt.goncalo.blissquestions.utils.ActivityUtils;
import pt.goncalo.blissquestions.viewmodel.DetailViewModel;

public class DetailActivity extends AppCompatActivity {
    private final String TAG = DetailActivity.class.getSimpleName();
    private final int EXTRA_DEFAULT = -1;

    private DetailViewModel detailViewModel;

    private TextView titleTv, dateTv, idTv;
    private ImageView image;
    private Button submitBtn;
    private RadioGroup choicesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        assignViews();

        detailViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(getString(R.string.question_id_extra_key))) {
            int questionId = intent.getIntExtra(getString(R.string.question_id_extra_key), EXTRA_DEFAULT);
            if (questionId != EXTRA_DEFAULT) {
                detailViewModel.getQuestionById(questionId).observe(this,
                        question -> onQuestionDetailChanged(question, true));
                return;
            }
        } else if (ActivityUtils.isUrlIntent(intent)) {
            String query = intent.getData().getQueryParameter(getString(R.string.query_question_id));
            Log.i(TAG, String.format("Handling intent from url with param = %s", query));
            if (query == null || query.isEmpty()) {
                finish();
                return;
            }
            int questionId;
            try {
                questionId = Integer.valueOf(query);
            } catch (NumberFormatException nfe) {
                finish();
                return;
            }
            detailViewModel.getQuestionById(questionId).observe(this,
                    question -> onQuestionDetailChanged(question, true));
            return;
        }
        finish();
    }

    private void submitAnswer(List<Choice> choices) {
        RadioButton chosenBtn =
                choicesContainer.findViewById(choicesContainer.getCheckedRadioButtonId());
        String clearChoice = "";
        String[] brokenChoice =
                chosenBtn.getText().toString().split(getString(R.string.detail_votes));
        if (brokenChoice.length >= 1) {
            clearChoice = brokenChoice[0];
        }
        Log.i(TAG, String.format("Voting on answer %s", clearChoice));
        if (!clearChoice.isEmpty()) {
            detailViewModel.vote(clearChoice).observe(this,
                    question -> onQuestionDetailChanged(question, false));
        }
    }

    private void onQuestionDetailChanged(Question question, boolean enableChoice) {
        if (question != null) {
            Picasso.get()
                    .load(question.getImageUrl())
                    .placeholder(R.drawable.ic_broken_image_primary_24dp)
                    .error(R.drawable.ic_broken_image_primary_24dp)
                    .into(image);
            titleTv.setText(question.getQuestion());
            dateTv.setText(dateTv.getText() + question.getPublishedAt());
            idTv.setText(idTv.getText() + String.valueOf(question.getId()));
            buildChoices(question.getChoices(), enableChoice);
            submitBtn.setEnabled(enableChoice);
        }
    }

    private void buildChoices(List<Choice> choices, boolean enableChoice) {
        submitBtn.setOnClickListener(v -> submitAnswer(choices));
        choicesContainer.removeAllViews();
        for (Choice choice : choices) {
            RadioButton radioButton = new RadioButton(this);

            String description = choice.getChoice();
            String votes = String.valueOf(choice.getVotes());
            String label = getString(R.string.detail_votes);
            String builtString = String.format("%s%s %s", description,
                    label, votes);
            Spannable textToSpan = new SpannableString(builtString);

            textToSpan.setSpan(new ForegroundColorSpan(getColor(R.color.lightGray)),
                    builtString.length() - (label.length() + votes.length() + 1),
                    builtString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            radioButton.setText(textToSpan);
            radioButton.setEnabled(enableChoice);
            choicesContainer.addView(radioButton);
        }
    }

    private void assignViews() {
        titleTv = findViewById(R.id.detail_title);
        dateTv = findViewById(R.id.detail_date);
        idTv = findViewById(R.id.detail_id);
        image = findViewById(R.id.detail_image);
        submitBtn = findViewById(R.id.detail_submit);
        choicesContainer = findViewById(R.id.detail_choice_group);

        choicesContainer.setOnCheckedChangeListener((group, checkedId) -> {
            Log.i(TAG, "checked: " + checkedId);
            submitBtn.setEnabled(true);
        });
    }

}
