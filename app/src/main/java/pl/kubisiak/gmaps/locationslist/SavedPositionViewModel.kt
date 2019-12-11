package pl.kubisiak.gmaps.locationslist

import androidx.databinding.Bindable
import pl.kubisiak.gmaps.SavedPosition

class SavedPositionViewModel(@Bindable val position : SavedPosition, private val parent: SavedLocationsListViewModel) : BaseViewModel() {
    fun goToDetails() {
        parent.result.postValue(position.date)
    }
}