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
import com.aghajari.fragments.ProfileFragment;
import com.aghajari.models.MyInfo;
import com.aghajari.my_friend_item;
import com.aghajari.pending_friend_item;
import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.FriendshipRequestModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticListeners;
import com.aghajari.views.EmptyView;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter<BaseAdapter.VH> {

    public static final int TYPE_ONLINE_FRIENDS = 1;
    public static final int TYPE_MY_FRIENDS = 2;
    public static final int TYPE_PENDING_FRIENDS = 3;
    public static final int TYPE_BlOCK = 4;
    public static final int TYPE_MUTUAL_FRIENDS = 5;
    private final int type;
    private final String id;
    private boolean isSearching;

    ArrayList<IDFinder> list = new ArrayList<>();
    ArrayList<IDFinder> res = list;

    private final EditText edt;
    private RecyclerView rv;

    public ListAdapter(int type, String id, EditText search, BaseFragment<?> base) {
        super(base);
        this.id = id;
        this.type = type;
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

        switch (type) {
            case TYPE_ONLINE_FRIENDS:
                search.setHint("Search for online friends");
                break;
            case TYPE_MY_FRIENDS:
                search.setHint("Search for friends");
                break;
            case TYPE_MUTUAL_FRIENDS:
                search.setHint("Search for mutual friends");
                break;
            case TYPE_PENDING_FRIENDS:
                search.setHint("Search for requests");
                break;
            case TYPE_BlOCK:
                search.setHint("Search");
                break;
        }

        load();
    }

    public void load() {
        list.clear();
        setLoading(true);
        switch (type) {
            case TYPE_ONLINE_FRIENDS:
                EasyApi.getOnlineFriends(model -> load(model.get()));
                break;
            case TYPE_MY_FRIENDS:
                EasyApi.getListOfMyFriends(model -> load(model.get()));
                break;
            case TYPE_PENDING_FRIENDS:
                EasyApi.getListOfPendingFriends(model -> load(model.get()));
                break;
            case TYPE_BlOCK:
                EasyApi.getBlockList(model -> load(model.get()));
                break;
            case TYPE_MUTUAL_FRIENDS:
                EasyApi.getMutualFriends(model -> load(model.get()), id);
                break;
        }
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

        if (type == TYPE_MY_FRIENDS || type == TYPE_PENDING_FRIENDS)
            StaticListeners.friendshipUpdater = friendshipModel -> load();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        rv = null;
        StaticListeners.friendshipUpdater = null;
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
        switch (type) {
            case TYPE_MY_FRIENDS:
                return new VH(new my_friend_item(context));
            case TYPE_PENDING_FRIENDS:
                return new VH(new pending_friend_item(context));
            default:
                return new VH(new chat_item(context));
        }
    }

    @Override
    public void bind(VH viewHolder, int position) {
        IDFinder user = res.get(position);
        chat_item chat;

        switch (type) {
            case TYPE_MY_FRIENDS:
                my_friend_item friendChat = viewHolder.get();
                chat = friendChat.chat;
                friendChat.delete.setOnClickListener(v -> {
                    EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP,
                            user.getUser(), ((FriendshipModel) user).index);
                    load();
                });
                break;
            case TYPE_PENDING_FRIENDS:
                pending_friend_item pendingChat = viewHolder.get();
                chat = pendingChat.chat;
                FriendshipModel fm = (FriendshipModel) user;

                if (fm.fromId.equals(MyInfo.getInstance().getId())) {
                    ((View) pendingChat.accept.getParent()).setVisibility(View.GONE);
                    ((View) pendingChat.reject.getParent()).setVisibility(View.GONE);
                    ((View) pendingChat.delete.getParent()).setVisibility(View.VISIBLE);

                    pendingChat.delete.setOnClickListener(v -> {
                        EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP,
                                user.getUser(), fm.index);
                        load();
                    });
                } else {
                    ((View) pendingChat.accept.getParent()).setVisibility(View.VISIBLE);
                    ((View) pendingChat.reject.getParent()).setVisibility(View.VISIBLE);
                    ((View) pendingChat.delete.getParent()).setVisibility(View.GONE);

                    pendingChat.accept.setOnClickListener(v -> {
                        EasyApi.friendshipRequest(FriendshipRequestModel.ACCEPT_REQUEST,
                                user.getUser(), fm.index);
                        load();
                    });
                    pendingChat.reject.setOnClickListener(v -> {
                        EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP,
                                user.getUser(), fm.index);
                        load();
                    });
                }
                break;
            default:
                chat = viewHolder.get();
        }

        chat.divider.setVisibility(position < list.size() - 1 ? View.VISIBLE : View.GONE);

        Utils.loadAvatar(user.getUser(), chat.profile);

        chat.username.setText(user.getUser().getName());
        chat.nickname.setText(user.getUser().nickname);

        chat.setOnClickListener(v -> base.showFragment(ProfileFragment.newInstance(user.getUser())));
    }

    @Override
    public VH createEmpty(Context context) {
        EmptyView.Type et;
        if (isSearching)
            et = EmptyView.Type.NO_RESULTS;
        else
            switch (type) {
                case TYPE_ONLINE_FRIENDS:
                    et = EmptyView.Type.NO_ONLINE;
                    break;
                case TYPE_MY_FRIENDS:
                    et = EmptyView.Type.NO_FRIEND;
                    break;
                case TYPE_PENDING_FRIENDS:
                    et = EmptyView.Type.NO_PENDING;
                    break;
                case TYPE_BlOCK:
                    et = EmptyView.Type.NO_BlOCK;
                    break;
                case TYPE_MUTUAL_FRIENDS:
                    et = EmptyView.Type.NO_MUTUAL_FRIENDS;
                    break;
                default:
                    et = EmptyView.Type.NO_RESULTS;
                    break;
            }

        return new VH(new EmptyView(context, et));
    }

    @Override
    public VH createViewHolder(View view) {
        return new VH(view);
    }
}
