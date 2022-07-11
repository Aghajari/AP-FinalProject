package com.aghajari.adapters;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.aghajari.R;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.fragments.ProfileFragment;
import com.aghajari.models.MyInfo;
import com.aghajari.profile_item;
import com.aghajari.profile_item_title;
import com.aghajari.profile_owner_item;
import com.aghajari.shared.Profile;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticListeners;
import com.aghajari.views.PermissionUtils;
import com.aghajari.views.Utils;

import java.util.ArrayList;

public class ProfileItemAdapter extends BaseAdapter<BaseAdapter.VH> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_TITLE = 1;
    private static final int TYPE_DIVIDER = 2;
    private static final int TYPE_OWNER = 3;

    private final ItemClick itemClick;
    ArrayList<Item> list = new ArrayList<>();

    public ProfileItemAdapter(Profile profile, ItemClick itemClick, BaseFragment<?> base) {
        super(base);
        this.itemClick = itemClick;

        if (profile == MyInfo.getInstance()) {
            UserModel user = MyInfo.getInstance();

            list.add(new ItemTitle("PROFILE"));
            list.add(new ItemInfo('\ue0e6', MyInfo.getUsername(user, "Not set"),
                    true, "Edit", "username"));
            list.add(new ItemInfo('\uea67', user.nickname));
            list.add(new ItemInfo('\ue158', user.email, false, "", "email"));
            list.add(new ItemInfo('\ue897', "Change Password", "change_pass"));
            list.add(new ItemDivider());
            list.add(new ItemTitle("RELATION"));
            list.add(new ItemInfo('\ue7f2', "Online Friends", "online"));
            list.add(new ItemInfo('\ue7ef', "Friends", "friends"));
            list.add(new ItemInfo('\ue7fd', "Pending Friends", "pending"));
            list.add(new ItemInfo('\ue14b', "Blocked", "blocked"));
            list.add(new ItemDivider());
            list.add(new ItemTitle("OTHERS"));
            list.add(new ItemInfo('\ue866', "Saved Messages", "saved_messages"));
            list.add(new ItemInfo('\ue875', "Servers", "server"));
            list.add(new ItemInfo('\ue1b1', "Devices", "devices"));
            list.add(new ItemInfo('\ue859', "Bot", "bot"));
            list.add(new ItemInfo('\ue9ba', "Log Out", "logout"));
        } else if (profile instanceof UserModel) {
            UserModel user = (UserModel) profile;

            list.add(new ItemTitle("PROFILE"));
            list.add(new ItemInfo('\ue0e6', MyInfo.getUsername(user, "Not set")));
            list.add(new ItemInfo('\uea67', user.nickname));
            list.add(new ItemInfo('\ue158', user.email, false, "", "email"));
            list.add(new ItemDivider());
            list.add(new ItemTitle("OTHERS"));
            ItemInfo openChat = new ItemInfo('\ue89e', "Loading");
            list.add(openChat);
            ItemInfo block = new ItemInfo('\ue14b', "Loading");
            list.add(block);
            list.add(new ItemInfo('\ue7ef', "Mutual Friends", "mutual_friends"));

            Utils.ui(() -> {
                EasyApi.isChatOpen(user, model -> {
                    if (model.get()) {
                        openChat.text = "Close Chat";
                        openChat.tag = "close_chat";
                    } else {
                        openChat.text = "Open Chat";
                        openChat.tag = "open_chat";
                    }
                    notifyItemChanged(6);
                });

                EasyApi.hasBlocked(user.getId(), model -> {
                    if (model.get()) {
                        block.text = "Unblock";
                        block.tag = "unblock";
                    } else {
                        block.text = "Block";
                        block.tag = "block";
                    }
                    notifyItemChanged(7);
                });
            });

            StaticListeners.updateProfileBlockUnblock = userModel ->
                    EasyApi.hasBlocked(user.getId(), model -> {
                        if (model.get()) {
                            block.text = "Unblock";
                            block.tag = "unblock";
                        } else {
                            block.text = "Block";
                            block.tag = "block";
                        }
                        notifyItemChanged(7);
                    });
        } else if (profile instanceof ServerModel) {
            ServerModel serverModel = (ServerModel) profile;

            list.add(new ItemTitle("SERVER"));

            if (PermissionUtils.canSeeInviteCode(serverModel)) {
                if (PermissionUtils.canRevokeInviteCode(serverModel)) {
                    list.add(new ItemInfo('\ue0e6', "Invite Code",
                            true, "Revoke", "invite_code"));
                } else {
                    list.add(new ItemInfo('\ue0e6', "Invite Code",
                            true, "", "invite_code"));
                }
            }

            if (PermissionUtils.canChangeServerName(serverModel)) {
                list.add(new ItemInfo('\uea67', serverModel.getName(),
                        true, "Edit", "server_name"));
            } else {
                list.add(new ItemInfo('\uea67', serverModel.getName()));
            }

            ItemOwner owner = new ItemOwner();
            list.add(owner);

            if (PermissionUtils.canChangePermissions(serverModel))
                list.add(new ItemInfo('\ue73c', "Permissions", "permissions"));

            list.add(new ItemInfo('\ue7fd', "Members", "server_members"));

            if (PermissionUtils.isOwner(serverModel)) {
                list.add(new ItemInfo('\uf10a', "Design Server", "design"));
                list.add(new ItemInfo('\ue92b', "Delete Server", "delete_server"));
            } else {
                list.add(new ItemInfo('\ue9ba', "Leave Server", "leave_server"));
            }

            if (serverModel.channels == null)
                serverModel.channels = new ArrayList<>();

            if (serverModel.channels.size() > 0) {
                list.add(new ItemDivider());
                list.add(new ItemTitle("CHANNELS"));
            }

            for (ServerModel.ServerChannel channel : serverModel.channels) {
                ItemInfo info = new ItemInfo(channel.type == 0 ? '\uef49' : '\ue7ef',
                        channel.name, "channel");
                info.data = channel;
                list.add(info);
            }

            if (base.getArguments() != null && base.getArguments().containsKey("server_owner")) {
                owner.userModel = (UserModel) base.getArguments().getSerializable("server_owner");
            } else {
                EasyApi.getUserInfo(serverModel.owner, model -> {
                    owner.userModel = model.get();
                    if (base.getArguments() != null)
                        base.getArguments().putSerializable("server_owner", owner.userModel);
                    notifyItemChanged(list.indexOf(owner));
                });
            }
        }
    }

    public interface ItemClick {
        void click(ItemInfo info, int position, ProfileItemAdapter adapter);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public VH create(Context context, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new VH(new profile_item(context));
            case TYPE_TITLE:
                return new VH(new profile_item_title(context));
            case TYPE_OWNER:
                return new VH(new profile_owner_item(context));
        }

        View view = new View(context);
        view.setBackgroundColor(context.getResources().getColor(R.color.background_light));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, Utils.dp(1));
        lp.leftMargin = lp.rightMargin = Utils.dp(24);
        lp.topMargin = Utils.dp(12);
        view.setLayoutParams(lp);
        return new VH(view);
    }

    @Override
    public void bind(VH viewHolder, int position) {
        switch (viewType(position)) {
            case TYPE_ITEM:
                profile_item item = viewHolder.get();
                ItemInfo info = (ItemInfo) list.get(position);
                item.setTag(info.tag);
                item.setOnClickListener(v -> itemClick.click(info, position, this));
                item.icon.setText(String.valueOf(info.icon));
                item.text.setText(info.text);
                item.next.setVisibility(info.isNextVisible ? View.VISIBLE : View.GONE);
                item.next_tv.setText(info.next);
                break;
            case TYPE_TITLE:
                profile_item_title title = viewHolder.get();
                title.text.setText(((ItemTitle) list.get(position)).text);
                break;
            case TYPE_OWNER:
                ItemOwner owner = (ItemOwner) list.get(position);
                profile_owner_item owner_item = viewHolder.get();
                owner_item.setOnClickListener(v ->
                        base.showFragment(ProfileFragment.newInstance(owner.userModel)));
                if (owner.userModel == null) {
                    owner_item.text.setText("Loading Owner's Profile");
                } else {
                    Utils.loadAvatar(owner.userModel, owner_item.icon);
                    owner_item.text.setText(owner.userModel.getName());
                }
                break;
        }
    }

    @Override
    public VH createEmpty(Context context) {
        return null;
    }

    @Override
    public VH createViewHolder(View view) {
        return new VH(view);
    }

    @Override
    public boolean supportsEmptyView() {
        return false;
    }

    @Override
    public int viewType(int position) {
        return list.get(position).viewType;
    }

    public static abstract class Item {
        public final int viewType;

        private Item(int viewType) {
            this.viewType = viewType;
        }
    }

    public static final class ItemTitle extends Item {
        public final String text;

        private ItemTitle(String text) {
            super(TYPE_TITLE);
            this.text = text;
        }
    }

    public static final class ItemDivider extends Item {
        private ItemDivider() {
            super(TYPE_DIVIDER);
        }
    }

    public static final class ItemOwner extends Item {
        public UserModel userModel;

        private ItemOwner() {
            super(TYPE_OWNER);
        }
    }

    public static final class ItemInfo extends Item {
        public char icon;
        public String text;
        public boolean isNextVisible;
        public String next;
        public String tag;
        public Object data;

        public ItemInfo(char icon, String text) {
            this(icon, text, false, "", null);
        }

        public ItemInfo(char icon, String text, String tag) {
            this(icon, text, true, "", tag);
        }

        public ItemInfo(char icon, String text, boolean isNextVisible, String next, String tag) {
            super(TYPE_ITEM);
            this.icon = icon;
            this.text = text;
            this.isNextVisible = isNextVisible;
            this.next = next;
            this.tag = tag;
        }
    }
}
