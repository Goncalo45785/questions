package pt.goncalo.blissquestions.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import pt.goncalo.blissquestions.R;

public class QuestionListActivity extends AppCompatActivity {
    private final String TAG = QuestionListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String queryParam = intent.getData().getQueryParameter(
                    getString(R.string.query_filter));
            Log.i(TAG, String.format("Activity started from url with param = %s", queryParam));

            /*TODO: Notice that this format contains a query parameter which should be used to fill
            the search box and trigger the search functionality. If the question_filter parameter
             is missing the user should simply be placed at the listing. If the question_filter
             parameter is present but has an empty value the the user should be placed at the
             filter variant with no input inserted but with the input box focused*/
        } else {
            Log.i(TAG, "Activity not started from URL");
        }
    }


}
