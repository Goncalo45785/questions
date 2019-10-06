package pt.goncalo.blissquestions.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;

import java.util.List;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.model.entity.Question;
import pt.goncalo.blissquestions.view.adapter.QuestionListAdapter;
import pt.goncalo.blissquestions.viewmodel.QuestionViewModel;

public class QuestionListActivity extends AppCompatActivity {
    private final String TAG = QuestionListActivity.class.getSimpleName();

    private QuestionViewModel questionViewModel;

    private SearchView searchView;
    private Button loadMoreBtn;
    private RecyclerView listView;
    private Adapter listAdapter;
    private LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        assignViews();

        questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);

        continueWithStartingIntent(getIntent());
    }

    private void continueWithStartingIntent(Intent intent) {
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
            questionViewModel.getQuestions().observe(this, this::onQuestionsReceived);

        }
    }

    private void onQuestionsReceived(List<Question> questions) {
        Log.i(TAG, questions.size() + "");
        if (listAdapter == null) {
            listAdapter = new QuestionListAdapter(questions);
            listView.setAdapter(listAdapter);
        }
        listAdapter.notifyDataSetChanged();
    }

    private void assignViews() {
        loadMoreBtn = findViewById(R.id.loadMoreBtn);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        loadMoreBtn.setOnClickListener(v -> questionViewModel.getQuestions());

        listView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
    }


}
