package com.aghajari.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aghajari.HomeActivity;
import com.aghajari.api.SocketApi;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

public abstract class BaseFragment<T extends View> extends Fragment {

    T view;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return create(inflater.getContext());
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SocketApi.tryToConnectIfNeeded();

        //noinspection unchecked
        this.view = (T) view;
        bind();
    }

    public abstract T create(Context context);

    public abstract void bind();

    public void showFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        ((HomeActivity) getActivity()).showFragment(fragment);
    }

    public boolean onBack() {
        return false;
    }

    public void back() {
        if (getActivity() == null)
            return;

        getActivity().onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(view);
    }
}
