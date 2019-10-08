package pt.goncalo.blissquestions.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import pt.goncalo.blissquestions.model.QuestionRepository;
import pt.goncalo.blissquestions.model.entity.Choice;
import pt.goncalo.blissquestions.model.entity.Question;

public class DetailViewModel extends ViewModel {
    private QuestionRepository questionRepository;
    private int questionId = -1;

    public DetailViewModel() {
        questionRepository = QuestionRepository.getInstance();
    }

    public LiveData<Question> getQuestionById(int id) {
        questionId = id;
        return questionRepository.getQuestionById(id);
    }

    public LiveData<Question> vote(String choice) {
        return questionRepository.vote(questionId, choice);
    }

    public int getQuestionId() {
        return questionId;
    }
}
