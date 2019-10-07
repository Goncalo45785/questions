package pt.goncalo.blissquestions.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import pt.goncalo.blissquestions.components.LiveNetworkState;

public class ConnectivityViewModel extends AndroidViewModel {
    private LiveNetworkState networkState;

    public LiveNetworkState getNetworkState() {
        return networkState;
    }

    public ConnectivityViewModel(@NonNull Application application) {
        super(application);
        networkState = new LiveNetworkState(application);
    }
}
