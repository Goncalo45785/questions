package pt.goncalo.blissquestions.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private MutableLiveData<List<Question>> unfilteredQuestions;
    private MutableLiveData<List<Question>> filteredQuestions;
    private HashMap<Integer, MutableLiveData<Question>> questionDetailCache;

    private int loadedFilteredQuestions, loadedUnfilteredQuestions = 0;

    private QuestionRepository() {
        apiClient = new Client();
        questionDetailCache = new HashMap<>();
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
                    Log.i(TAG, String.format("getServiceReadyState Bad Response [%s]", statusCode));
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


    public LiveData<Boolean> share(String email, String url) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        apiClient.share(email, url, new Callback<Health>() {
            @Override
            public void onResponse(Call<Health> call, Response<Health> response) {
                int statusCode = response.code();
                Health healthStatus = response.body();
                if (healthStatus != null) {
                    Log.i(TAG, String.format("share Response [%s] %s", statusCode,
                            healthStatus.getStatus()));
                    result.postValue(healthStatus.getStatus().equals(Health.STATUS_OK));
                } else {
                    Log.i(TAG, String.format("share Bad Response [%s]", statusCode));
                    result.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Health> call, Throwable t) {
                Log.wtf(TAG, String.format("share Failed: %s", t.getMessage()));
                result.postValue(false);
            }
        });
        return result;
    }

    public LiveData<Question> vote(int questionId, String choice) {
        MutableLiveData<Question> result = new MutableLiveData<>();
        apiClient.vote(questionId, choice, new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                int statusCode = response.code();
                Question question = response.body();
                if (question != null) {
                    Log.i(TAG, String.format("vote Response [%s] Choice: %s",
                            statusCode,
                            choice));
                    result.postValue(question);
                } else {
                    Log.i(TAG, String.format("vote Bad Response [%s]", statusCode));
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.i(TAG, String.format("vote Failed: %s", t.getMessage()));
            }
        });
        return result;
    }

    public LiveData<Question> getQuestionById(int id) {
        if (!questionDetailCache.containsKey(id)) {
            questionDetailCache.put(id, new MutableLiveData<>());
        }

        MutableLiveData<Question> result = questionDetailCache.get(id);
        if (result.getValue() == null) {
            apiClient.getQuestionById(id, new Callback<Question>() {
                @Override
                public void onResponse(Call<Question> call, Response<Question> response) {
                    int statusCode = response.code();
                    Question question = response.body();
                    if (question != null) {
                        Log.i(TAG, String.format("getQuestionById Response [%s] Question: %s",
                                statusCode,
                                question.getQuestion()));
                        result.postValue(question);
                    } else {
                        Log.i(TAG, String.format("getQuestionById Bad Response [%s]", statusCode));
                    }
                }

                @Override
                public void onFailure(Call<Question> call, Throwable t) {
                    Log.i(TAG, String.format("getQuestionById Failed: %s", t.getMessage()));
                }
            });
        }

        return result;
    }

    public LiveData<List<Question>> getQuestionsWithFilter(String filter) {
        return getQuestions(filter);
    }

    public LiveData<List<Question>> getQuestions() {
        return getQuestions(null);
    }

    public List<Question> getLastKnownQuestions() {
        return unfilteredQuestions.getValue();
    }

    public void clearUnfilteredQuestions() {
        if (filteredQuestions != null) {
            unfilteredQuestions.setValue(new ArrayList<>(0));
            loadedUnfilteredQuestions = 0;
        }
    }

    public void clearFilteredQuestions() {
        if (filteredQuestions != null) {
            filteredQuestions.setValue(new ArrayList<>(0));
            loadedFilteredQuestions = 0;
        }
    }

    private LiveData<List<Question>> getQuestions(String filter) {
        boolean isFiltered = filter != null;
        initQuestionLists(isFiltered);

        apiClient.getQuestions(REQUEST_LIMIT, loadedUnfilteredQuestions, filter,
                new Callback<List<Question>>() {
                    @Override
                    public void onResponse(Call<List<Question>> call,
                            Response<List<Question>> response) {
                        int statusCode = response.code();
                        List<Question> questionList = response.body();
                        if (questionList != null) {
                            Log.i(TAG,
                                    String.format("getQuestions Response [%s] Size: %s", statusCode,
                                            questionList.size()));
                            updateQuestionLists(isFiltered, questionList);
                        } else {
                            Log.i(TAG, String.format("getQuestions Bad Response [%s]", statusCode));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Question>> call, Throwable t) {
                        Log.i(TAG, String.format("getQuestions Failed: %s", t.getMessage()));
                    }
                });
        return isFiltered ? filteredQuestions : unfilteredQuestions;
    }

    public boolean hasUnfilteredQuestions() {
        return loadedUnfilteredQuestions > 0;
    }

    public boolean hasFilteredQuestions() {
        return loadedFilteredQuestions > 0;
    }

    private void initQuestionLists(boolean isFiltered) {
        if (isFiltered) {
            if (filteredQuestions == null) {
                filteredQuestions = new MutableLiveData<>();
                filteredQuestions.setValue(new ArrayList<>(0));
            }
        } else {
            if (unfilteredQuestions == null) {
                unfilteredQuestions = new MutableLiveData<>();
                unfilteredQuestions.setValue(new ArrayList<>(0));
            }
        }
    }

    private void updateQuestionLists(boolean isFiltered, List<Question> newQuestions) {
        if (!newQuestions.isEmpty()) {
            if (isFiltered) {
                List<Question> list = filteredQuestions.getValue();
                if (list.addAll(newQuestions)) {
                    filteredQuestions.postValue(list);
                    loadedFilteredQuestions = list.size();
                }
            } else {
                List<Question> list = unfilteredQuestions.getValue();
                if (list.addAll(newQuestions)) {
                    unfilteredQuestions.postValue(list);
                    loadedUnfilteredQuestions = list.size();
                }
            }
        }
    }


}
