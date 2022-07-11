package com.aghajari.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.R;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.views.Utils;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

public abstract class BaseAdapter<V extends BaseAdapter.VH>
        extends RecyclerView.Adapter<V> {

    protected final BaseFragment<?> base;
    private boolean loading = false;

    protected BaseAdapter(BaseFragment<?> base) {
        this.base = base;
    }

    @NonNull
    @NotNull
    @Override
    public V onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (size() == 0) {
            if (isLoading() && viewType == -100) {
                FrameLayout frameLayout = new FrameLayout(parent.getContext());
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));

                CircularProgressIndicator indicator = new CircularProgressIndicator(parent.getContext());
                indicator.setIndeterminate(true);
                indicator.setIndicatorColor(parent.getContext().getResources().getColor(R.color.color2));
                indicator.setIndicatorSize(Utils.dp(50));
                indicator.setTrackThickness(Utils.dp(4));

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        Utils.dp(30), Utils.dp(30), loadingGravity());
                lp.topMargin = loadingTopMargin();
                frameLayout.addView(indicator, lp);
                return createViewHolder(frameLayout);

            } else if (viewType == -101) {
                return createEmpty(parent.getContext());
            }
        }
        return create(parent.getContext(), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        if (!isLoading() && size() != 0)
            bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return isLoading() ? 1 : Math.max(supportsEmptyView() ? 1 : 0, size());
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading())
            return -100;

        if (size() == 0 && supportsEmptyView())
            return -101;

        return viewType(position);
    }

    public int viewType(int position) {
        return super.getItemViewType(position);
    }

    public abstract int size();

    public abstract V create(Context context, int viewType);

    public abstract void bind(VH viewHolder, int position);

    public abstract V createEmpty(Context context);

    public abstract V createViewHolder(View view);

    public boolean supportsEmptyView() {
        return true;
    }

    public boolean isLoading() {
        return loading;
    }

    public int loadingTopMargin() {
        return Utils.perY(15);
    }

    public int loadingGravity() {
        return Gravity.CENTER_HORIZONTAL;
    }

    public void setLoading(boolean loading) {
        if (loading == this.loading)
            return;

        this.loading = loading;
        notifyDataSetChanged();
    }

    public static class VH extends RecyclerView.ViewHolder {

        public VH(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public <V extends View> V get(){
            //noinspection unchecked
            return (V) itemView;
        }
    }
}
