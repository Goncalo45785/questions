package pt.goncalo.blissquestions.model;

import android.util.Log;

import java.util.List;

import pt.goncalo.blissquestions.model.entity.Health;
import pt.goncalo.blissquestions.model.entity.Question;
import pt.goncalo.blissquestions.model.webservice.Endpoints;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static final String TAG = Client.class.getSimpleName();
    public static final String BASE_URL = "https://private-bbbe9-blissrecruitmentapi.apiary-mock"
            + ".com/";

    private Retrofit retrofit;
    private Endpoints api;


    Client() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                GsonConverterFactory.create())
                .build();
        api = retrofit.create(Endpoints.class);
    }

    void getServerHealth(Callback<Health> callback) {
        Log.i(TAG, "getServerHealth Requesting...");
        api.getHealth().enqueue(callback);
    }

    void getQuestions(int limit, int offset, String filter, Callback<List<Question>> callback) {
        Log.i(TAG, "getQuestions Requesting...");
        api.getQuestions(limit, offset, filter).enqueue(callback);
    }

    void getQuestionById(int id, Callback<Question> callback) {
        Log.i(TAG, "getQuestionById Requesting...");
        api.getQuestion(id).enqueue(callback);
    }

    void vote(int id, String choice, Callback<Question> callback) {
        Log.i(TAG, "vote Requesting...");
        api.vote(id, choice).enqueue(callback);
    }

    void share(String email, String url, Callback<Health> callback) {
        api.share(email, url).enqueue(callback);
    }
}
