package com.aghajari.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.R;
import com.aghajari.chat_item;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.fragments.ProfileFragment;
import com.aghajari.shared.ChatsList;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.EmptyView;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class MyChatsAdapter extends BaseAdapter<BaseAdapter.VH> {

    RecyclerView rv;
    ChatsList list = new ChatsList();


    public MyChatsAdapter(BaseFragment<?> base) {
        super(base);

        setLoading(true);
        reload();
    }

    public void reload(){
        EasyApi.getChats(model -> {
            list = model.get();

            if (list == null)
                list = new ChatsList();
            if (list.openedChats == null)
                list.openedChats = new ArrayList<>();
            if (list.servers == null)
                list.servers = new ArrayList<>();

            Collections.reverse(list.openedChats);
            Collections.reverse(list.servers);

            if (isLoading()) {
                setLoading(false);
                animate();
            } else
                notifyDataSetChanged();
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = recyclerView;
        animate();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        rv = null;
    }

    public void animate(){
        if (rv == null)
            return;
        if (!list.openedChats.isEmpty() || !list.servers.isEmpty())
            Utils.animate(rv);
    }

    @Override
    public int size() {
        return (list.servers.isEmpty() ? 0 : list.servers.size() + 1) +
                (list.openedChats.isEmpty() ? 0 : list.openedChats.size() + 1);
    }

    @Override
    public VH create(Context context, int viewType) {
        if (viewType == 1) {
            TextView tv = new TextView(context);
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.semi_bold));
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(17);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-2, -2);
            lp.leftMargin = lp.rightMargin = Utils.dp(26);
            lp.topMargin = lp.bottomMargin = Utils.dp(8);
            tv.setLayoutParams(lp);
            return new VH(tv);
        }
        return new VH(new chat_item(context));
    }

    @Override
    public void bind(VH viewHolder, int position) {
        if (viewType(position) == 1) {
            TextView tv = viewHolder.get();
            if (position == 0 && !list.openedChats.isEmpty())
                tv.setText("Chats");
            else
                tv.setText("Servers");
        } else {
            chat_item view = viewHolder.get();
            boolean isServer = position > list.openedChats.size();

            if (isServer) {
                int realPosition = position - (list.openedChats.isEmpty() ? 1 : list.openedChats.size() + 2);
                view.divider.setVisibility(realPosition < list.servers.size() - 1 ? View.VISIBLE : View.GONE);
                ServerModel serverModel = list.servers.get(realPosition);
                Utils.loadAvatar(serverModel, view.profile);
                view.username.setText(serverModel.getName());
                view.nickname.setVisibility(View.GONE);

                view.setOnClickListener(v -> base.showFragment(ProfileFragment.newInstance(serverModel)));
            } else {
                view.divider.setVisibility(position < list.openedChats.size() ? View.VISIBLE : View.GONE);
                UserModel user = list.openedChats.get(position - 1).getUser();
                Utils.loadAvatar(user, view.profile);
                view.username.setText(user.getName());
                view.nickname.setText(user.nickname);
                view.nickname.setVisibility(View.VISIBLE);

                view.setOnClickListener(v -> base.showFragment(ProfileFragment.newInstance(user)));
            }

        }

    }

    @Override
    public int viewType(int position) {
        return position == 0 || (!list.openedChats.isEmpty() &&
                position == list.openedChats.size() + 1) ? 1 : 0;
    }

    @Override
    public VH createEmpty(Context context) {
        return new VH(new EmptyView(context, EmptyView.Type.NO_CHATS));
    }

    @Override
    public VH createViewHolder(View view) {
        return new VH(view);
    }
}
