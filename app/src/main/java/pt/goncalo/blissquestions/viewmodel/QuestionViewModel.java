package pt.goncalo.blissquestions.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import pt.goncalo.blissquestions.model.QuestionRepository;
import pt.goncalo.blissquestions.model.entity.Question;

public class QuestionViewModel extends AndroidViewModel {
    private QuestionRepository questionRepository;

    public QuestionViewModel(@NonNull Application application) {
        super(application);
        questionRepository = QuestionRepository.getInstance();
    }


    public LiveData<Boolean> getServiceReadyState() {
        return questionRepository.getServiceReadyState();
    }

    public LiveData<List<Question>> getQuestionsForFilter(String filter) {
        LiveData<List<Question>> result = questionRepository.getQuestions(filter);
        return result;
    }

    public LiveData<List<Question>> getQuestions() {
        return getQuestionsForFilter("");
    }


}
