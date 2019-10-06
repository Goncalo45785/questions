package pt.goncalo.blissquestions.model.webservice;

import java.util.List;

import pt.goncalo.blissquestions.model.entity.Health;
import pt.goncalo.blissquestions.model.entity.Question;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Endpoints {
    // Request method and URL specified in the annotation

    @GET("health")
    Call<Health> getHealth();

    @GET("questions")
    Call<List<Question>> getQuestions(@Query("limit") int limit, @Query("offset") int offset,
            @Query("filter") String filter);
}
