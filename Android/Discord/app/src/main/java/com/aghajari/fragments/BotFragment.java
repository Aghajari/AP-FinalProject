package com.aghajari.fragments;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aghajari.bot_fragment;
import com.aghajari.store.EasyApi;
import com.aghajari.views.Utils;

public class BotFragment extends BaseFragment<bot_fragment> {

    @Override
    public bot_fragment create(Context context) {
        return new bot_fragment(context);
    }

    String link = "";

    @Override
    public void bind() {
        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        EasyApi.getBotLink(model -> {
            String l = model.get();
            if (l != null) {
                link = l.trim();
                view.api.setText(link);
            }
        });

        view.back.setOnClickListener(v -> back());

        view.save.setOnClickListener(v -> {
            String l = Utils.textOf(view.api);
            if (l.equals(link)) {
                back();
                return;
            }
            if (l.isEmpty()) {
                EasyApi.updateBotLink("", model ->
                        Toast.makeText(v.getContext(), "Disconnected", Toast.LENGTH_SHORT).show());
                back();
                return;
            }
            if (!l.toLowerCase().startsWith("https://")) {
                Toast.makeText(v.getContext(), "Api link must start with https://", Toast.LENGTH_SHORT).show();
                return;
            }
            if (l.length() < 14) {
                Toast.makeText(v.getContext(), "Invalid Api!", Toast.LENGTH_SHORT).show();
                return;
            }
            view.progress.setLoading(true);
            EasyApi.updateBotLink(l, model -> {
                Toast.makeText(v.getContext(), "Connected :)", Toast.LENGTH_SHORT).show();
                if (getContext() != null) {
                    view.progress.setLoading(false);
                    back();
                }
            });
        });
    }

}
