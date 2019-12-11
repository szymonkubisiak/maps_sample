package pl.kubisiak.gmaps.locationslist

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import pl.kubisiak.gmaps.R


class SavedLocationsListFragment : BaseFragment() {

    override fun createViewModel(): BaseViewModel {
        val retval = ViewModelProviders
            .of(this)
            .get(SavedLocationsListViewModel::class.java)

        retval.result.observe(this, Observer {
            activity?.apply {
                val output = Intent()
                output.putExtra("date", it)
                setResult(Activity.RESULT_OK, output)
                finish()
            }
        })

        return retval
    }

    override fun getLayoutRes() =
        R.layout.fragment_saved_locations_list

    companion object {
        fun newInstance() = SavedLocationsListFragment()
    }
}