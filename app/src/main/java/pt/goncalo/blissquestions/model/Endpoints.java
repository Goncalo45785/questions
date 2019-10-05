package pt.goncalo.blissquestions.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Endpoints {
    // Request method and URL specified in the annotation

    @GET("health")
    Call<Health> getHealth();
}
