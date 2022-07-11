package com.aghajari.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.chat_item;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.fragments.PermissionsFragment;
import com.aghajari.fragments.ProfileFragment;
import com.aghajari.models.MyInfo;
import com.aghajari.pending_friend_item;
import com.aghajari.server_member_item;
import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.FriendshipRequestModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.EmptyView;
import com.aghajari.views.PermissionUtils;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerMembersAdapter extends BaseAdapter<BaseAdapter.VH> {

    private final ServerModel serverModel;
    private boolean isSearching;

    ArrayList<IDFinder> list = new ArrayList<>();
    ArrayList<IDFinder> res = list;

    private final EditText edt;
    private RecyclerView rv;

    public ServerMembersAdapter(ServerModel serverModel, EditText search, BaseFragment<?> base) {
        super(base);
        this.serverModel = serverModel;
        this.edt = search;

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
                notifyDataSetChanged();
            }
        });

        search.setHint("Search for members");
        load();
    }

    public void load() {
        list.clear();
        setLoading(true);
        EasyApi.getMembersOfServer(serverModel.serverId, model -> {
            load(model.get());
        });
    }

    public void load(List<? extends IDFinder> out) {
        list.clear();
        list.addAll(out);
        search();
        setLoading(false);
        animate();
    }

    public void search() {
        String s = Utils.textOf(edt).toLowerCase();
        if (s.isEmpty()) {
            res = list;
            isSearching = false;
        } else {
            isSearching = true;
            try {
                res = new ArrayList<>();
                for (IDFinder id : list) {
                    if (id.getUser().getName().toLowerCase().contains(s)
                            || id.getUser().nickname.toLowerCase().contains(s)) {
                        res.add(id);
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        rv = null;
    }

    public void animate() {
        if (rv != null && !res.isEmpty())
            Utils.animate(rv);
    }

    @Override
    public int size() {
        return res.size();
    }

    @Override
    public VH create(Context context, int viewType) {
        return new VH(new server_member_item(context));
    }

    @Override
    public void bind(VH viewHolder, int position) {
        IDFinder user = res.get(position);
        chat_item chat;

        server_member_item serverMemberItem = viewHolder.get();
        chat = serverMemberItem.chat;

        chat.divider.setVisibility(position < list.size() - 1 ? View.VISIBLE : View.GONE);

        Utils.loadAvatar(user.getUser(), chat.profile);

        chat.username.setText(user.getUser().getName());
        chat.nickname.setText(user.getUser().nickname);

        chat.setOnClickListener(v -> {
            if (user.getUser().getId().equals(MyInfo.getInstance().getId()))
                return;

            base.showFragment(ProfileFragment.newInstance(user.getUser()));
        });

        if (!user.getUser().getId().equals(MyInfo.getInstance().getId()) &&
                !serverModel.owner.equals(user.getUser().getId())) {

            if (PermissionUtils.canChangePermissions(serverModel)) {
                ((View) serverMemberItem.permission.getParent()).setVisibility(View.VISIBLE);
                serverMemberItem.permission.setOnClickListener(v -> {
                    base.showFragment(PermissionsFragment.newInstance(serverModel, user.getUser()));
                });
            } else {
                ((View) serverMemberItem.permission.getParent()).setVisibility(View.GONE);
            }

            if (PermissionUtils.canRemoveMember(serverModel)) {
                ((View) serverMemberItem.remove.getParent()).setVisibility(View.VISIBLE);
                serverMemberItem.remove.setOnClickListener(v -> {
                    EasyApi.removeFromServer(serverModel.serverId, user.getUser().getId());
                    load();
                });
            } else {
                ((View) serverMemberItem.remove.getParent()).setVisibility(View.GONE);
            }
        } else {
            ((View) serverMemberItem.remove.getParent()).setVisibility(View.GONE);
            ((View) serverMemberItem.permission.getParent()).setVisibility(View.GONE);
        }
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
