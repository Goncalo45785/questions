package pt.goncalo.blissquestions.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pt.goncalo.blissquestions.model.QuestionRepository;
import pt.goncalo.blissquestions.model.entity.Question;

public class QuestionViewModel extends ViewModel {
    private QuestionRepository questionRepository;
    private boolean isInSearchMode;

    public QuestionViewModel() {
        questionRepository = QuestionRepository.getInstance();
    }


    public LiveData<Boolean> getServiceReadyState() {
        return questionRepository.getServiceReadyState();
    }

    public LiveData<List<Question>> getQuestionsForFilter(String filter) {
        return questionRepository.getQuestionsWithFilter(filter);
    }

    public LiveData<List<Question>> getQuestions() {
        return questionRepository.getQuestions();
    }

    public boolean hasQuestions() {
        return questionRepository.hasUnfilteredQuestions();
    }

    public void setSearchMode(boolean inSearchMode) {
        this.isInSearchMode = inSearchMode;
    }

    public boolean isInSearchMode() {
        return isInSearchMode;
    }

    public List<Question> getCachedQuestions() {
        return questionRepository.getLastKnownQuestions();
    }

    public void clearSearch() {
        questionRepository.clearFilteredQuestions();
    }

    public LiveData<Boolean> share(String email, String url) {
        return questionRepository.share(email, url);
    }
}
