package com.aghajari.adapters;

import android.content.Context;
import android.view.View;

import com.aghajari.fragments.BaseFragment;
import com.aghajari.models.MyInfo;
import com.aghajari.permissions_item;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.PermissionUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class MemberPermissionsAdapter extends BaseAdapter<BaseAdapter.VH> {

    ArrayList<Item> list = new ArrayList<>();
    private final ServerModel serverModel, server;
    private final UserModel userModel;

    public MemberPermissionsAdapter(ServerModel serverModel, UserModel userModel, BaseFragment<?> base) {
        super(base);
        this.serverModel = serverModel;
        this.userModel = userModel;
        this.server = new ServerModel(serverModel.serverId);
        this.server.owner = "";
        this.server.permissions = serverModel.permissions;

        setLoading(true);
        EasyApi.getMemberPermission(server.serverId, userModel.getId(), model -> {
            server.permissions2 = model.get();
            list.add(new Item("Send Messages", PermissionUtils.SEND_MESSAGE, PermissionUtils.NOT_SEND_MESSAGE));
            list.add(new Item("See Invite Code", PermissionUtils.SEE_INVITE_CODE, PermissionUtils.NOT_SEE_INVITE_CODE));
            list.add(new Item("Change Server Profile", PermissionUtils.CHANGE_SERVER_PROFILE, PermissionUtils.NOT_CHANGE_SERVER_PROFILE));
            list.add(new Item("Change Server Name", PermissionUtils.CHANGE_SERVER_NAME, PermissionUtils.NOT_CHANGE_SERVER_NAME));
            list.add(new Item("Send Message To Channels", PermissionUtils.SEND_MESSAGE_TO_CHANNELS));
            list.add(new Item("Revoke Invite Code", PermissionUtils.REVOKE_INVITE_CODE));
            list.add(new Item("Change Permissions", PermissionUtils.CHANGE_PERMISSIONS));
            list.add(new Item("Remove Members", PermissionUtils.REMOVE_MEMBERS));
            removeInaccessiblePermissions();
            setLoading(false);
        });
    }

    private void removeInaccessiblePermissions() {
        Iterator<Item> iterator = list.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();

            if (item.notKey == 0) {
                if (!serverModel.owner.equals(MyInfo.getInstance().getId())
                        && !PermissionUtils.hasPermission(serverModel.permissions2, item.key))
                    iterator.remove();
            } else {
                if (!PermissionUtils.hasPermission(serverModel.permissions, item.key)
                        || (!serverModel.owner.equals(MyInfo.getInstance().getId())
                        && PermissionUtils.hasPermission(serverModel.permissions2, item.notKey)))
                    iterator.remove();
            }
        }
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public VH create(Context context, int viewType) {
        return new VH(new permissions_item(context));
    }

    @Override
    public void bind(VH viewHolder, int position) {
        Item p = list.get(position);
        permissions_item item = viewHolder.get();
        item.title.setText(p.name);

        item.switchView.setChecked(PermissionUtils.hasPermission(server, p.key, p.notKey));
        item.click.setOnClickListener(v -> {
            item.switchView.setChecked(!item.switchView.isChecked());
            if (item.switchView.isChecked()) {
                server.permissions2 |= p.key;
                if (p.notKey != 0)
                    server.permissions2 ^= p.notKey;
            } else {
                server.permissions2 ^= p.key;
                if (p.notKey != 0)
                    server.permissions2 |= p.notKey;
            }
            EasyApi.updateMemberPermission(server.serverId, userModel.getId(), server.permissions2);
        });
        item.divider.setVisibility(size() - 1 == position ? View.GONE : View.VISIBLE);
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


    private static final class Item {
        public final String name;
        public final int key;
        public final int notKey;

        public Item(String name, int key) {
            this(name, key, 0);
        }

        public Item(String name, int key, int notKey) {
            this.name = name;
            this.key = key;
            this.notKey = notKey;
        }
    }

}
