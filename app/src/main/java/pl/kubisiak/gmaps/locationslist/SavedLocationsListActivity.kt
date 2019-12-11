package pl.kubisiak.gmaps.locationslist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.kubisiak.gmaps.R


class SavedLocationsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_locations_list)
        if (savedInstanceState == null) {
            replaceFragment(SavedLocationsListFragment.newInstance(), false)
        }
    }

    fun replaceFragment(newFragment: BaseFragment, addToBackstack: Boolean = true) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, newFragment, newFragment.generateTag())
            .apply {
                if (addToBackstack) {
                    addToBackStack(null)
                    commitAllowingStateLoss()
                    supportFragmentManager.executePendingTransactions()
                }
                else
                    commitNow()
            }
    }
}
