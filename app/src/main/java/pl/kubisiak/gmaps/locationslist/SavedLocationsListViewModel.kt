package pl.kubisiak.gmaps.locationslist

import androidx.databinding.Bindable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import pl.kubisiak.gmaps.MyApplication
import pl.kubisiak.gmaps.persistence.MyDataBase
import java.util.*


class SavedLocationsListViewModel: BaseViewModel(){

    val result = MutableLiveData<Date>()

    private var _list: ObservableList<BaseViewModel>? = ObservableArrayList<BaseViewModel>()
    var list: ObservableList<BaseViewModel>?
        @Bindable get() = _list
        @Bindable set(value) {
            _list = value
            notifyPropertyChanged(BR.list)
        }


    init {
        disposer.add(MyDataBase.getDatabase(MyApplication.getInstance())
            .courseAndNameModel()
            .getItems()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe{
                val retval = ObservableArrayList<BaseViewModel>()
                for (oneLocation in it)
                    retval.add(SavedPositionViewModel(oneLocation, this))
                list = retval
            }
        )
    }
}

