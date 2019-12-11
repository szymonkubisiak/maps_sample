package pl.kubisiak.gmaps.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.library.baseAdapters.BR
import pl.kubisiak.gmaps.locationslist.BaseViewModel

class ViewModelViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: BaseViewModel) {
        binding.setVariable(BR.vm, viewModel)
        binding.executePendingBindings()
    }
}
