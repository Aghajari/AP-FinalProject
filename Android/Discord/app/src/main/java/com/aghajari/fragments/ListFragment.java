package com.aghajari.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.aghajari.adapters.ListAdapter;
import com.aghajari.adapters.ServerMembersAdapter;
import com.aghajari.list_fragment;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.views.Utils;

public class ListFragment extends BaseFragment<list_fragment> {

    public static ListFragment newInstance(int type) {
        return newInstance(type, null);
    }

    public static ListFragment newInstance(int type, String id) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListFragment newInstance(ServerModel serverModel) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable("server", serverModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public list_fragment create(Context context) {
        return new list_fragment(context);
    }

    @Override
    public void bind() {
        if (getArguments() == null)
            return;

        view.rv.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments().containsKey("server")) {
            view.rv.setAdapter(new ServerMembersAdapter(
                    (ServerModel) getArguments().getSerializable("server"),
                    view.search, this));
        } else {
            view.rv.setAdapter(new ListAdapter(
                    getArguments().getInt("type"),
                    getArguments().getString("id"),
                    view.search, this));
        }
        Utils.addSpace(view.rv, 24, 80);
    }
}
