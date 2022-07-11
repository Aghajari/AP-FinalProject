package com.aghajari.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.home_fragment;
import com.aghajari.adapters.BaseAdapter;
import com.aghajari.adapters.MyChatsAdapter;
import com.aghajari.adapters.SearchUsersAdapter;
import com.aghajari.axanimation.AXAnimation;
import com.aghajari.models.MyInfo;
import com.aghajari.store.StaticListeners;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends BaseFragment<home_fragment> {

    private MyChatsAdapter myChatsAdapter;

    @Override
    public home_fragment create(Context context) {
        return new home_fragment(context);
    }

    @Override
    public void bind() {
        Utils.loadAvatar(MyInfo.getInstance(), view.profile);
        view.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        view.rv.setAdapter(myChatsAdapter = new MyChatsAdapter(this));
        view.rv.setTag("");
        Utils.addSpace(view.rv, 24, 80);

        view.search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.search.clearFocus();

                String text = Utils.textOf(view.search);
                if (text.equals(view.rv.getTag()))
                    return false;

                if (text.length() == 0) {
                    view.rv.setTag("");
                    view.rv.setAdapter(myChatsAdapter);
                } else {
                    view.rv.setTag(text);
                    view.rv.setAdapter(new SearchUsersAdapter(text, view.rv, HomeFragment.this));
                }
            }
            return false;
        });

        view.profile.setOnClickListener(v -> AXAnimation.create()
                .duration(100)
                .scale(0.6f)
                .nextSection()
                .scale(1f)
                .withSectionEndAction(animation ->
                        showFragment(ProfileFragment.newInstance(MyInfo.getInstance())))
                .start(view.profile));

        StaticListeners.updateOpenedChatsListener = () -> myChatsAdapter.reload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StaticListeners.updateOpenedChatsListener = null;
    }

    @Override
    public boolean onBack() {
        if (!"".equals(view.rv.getTag())) {
            view.rv.setTag("");
            view.rv.setAdapter(myChatsAdapter);
            view.search.setText("");
            view.search.clearFocus();
            return true;
        }
        return super.onBack();
    }
}
