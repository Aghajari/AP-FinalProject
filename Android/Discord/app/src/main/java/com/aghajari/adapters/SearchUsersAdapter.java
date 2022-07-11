package com.aghajari.adapters;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.chat_item;
import com.aghajari.api.ApiService;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.fragments.ProfileFragment;
import com.aghajari.models.MyInfo;
import com.aghajari.models.SearchUsers;
import com.aghajari.shared.models.UserModel;
import com.aghajari.views.EmptyView;
import com.aghajari.views.Utils;

import java.util.ArrayList;

public class SearchUsersAdapter extends BaseAdapter<BaseAdapter.VH> {

    ArrayList<UserModel> list = new ArrayList<>();

    public SearchUsersAdapter(String searchFor, RecyclerView rv, BaseFragment<?> base) {
        super(base);

        setLoading(true);
        ApiService.search(searchFor, new ApiService.Callback() {
            @Override
            public void onResponse(String body) {
                SearchUsers users = SearchUsers.parse(body);

                if (users.users != null && !users.users.isEmpty()) {
                    for (UserModel model : users.users) {
                        if (model.getId().equals(MyInfo.getInstance().getId()))
                            continue;
                        list.add(model);
                    }
                }

                setLoading(false);
                if (!list.isEmpty())
                    Utils.animate(rv);
            }

            @Override
            public void onError(boolean network, int code) {
                setLoading(false);
            }
        });
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public VH create(Context context, int viewType) {
        return new VH(new chat_item(context));
    }

    @Override
    public void bind(VH viewHolder, int position) {
        chat_item view = viewHolder.get();
        UserModel user = list.get(position);

        view.divider.setVisibility(position < list.size() - 1 ? View.VISIBLE : View.GONE);
        Utils.loadAvatar(user, view.profile);
        view.username.setText(user.getName());
        view.nickname.setText(user.nickname);

        view.setOnClickListener(v -> base.showFragment(ProfileFragment.newInstance(user)));
    }

    @Override
    public VH createEmpty(Context context) {
        return new VH(new EmptyView(context, EmptyView.Type.NO_RESULTS));
    }

    @Override
    public VH createViewHolder(View view) {
        return new VH(view);
    }
}
