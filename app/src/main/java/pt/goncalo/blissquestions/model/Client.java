package pt.goncalo.blissquestions.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static final String TAG = "Client";
    public static final String BASE_URL = "https://private-bbbe9-blissrecruitmentapi.apiary-mock"
            + ".com/";

    private static Client singleInstance;
    private Retrofit retrofit;
    private Endpoints api;

    private Client() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                GsonConverterFactory.create())
                .build();
        api = retrofit.create(Endpoints.class);
    }

    public static Client getInstance() {
        if (singleInstance == null) {
            singleInstance = new Client();
        }
        return singleInstance;
    }

    public LiveData<Boolean> getServerHealth() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        Log.i(TAG, "getServerHealth Requesting...");
        api.getHealth().enqueue(new Callback<Health>() {
            @Override
            public void onResponse(Call<Health> call, Response<Health> response) {
                int statusCode = response.code();
                Health healthStatus = response.body();
                Log.i(TAG, String.format("getServerHealth Response [%s] %s", statusCode,
                        healthStatus.getStatus()));
                result.postValue(healthStatus.getStatus().equals(Health.STATUS_OK));
            }

            @Override
            public void onFailure(Call<Health> call, Throwable t) {
                Log.wtf(TAG, "getServerHealth Failed");
                result.postValue(false);
            }
        });
        return result;
    }
}
