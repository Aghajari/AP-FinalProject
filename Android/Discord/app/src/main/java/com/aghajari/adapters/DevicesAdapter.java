package com.aghajari.adapters;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.R;
import com.aghajari.chat_item;
import com.aghajari.dialogs.TerminateDeviceDialog;
import com.aghajari.fragments.BaseFragment;
import com.aghajari.fragments.DevicesFragment;
import com.aghajari.fragments.QRScannerFragment;
import com.aghajari.link_desktop_device;
import com.aghajari.shared.models.DeviceInfo;
import com.aghajari.store.EasyApi;
import com.aghajari.views.EmptyView;
import com.aghajari.views.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends BaseAdapter<BaseAdapter.VH> {

    ArrayList<DeviceInfo> list = new ArrayList<>();

    public DevicesAdapter(RecyclerView rv, DevicesFragment base) {
        super(base);

        setLoading(true);
        EasyApi.getDevices(model -> {
            List<DeviceInfo> l = model.get();
            if (l != null && !l.isEmpty() && l.get(0) != null)
                list.addAll(l);

            setLoading(false);
            Utils.animate(rv);
        });
    }

    public void remove(int index) {
        list.remove(index - 1);
        notifyItemRemoved(index);
    }

    @Override
    public int size() {
        return list.isEmpty() ? 0 : list.size() + 1;
    }

    @Override
    public VH create(Context context, int viewType) {
        if (viewType == 100)
            return new VH(new link_desktop_device(context));

        return new VH(new chat_item(context));
    }

    @Override
    public void bind(VH viewHolder, int position) {
        if (position == 0) {
            link_desktop_device v = viewHolder.get();
            v.click.setOnClickListener(v0 -> {
                ((DevicesFragment) base).requestCameraPermission(res -> {
                    if (res)
                        base.showFragment(new QRScannerFragment());
                });
            });
            return;
        }

        chat_item view = viewHolder.get();
        final DeviceInfo info = list.get(position - 1);

        view.divider.setVisibility(position < list.size() ? View.VISIBLE : View.GONE);

        view.username.setText(info.name);
        view.nickname.setText(info.os);

        int image = R.drawable.os_win;
        if (info.type == DeviceInfo.MAC)
            image = R.drawable.os_mac;
        else if (info.type == DeviceInfo.ANDROID)
            image = R.drawable.os_android;

        Glide.with(view)
                .load(image)
                .circleCrop()
                .into(view.profile);

        view.setOnClickListener(v -> {
            if (viewHolder.getAdapterPosition() != 1)
                new TerminateDeviceDialog(v.getContext(),
                        viewHolder.getAdapterPosition(), this, info).show();
        });
    }

    @Override
    public VH createEmpty(Context context) {
        return new VH(new EmptyView(context, EmptyView.Type.NO_RESULTS));
    }

    @Override
    public VH createViewHolder(View view) {
        return new VH(view);
    }

    @Override
    public int viewType(int position) {
        if (position == 0)
            return 100;

        return super.viewType(position);
    }
}
