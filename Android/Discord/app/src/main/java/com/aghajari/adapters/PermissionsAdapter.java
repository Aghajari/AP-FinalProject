package com.aghajari.adapters;

import android.content.Context;
import android.view.View;

import com.aghajari.fragments.BaseFragment;
import com.aghajari.permissions_item;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.PermissionUtils;


import java.util.ArrayList;

public class PermissionsAdapter extends BaseAdapter<BaseAdapter.VH> {

    ArrayList<Item> list = new ArrayList<>();
    private final ServerModel serverModel;

    public PermissionsAdapter(ServerModel serverModel, BaseFragment<?> base) {
        super(base);
        this.serverModel = serverModel;

        list.add(new Item("Send Messages", PermissionUtils.SEND_MESSAGE));
        list.add(new Item("See Invite Code", PermissionUtils.SEE_INVITE_CODE));
        list.add(new Item("Change Server Profile", PermissionUtils.CHANGE_SERVER_PROFILE));
        list.add(new Item("Change Server Name", PermissionUtils.CHANGE_SERVER_NAME));
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

        item.switchView.setChecked(PermissionUtils.hasPermission(serverModel.permissions, p.key));
        item.click.setOnClickListener(v -> {
            item.switchView.setChecked(!item.switchView.isChecked());
            if (item.switchView.isChecked()) {
                serverModel.permissions |= p.key;
            } else {
                serverModel.permissions ^= p.key;
            }
            EasyApi.updateServerPermissions(serverModel);
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
        public int key;

        public Item(String name, int key) {
            this.name = name;
            this.key = key;
        }
    }

}
