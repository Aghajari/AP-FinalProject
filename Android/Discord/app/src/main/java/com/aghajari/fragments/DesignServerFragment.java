package com.aghajari.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.adapters.DesignServerAdapter;
import com.aghajari.design_server;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

public class DesignServerFragment extends BaseFragment<design_server> {

    public static DesignServerFragment newInstance(ServerModel profile) {
        DesignServerFragment fragment = new DesignServerFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public design_server create(Context context) {
        return new design_server(context);
    }

    @Override
    public void bind() {
        if (getArguments() == null)
            return;

        ServerModel profile = (ServerModel) getArguments().getSerializable("profile");

        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        view.back.setOnClickListener(v -> back());
        view.rv.setLayoutManager(new LinearLayoutManager(getContext()));

        DesignServerAdapter adapter = new DesignServerAdapter(profile, this);
        view.rv.setAdapter(adapter);
        Utils.addSpace(view.rv, 18, 80);

        view.add.setOnClickListener(v -> adapter.add());

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.START | ItemTouchHelper.END
                );
            }

            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                adapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.delete(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(view.rv);

        view.save.setOnClickListener(v -> {
            adapter.update(profile);
            EasyApi.updateServerChannels(profile);
            back();
        });
    }
}
