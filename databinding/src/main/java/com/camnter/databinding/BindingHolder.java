package com.camnter.databinding;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * @author CaMnter
 */

public class BindingHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    @NonNull
    private final T binding;


    public BindingHolder(@NonNull final T binding) {
        // binding.getRoot() = itemView
        super(binding.getRoot());
        this.binding = binding;
    }


    @NonNull
    public T getBinding() {
        return this.binding;
    }

}