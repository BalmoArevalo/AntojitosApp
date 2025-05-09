package sv.ues.fia.eisi.proyecto01_antojitos.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();

    public LiveData<String> getText() { return mText; }
    public void  setText (String value) { mText.setValue(value); }
}
