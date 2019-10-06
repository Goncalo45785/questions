package pt.goncalo.blissquestions.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pt.goncalo.blissquestions.model.entity.Health;
import pt.goncalo.blissquestions.model.entity.Question;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionRepository {
    public static final int REQUEST_LIMIT = 10;

    private final String TAG = QuestionRepository.class.getSimpleName();
    private static QuestionRepository singleInstance;
    private Client apiClient;

    private MutableLiveData<Boolean> serverHealth;
    private MutableLiveData<List<Question>> questions;

    private int loadedQuestions;

    private QuestionRepository() {
        apiClient = new Client();
    }

    public static QuestionRepository getInstance() {
        if (singleInstance == null) {
            singleInstance = new QuestionRepository();
        }
        return singleInstance;
    }

    public LiveData<Boolean> getServiceReadyState() {
        if (serverHealth == null) {
            serverHealth = new MutableLiveData<>();
        }

        apiClient.getServerHealth(new Callback<Health>() {
            @Override
            public void onResponse(Call<Health> call, Response<Health> response) {
                int statusCode = response.code();
                Health healthStatus = response.body();
                if (healthStatus != null) {
                    Log.i(TAG, String.format("getServiceReadyState Response [%s] %s", statusCode,
                            healthStatus.getStatus()));

                    serverHealth.postValue(healthStatus.getStatus().equals(Health.STATUS_OK));
                } else {
                    Log.i(TAG, String.format("getServiceReadyState Response [%s]", statusCode));

                    serverHealth.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Health> call, Throwable t) {
                Log.wtf(TAG, String.format("getServiceReadyState Failed: %s", t.getMessage()));
                serverHealth.postValue(false);
            }
        });
        return serverHealth;
    }

    public LiveData<List<Question>> getQuestions(String filter) {
        if (questions == null) {
            questions = new MutableLiveData<>();
            questions.setValue(new LinkedList<>());
        }

        apiClient.getQuestions(REQUEST_LIMIT, loadedQuestions, filter, new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                int statusCode = response.code();
                List<Question> questionList = response.body();
                if (questionList != null) {
                    Log.i(TAG, String.format("getQuestions Response [%s] Size: %s", statusCode,
                            questionList.size()));
                    if (!questionList.isEmpty()) {
                        List<Question> list = questions.getValue();
                        if (list.addAll(questionList)) {
                            questions.postValue(list);
                            loadedQuestions = list.size();
                        }
                    }
                } else {
                    Log.i(TAG, String.format("getQuestions Response [%s]", statusCode));
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.i(TAG, String.format("getQuestionsFailed: %s", t.getMessage()));
            }
        });
        return questions;
    }
}
