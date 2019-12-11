package pl.kubisiak.gmaps.recyclerview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pl.kubisiak.gmaps.locationslist.BaseViewModel;

public class BindingAdapterRecyclerView {

    @BindingAdapter({"items"})
    public static void bindItemsThroughViewModelAdapter(@NonNull RecyclerView recyclerView, @Nullable List<BaseViewModel> items) {
        if (items != null ) {
            recyclerView.setAdapter(new ViewModelAdapter(items));
        } else {
            recyclerView.setAdapter(null);
        }
    }

    @BindingAdapter({"vertical"})
    public static void setRecyclerViewOrientation(@NonNull RecyclerView recyclerView, @Nullable Boolean isVertical) {
        if(isVertical == null) isVertical = true;
        Context recyclerViewContext = recyclerView.getContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerViewContext, isVertical?RecyclerView.VERTICAL:RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

    }
}
