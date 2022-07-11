package com.aghajari.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.axanimation.AXAnimation;
import com.aghajari.design_server_item;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class DesignServerAdapter extends BaseAdapter<BaseAdapter.VH> {

    ArrayList<ServerModel.ServerChannel> list = new ArrayList<>();
    RecyclerView rv;

    public DesignServerAdapter(ServerModel serverModel, BaseFragment<?> base) {
        super(base);

        if (serverModel.channels != null) {
            for (ServerModel.ServerChannel c : serverModel.channels) // clone
                list.add(new ServerModel.ServerChannel(c.name, c.id, c.type));
        }
        if (list.isEmpty())
            list.add(new ServerModel.ServerChannel("", Utils.rnd(), 0));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Utils.animate(rv = recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        rv = null;
    }

    public void add() {
        list.add(new ServerModel.ServerChannel("", Utils.rnd(), 0));
        notifyItemInserted(list.size() - 1);
        if (rv != null)
            rv.invalidateItemDecorations();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public VH create(Context context, int viewType) {
        return new VH(new design_server_item(context));
    }

    public void move(int from, int to) {
        Collections.swap(list, from, to);
        notifyItemMoved(from, to);
        if (rv != null)
            rv.invalidateItemDecorations();
    }

    public void delete(int index) {
        list.remove(index);
        notifyItemRemoved(index);
        if (rv != null)
            rv.invalidateItemDecorations();
    }

    public void update(ServerModel serverModel) {
        serverModel.channels = list;
    }

    @Override
    public void bind(VH viewHolder, int position) {
        design_server_item view = viewHolder.get();
        ServerModel.ServerChannel channel = list.get(position);

        view.edt.setText(channel.name);

        if (view.edt.getTag() != null)
            view.edt.removeTextChangedListener((TextWatcher) view.edt.getTag());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (view.edt.getTag() == this && view.edt.isAttachedToWindow())
                    channel.name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        view.edt.addTextChangedListener(textWatcher);
        view.edt.setTag(textWatcher);

        view.channel.setVisibility(channel.type == 0 ? View.VISIBLE : View.GONE);
        view.group.setVisibility(channel.type == 0 ? View.GONE : View.VISIBLE);
        view.channel.setScaleX(1f);
        view.channel.setScaleY(1f);
        view.group.setScaleX(1f);
        view.group.setScaleY(1f);
        view.edt.setHint(channel.type == 0 ? "Channel Name" : "Group Name");

        view.channel.setOnClickListener(v -> {
            channel.type = 1;
            view.edt.setHint("Group Name");

            AXAnimation.create()
                    .firstValueFromView(false)
                    .duration(250)
                    .scale(1f, 0f)
                    .withEndAction(a -> v.setVisibility(View.GONE))
                    .start(v);

            view.group.setVisibility(View.VISIBLE);
            AXAnimation.create()
                    .firstValueFromView(false)
                    .duration(250)
                    .scale(0f, 1f)
                    .start(view.group);
        });

        view.group.setOnClickListener(v -> {
            channel.type = 0;
            view.edt.setHint("Channel Name");

            AXAnimation.create()
                    .firstValueFromView(false)
                    .duration(250)
                    .scale(1f, 0f)
                    .withEndAction(a -> v.setVisibility(View.GONE))
                    .start(v);

            view.channel.setVisibility(View.VISIBLE);
            AXAnimation.create()
                    .firstValueFromView(false)
                    .duration(250)
                    .scale(0f, 1f)
                    .start(view.channel);
        });
    }

    @Override
    public VH createEmpty(Context context) {
        return null;
    }

    @Override
    public boolean supportsEmptyView() {
        return false;
    }

    @Override
    public VH createViewHolder(View view) {
        return new VH(view);
    }

}
