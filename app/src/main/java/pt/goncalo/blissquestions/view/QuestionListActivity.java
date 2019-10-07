package pt.goncalo.blissquestions.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import java.util.List;

import pt.goncalo.blissquestions.R;
import pt.goncalo.blissquestions.model.entity.Question;
import pt.goncalo.blissquestions.utils.ActivityUtils;
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

        continueWithIntent(getIntent());
    }

    private void continueWithIntent(Intent intent) {
        if (ActivityUtils.isUrlIntent(intent)) {
            handleIntentFromURL(intent);
        } else {
            Log.i(TAG, "Activity not started from URL");
            questionViewModel.getQuestions().observe(this, this::onUnfilteredQuestionsReceived);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isSearchIntent(intent)) {
            handleIntentFromSearch(intent);
        } else if (ActivityUtils.isUrlIntent(intent)) {
            handleIntentFromURL(intent);
        }
    }

    private boolean isSearchIntent(Intent intent) {
        return Intent.ACTION_SEARCH.equals(intent.getAction());
    }

    private void handleIntentFromURL(Intent intent) {
        String query = intent.getData().getQueryParameter(getString(R.string.query_filter));
        Log.i(TAG, String.format("Handling intent from url with param = %s", query));

        if (query == null) {
            /*If the question_filter parameter is missing the user should simply be placed at the
            listing. hasUnfilteredQuestions is used to avoid re-fetching questions.*/
            if (!questionViewModel.hasQuestions()) {
                questionViewModel.getQuestions();
            } else if (questionViewModel.isInSearchMode()){
                endSearch();
            }
        } else {
            /* fill the search box and trigger the search functionality. If the question_filter
            parameter is present but has an empty value the the user should be placed at the
            filter variant with no input inserted but with the input box focused */

            if (query.isEmpty()) {
                /* when the query is an empty string, submitting the search will not trigger the
                search intent*/
                searchView.requestFocus();
                searchView.setQuery(query, false);
                searchView.setIconified(false);
            } else {
                searchView.setQuery(query, true);
                searchView.setIconified(false);
            }
        }
    }

    private void handleIntentFromSearch(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        Log.i(TAG, String.format("Handling intent from search with param = %s", query));
        questionViewModel.getQuestionsForFilter(query).observe(this,
                    this::onFilteredQuestionsReceived);
    }

    private void endSearch() {
        Log.i(TAG, "Search closed. Clearing.");
        questionViewModel.clearSearch();
        List<Question> lastKnownQuestions = questionViewModel.getCachedQuestions();
        if (questionViewModel.isInSearchMode() && lastKnownQuestions != null && !lastKnownQuestions.isEmpty()) {
            initListAdapter(lastKnownQuestions);
            questionViewModel.setSearchMode(false);
//            getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(
//                    Color.GREEN);
        } else if (lastKnownQuestions == null || lastKnownQuestions.isEmpty()){
            questionViewModel.getQuestions();
        }
    }

    private void onFilteredQuestionsReceived(List<Question> filteredQuestions) {
        if (listAdapter == null || !questionViewModel.isInSearchMode()) {
            initListAdapter(filteredQuestions);
//            getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(
//                    Color.RED);
            questionViewModel.setSearchMode(true);
        }
        listAdapter.notifyDataSetChanged();
    }

    private void onUnfilteredQuestionsReceived(List<Question> unfilteredQuestions) {
        if (listAdapter == null || questionViewModel.isInSearchMode()) {
            initListAdapter(unfilteredQuestions);
//            getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(
//                    Color.GREEN);
            questionViewModel.setSearchMode(false);
        }
        listAdapter.notifyDataSetChanged();
    }

    private void initListAdapter(List<Question> questions) {
        listAdapter = new QuestionListAdapter(questions);
        listView.setAdapter(listAdapter);
    }

    private void assignViews() {
        loadMoreBtn = findViewById(R.id.loadMoreBtn);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        loadMoreBtn.setOnClickListener(v -> questionViewModel.getQuestions());

        listView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by
        // default
        searchView.setOnCloseListener(() -> {
            endSearch();
            //returning false will allow the system to take care of clearing and closing the view
            return false;
        });
    }


}
