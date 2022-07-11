package com.aghajari.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.Application;
import com.aghajari.adapters.ListAdapter;
import com.aghajari.adapters.ProfileItemAdapter;
import com.aghajari.api.ApiService;
import com.aghajari.axanimation.listener.AXAnimatorEndListener;
import com.aghajari.dialogs.DeleteOrLeaveDialog;
import com.aghajari.dialogs.LogoutDialog;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.UploadAvatarModel;
import com.aghajari.profile_fragment;
import com.aghajari.R;
import com.aghajari.axanimation.AXAnimation;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.Profile;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.FriendshipRequestModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticListeners;
import com.aghajari.views.NotificationCenter;
import com.aghajari.views.PermissionUtils;
import com.aghajari.views.Utils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;


public class ProfileFragment extends BaseFragment<profile_fragment> {

    public static ProfileFragment newInstance(Profile profile) {
        if (profile instanceof UserModel &&
                profile != MyInfo.getInstance() &&
                ((UserModel) profile).getId().equals(MyInfo.getInstance().getId()))
            profile = MyInfo.getInstance();

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", (Serializable) profile);
        args.putInt("parallax", 0);
        args.putBoolean("visible", true);
        args.putBoolean("firstTime", true);
        args.putLong("delay", 500);
        fragment.setArguments(args);
        return fragment;
    }

    private final GradientDrawable gradientDrawable =
            new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
                    Application.context.getResources().getColor(R.color.color1),
                    Application.context.getResources().getColor(R.color.color2)
            });

    private boolean isEditingProfile = false;
    private long lastTime = 0;
    private boolean uploaded = false;
    private final long duration = 350;
    private ChatFragment chatFragment;

    private FriendshipModel friendshipModel =
            new FriendshipModel(-1, "", "", false);

    Bitmap uploadedBitmap = null;
    String format;

    private final Utils.Consumer<Integer> colorUpdater = color -> {
        if (getContext() == null || getActivity() == null || !view.isAttachedToWindow())
            return;

        GradientDrawable gd;
        if (color == null) {
            gd = gradientDrawable;

            if (view.getBackground() == gd)
                return;
        } else {
            gd = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[]{color, Utils.brighterColor(color)}
            );
        }

        boolean firstTime = getArguments() == null
                || getArguments().getBoolean("firstTime", true);
        long delay = getArguments() == null ? 500 : getArguments().getLong("delay", 500);

        if (getArguments() != null) {
            getArguments().putBoolean("firstTime", false);
            getArguments().putLong("delay", 0);
        }

        if (!firstTime)
            view.header.setBackground(gd);
        else
            AXAnimation.create()
                    .delay(delay)
                    .duration(500)
                    .background(gd)
                    .start(view.header);

        GradientDrawable gd2 = (GradientDrawable) gd.getConstantState().newDrawable().mutate();
        gd2.setCornerRadius(Utils.dp(100));
        if (firstTime && chatFragment.isChatting()) {
            AXAnimation.create()
                    .delay(delay)
                    .duration(500)
                    .background(gd2)
                    .start(view.send_parent);
        } else {
            view.send_parent.setBackground(gd2);
        }
    };

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (getContext() == null)
                    return;

                Intent data = result.getData();
                if (data == null)
                    return;
                format = Utils.getFileFormat(data.getData());

                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                    Bitmap res = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    Glide.with(view)
                            .load(res)
                            .circleCrop()
                            .into(view.profile);

                    uploadedBitmap = res;
                    if (getArguments() != null)
                        getArguments().putBoolean("firstTime", true);

                    Utils.getColor(res, colorUpdater);
                } catch (Exception ignore) {
                }
            });

    private Profile profile;

    @Override
    public profile_fragment create(Context context) {
        return new profile_fragment(context);
    }

    @Override
    public void bind() {
        if (getArguments() == null)
            return;

        profile = (Profile) getArguments().getSerializable("profile");
        chatFragment = new ChatFragment(this, view);
        StaticListeners.updateProfile = this::updateProfile;
        StaticListeners.updateServer = this::updateProfile;

        view.header.setBackground(gradientDrawable);
        Utils.loadAvatar(profile, view.profile);
        view.name.setText(profile.getName());
        Utils.getColor(profile, colorUpdater);

        view.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        view.rv.setAdapter(new ProfileItemAdapter(profile, this::item_click, this));
        Utils.addSpace(view.rv, 304, 80);

        parallax();
        view.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (getArguments() == null || isEditingProfile)
                    return;

                getArguments().putInt("parallax",
                        getArguments().getInt("parallax", 0) + dy);
                parallax();
            }

        });

        view.profile_click.setOnClickListener(v -> startEditingProfile());
        view.profile.setTag("profile");
        view.profile.setOnClickListener(v -> startEditingProfile());

        if (profile == MyInfo.getInstance()) {
            view.profile_picker.setVisibility(View.VISIBLE);
            view.chat_click.setText("\ue866");

            StaticListeners.updateMyProfile = () -> {
                if (getArguments() != null)
                    getArguments().putBoolean("firstTime", true);

                Utils.loadAvatar(profile, view.profile);
                view.name.setText(profile.getName());
                Utils.getColor(profile, colorUpdater);
            };

        } else if (profile instanceof ServerModel) {
            view.profile_hint.setText("Upload an image for server's profile");

            checkServerProfileAccess();
            view.chat_parent.setVisibility(View.GONE);

        } else if (profile instanceof UserModel) {
            NotificationCenter.cancel((UserModel) profile);
            view.profile_click.setText("\uea1d");

            view.request_friend.setVisibility(View.VISIBLE);
            updateTextWithName(view.request_friend_hint);
            updateTextWithName(view.cancel_request_friend_hint);
            updateTextWithName(view.ask_request_friend_hint);
            updateTextWithName(view.is_friend_hint);

            StaticListeners.friendshipUpdater = model -> {
                String id = ((UserModel) profile).getId();
                if (model != null &&
                        (model.fromId.equals(id) || model.toId.equals(id))) {
                    friendshipModel = model;
                    checkForCorrectFriendshipPage();
                }
            };

            reloadFriendship();

            view.send_friend_request.setOnClickListener(v -> {
                if (!isEditingProfile || isAnimating())
                    return;
                lastTime = System.currentTimeMillis() + (duration / 2);
                EasyApi.friendshipRequest(FriendshipRequestModel.SEND_REQUEST, (UserModel) profile);

                requestFriendAnimation(false, 0, a -> {
                    reloadFriendship();
                    view.request_friend.setVisibility(View.GONE);
                    view.cancel_request_friend.setVisibility(View.VISIBLE);
                    cancelRequestFriendAnimation(true, 0, null);
                });
            });

            view.cancel_friend_request.setOnClickListener(v -> {
                if (!isEditingProfile || isAnimating())
                    return;
                lastTime = System.currentTimeMillis() + (duration / 2);
                EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP,
                        (UserModel) profile, friendshipModel.index);

                cancelRequestFriendAnimation(false, 0, a -> {
                    reloadFriendship();
                    view.request_friend.setVisibility(View.VISIBLE);
                    view.cancel_request_friend.setVisibility(View.GONE);
                    requestFriendAnimation(true, 0, null);
                });
            });

            view.reject_friend_request.setOnClickListener(v -> {
                if (!isEditingProfile || isAnimating())
                    return;
                lastTime = System.currentTimeMillis() + (duration / 2);
                EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP,
                        (UserModel) profile, friendshipModel.index);

                askRequestFriendAnimation(false, 0, a -> {
                    reloadFriendship();
                    view.request_friend.setVisibility(View.VISIBLE);
                    view.ask_request_friend.setVisibility(View.GONE);
                    requestFriendAnimation(true, 0, null);
                });
            });

            view.accept_friend_request.setOnClickListener(v -> {
                if (!isEditingProfile || isAnimating())
                    return;
                lastTime = System.currentTimeMillis() + (duration / 2);
                EasyApi.friendshipRequest(FriendshipRequestModel.ACCEPT_REQUEST,
                        (UserModel) profile, friendshipModel.index);

                askRequestFriendAnimation(false, 0, a -> {
                    reloadFriendship();
                    view.is_friend.setVisibility(View.VISIBLE);
                    view.ask_request_friend.setVisibility(View.GONE);
                    isFriendAnimation(true, 0, null);
                });
            });

            view.remove_friend.setOnClickListener(v -> {
                if (!isEditingProfile || isAnimating())
                    return;
                lastTime = System.currentTimeMillis() + (duration / 2);
                EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP,
                        (UserModel) profile, friendshipModel.index);

                isFriendAnimation(false, 0, a -> {
                    reloadFriendship();
                    view.request_friend.setVisibility(View.VISIBLE);
                    view.is_friend.setVisibility(View.GONE);
                    requestFriendAnimation(true, 0, null);
                });
            });
        }

        if (!getArguments().getBoolean("visible")) {
            view.profile_parent.setScaleX(0);
            view.profile_parent.setScaleY(0);
            view.chat_parent.setScaleX(0);
            view.chat_parent.setScaleY(0);
            view.parallax_header.setElevation(0);
        }

        if (view.profile_picker.getVisibility() == View.VISIBLE) {
            view.cancel.setOnClickListener(v -> {
                if (view.progress.isLoading() ||
                        !isEditingProfile || isAnimating())
                    return;

                hideEditingProfile();
            });
            view.edit.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                launcher.launch(Intent.createChooser(intent, "Select Profile"));
            });
            view.apply.setOnClickListener(v -> {
                if (view.progress.isLoading() ||
                        !isEditingProfile || isAnimating())
                    return;

                if (uploadedBitmap == null) {
                    hideEditingProfile();
                } else {
                    File file = Utils.saveBitmap(uploadedBitmap, format);
                    if (file == null) {
                        Toast.makeText(v.getContext(), "Can't upload profile!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    view.progress.setLoading(true);

                    if (profile instanceof ServerModel) {
                        ApiService.uploadImage(file, new ApiService.Callback() {
                            @Override
                            public void onResponse(String body) {
                                System.out.println(body);
                                UploadAvatarModel model = UploadAvatarModel.parseOnlyUrl(body);

                                if (model != null && model.success) {
                                    view.progress.setLoading(false);

                                    ServerModel server = (ServerModel) profile;
                                    server.setImage(null);
                                    server.setColor(null);
                                    server.avatar = model.url;
                                    EasyApi.updateServerAvatar(server);
                                    hideEditingProfile();
                                } else {
                                    onError(false, 400);
                                }
                            }

                            @Override
                            public void onError(boolean network, int code) {
                                view.progress.setLoading(false);
                                Toast.makeText(v.getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError2(boolean network, int code, String res) {
                                view.progress.setLoading(false);
                                BaseApiModel.toastError(res);
                            }
                        });
                    } else {
                        ApiService.uploadAvatar(file, new ApiService.Callback() {
                            @Override
                            public void onResponse(String body) {
                                System.out.println(body);
                                UploadAvatarModel model = UploadAvatarModel.parse(body);

                                if (model != null && model.success) {
                                    view.progress.setLoading(false);
                                    EasyApi.updateProfile();
                                    hideEditingProfile();
                                } else {
                                    onError(false, 400);
                                }
                            }

                            @Override
                            public void onError(boolean network, int code) {
                                view.progress.setLoading(false);
                                Toast.makeText(v.getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError2(boolean network, int code, String res) {
                                view.progress.setLoading(false);
                                BaseApiModel.toastError(res);
                            }
                        });
                    }
                }
            });
        }

        view.chat_click.setOnClickListener(v -> startChat());
        view.header.setOnClickListener(v -> {
            if (chatFragment.isChatting())
                onBack();
        });

        if (profile instanceof UserModel) {
            checkOnlineStatus();
            StaticListeners.updateOnlineStatus = this::checkOnlineStatus;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatFragment.onDestroy();
        StaticListeners.friendshipUpdater = null;
        StaticListeners.updateOnlineStatus = null;
        StaticListeners.updateProfile = null;
        StaticListeners.updateServer = null;
        StaticListeners.updateMyProfile = null;
        StaticListeners.updateProfileBlockUnblock = null;
    }

    private void updateProfile(Profile profile) {
        if (this.profile == null)
            return;

        if (profile instanceof UserModel && this.profile instanceof UserModel) {
            UserModel model = (UserModel) profile;
            UserModel me = (UserModel) this.profile;
            if (!me.getId().equals(model.getId()))
                return;

            boolean updateAvatar = !me.avatar.equals(model.avatar);
            me.avatar = model.avatar;
            me.nickname = model.nickname;
            me.username = model.username;
            me.email = model.email;

            if (getContext() == null || getActivity() == null || !view.isAttachedToWindow())
                return;

            if (updateAvatar) {
                if (getArguments() != null)
                    getArguments().putBoolean("firstTime", true);
                me.setColor(null);
                me.setImage(null);
                Utils.loadAvatar(me, view.profile);
                Utils.getColor(me, colorUpdater);
            }
            view.name.setText(me.getName());
            view.rv.setAdapter(new ProfileItemAdapter(me, this::item_click, this));
        } else if (profile instanceof ServerModel && this.profile instanceof ServerModel) {
            ServerModel model = (ServerModel) profile;
            ServerModel me = (ServerModel) this.profile;
            if (!me.serverId.equals(model.serverId))
                return;

            me.permissions = model.permissions;
            me.permissions2 = model.permissions2;
            if (me.permissions2 == -1 && me.permissions == -1) {
                back();
                return;
            }

            me.owner = model.owner;
            me.channels = model.channels;
            boolean updateAvatar = !me.avatar.equals(model.avatar);
            me.avatar = model.avatar;
            me.name = model.name;
            me.inviteCode = model.inviteCode;

            if (updateAvatar) {
                if (getArguments() != null)
                    getArguments().putBoolean("firstTime", true);
                me.setColor(null);
                me.setImage(null);
                Utils.loadAvatar(me, view.profile);
                Utils.getColor(me, colorUpdater);
            }
            view.name.setText(me.getName());
            view.rv.setAdapter(new ProfileItemAdapter(me, this::item_click, this));
            checkServerProfileAccess();
        }
    }

    private void checkServerProfileAccess() {
        if (PermissionUtils.canChangeServerProfile((ServerModel) profile)) {
            view.profile_picker.setVisibility(View.VISIBLE);
            view.profile_parent.setVisibility(View.VISIBLE);
        } else {
            view.profile_picker.setVisibility(View.GONE);
            view.profile_parent.setVisibility(View.GONE);
        }
    }

    private void checkOnlineStatus() {
        EasyApi.isOnline((UserModel) profile, model -> {
            if (getContext() == null)
                return;

            if (model.get()) {
                if (isEditingProfile && !isAnimating()) {
                    view.online.setVisibility(View.VISIBLE);

                } else {
                    view.online.setScaleX(0);
                    view.online.setScaleY(0);
                    view.online.setVisibility(View.VISIBLE);

                    AXAnimation.create()
                            .delay(250)
                            .duration(100)
                            .scale(1f)
                            .start(view.online);
                }
            } else {
                if (view.online.getVisibility() == View.VISIBLE
                        && view.getScaleX() == 1) {
                    AXAnimation.create()
                            .duration(100)
                            .scale(0f)
                            .withEndAction(a -> view.online.setVisibility(View.GONE))
                            .start(view.online);
                } else {
                    view.online.setVisibility(View.GONE);
                }
            }
        });
    }

    private void reloadFriendship() {
        EasyApi.getFriendshipStatus(((UserModel) profile).getId(), model -> {
            if (model.get() != null) {
                friendshipModel = model.get();
                checkForCorrectFriendshipPage();
            }
        });
    }

    private void checkForCorrectFriendshipPage() {
        if (isAnimating()) {
            view.postDelayed(this::checkForCorrectFriendshipPage,
                    System.currentTimeMillis() - lastTime + 10);
            return;
        }

        int currentType = -1;
        if (view.request_friend.getVisibility() == View.VISIBLE)
            currentType = 0;
        else if (view.is_friend.getVisibility() == View.VISIBLE)
            currentType = 1;
        else if (view.cancel_request_friend.getVisibility() == View.VISIBLE)
            currentType = 2;
        else if (view.ask_request_friend.getVisibility() == View.VISIBLE)
            currentType = 3;

        int type = 0;
        if (friendshipModel.exists()) {
            if (friendshipModel.accepted) {
                type = 1;
            } else if (MyInfo.getInstance().getId().equals(friendshipModel.fromId)) {
                type = 2;
            } else {
                type = 3;
            }
        }

        if (currentType == type)
            return;

        if (!isEditingProfile) {
            makeFriendshipPageVisible(type);
        } else {
            lastTime = System.currentTimeMillis() + (duration / 2);
            int finalType = type;

            animateFriendshipPage(currentType, false,
                    animation -> {
                        makeFriendshipPageVisible(finalType);
                        animateFriendshipPage(finalType, true, null);
                    });
        }
    }

    private void animateFriendshipPage(int type, boolean show, AXAnimatorEndListener listener) {
        switch (type) {
            case 0:
                requestFriendAnimation(show, 0, listener);
                break;
            case 1:
                isFriendAnimation(show, 0, listener);
                break;
            case 2:
                cancelRequestFriendAnimation(show, 0, listener);
                break;
            case 3:
                askRequestFriendAnimation(show, 0, listener);
                break;
        }
    }

    private void makeFriendshipPageVisible(int type) {
        view.request_friend.setVisibility(View.GONE);
        view.is_friend.setVisibility(View.GONE);
        view.cancel_request_friend.setVisibility(View.GONE);
        view.ask_request_friend.setVisibility(View.GONE);

        switch (type) {
            case 0:
                view.request_friend.setVisibility(View.VISIBLE);
                break;
            case 1:
                view.is_friend.setVisibility(View.VISIBLE);
                break;
            case 2:
                view.cancel_request_friend.setVisibility(View.VISIBLE);
                break;
            case 3:
                view.ask_request_friend.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateTextWithName(TextView tv) {
        String name = view.name.getText().toString();
        tv.setText(tv.getText().toString().replace("X", name));
    }

    private void parallax() {
        if (getArguments() == null)
            return;

        int parallax = getArguments().getInt("parallax", 0);
        boolean visible = getArguments().getBoolean("visible", true);

        parallax = -Math.min(parallax, Utils.dp(304));

        if (-parallax >= Utils.dp(4)) {
            view.parallax_header.setElevation(0);
            if (visible) {
                getArguments().putBoolean("visible", false);
                AXAnimation animation = AXAnimation.create()
                        .duration(150)
                        .scale(0f);
                animation.clone().start(view.profile_parent);
                animation.start(view.chat_parent);
            }
        } else {
            view.parallax_header.setElevation(Utils.dp(10));
            if (!visible) {
                getArguments().putBoolean("visible", true);
                AXAnimation animation = AXAnimation.create()
                        .duration(250)
                        .overshootInterpolator()
                        .scale(1f);
                animation.clone().start(view.profile_parent);
                animation.start(view.chat_parent);
            }
        }
        view.rv_bg.setTranslationY(parallax);
        view.parallax_header.setTranslationY(parallax / 2f);
    }


    public void item_click(ProfileItemAdapter.ItemInfo info, int position, ProfileItemAdapter adapter) {
        if (info.tag == null)
            return;

        switch (info.tag) {
            case "server_name":
            case "username":
                showFragment(EditUsernameFragment.newInstance(profile));
                break;
            case "change_pass":
                showFragment(new ChangePasswordFragment());
                break;
            case "email":
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + ((UserModel) profile).email));
                startActivity(intent);
                break;
            case "online":
                showFragment(ListFragment.newInstance(ListAdapter.TYPE_ONLINE_FRIENDS));
                break;
            case "friends":
                showFragment(ListFragment.newInstance(ListAdapter.TYPE_MY_FRIENDS));
                break;
            case "pending":
                showFragment(ListFragment.newInstance(ListAdapter.TYPE_PENDING_FRIENDS));
                break;
            case "blocked":
                showFragment(ListFragment.newInstance(ListAdapter.TYPE_BlOCK));
                break;
            case "server":
                showFragment(new ServersFragment());
                break;
            case "bot":
                showFragment(new BotFragment());
                break;
            case "devices":
                showFragment(new DevicesFragment());
                break;
            case "logout":
                if (getActivity() != null)
                    new LogoutDialog(getActivity()).show();
                break;
            case "mutual_friends":
                showFragment(ListFragment.newInstance(ListAdapter.TYPE_MUTUAL_FRIENDS,
                        ((UserModel) profile).getId()));
                break;
            case "open_chat":
                EasyApi.openChat((UserModel) profile);
                info.text = "Close Chat";
                info.tag = "close_chat";
                adapter.notifyItemChanged(position);
                break;
            case "close_chat":
                EasyApi.closeChat((UserModel) profile);
                info.text = "Open Chat";
                info.tag = "open_chat";
                adapter.notifyItemChanged(position);
                break;
            case "unblock":
                EasyApi.block(chatFragment.getId(), false);
                info.text = "Block";
                info.tag = "block";
                adapter.notifyItemChanged(position);
                break;
            case "block":
                EasyApi.block(chatFragment.getId(), true);
                info.text = "Unblock";
                info.tag = "unblock";
                adapter.notifyItemChanged(position);
                break;
            case "saved_messages":
                startChat();
                break;
            case "channel":
                chatFragment.setChannel((ServerModel.ServerChannel) info.data);
                startChat();
                break;
            case "invite_code":
                showFragment(InviteCodeFragment.newInstance((ServerModel) profile));
                break;
            case "permissions":
                showFragment(PermissionsFragment.newInstance((ServerModel) profile));
                break;
            case "server_members":
                showFragment(ListFragment.newInstance((ServerModel) profile));
                break;
            case "design":
                showFragment(DesignServerFragment.newInstance((ServerModel) profile));
                break;
            case "leave_server":
            case "delete_server":
                if (getContext() != null)
                    new DeleteOrLeaveDialog((ServerModel) profile, this).show();
                break;
        }
    }

    public void startEditingProfile() {
        if (isEditingProfile || isAnimating())
            return;

        if (chatFragment.isChatting()) {
            onBack();
            return;
        }

        lastTime = System.currentTimeMillis();
        isEditingProfile = true;

        AXAnimation.create()
                .duration(duration)
                .translationX(Utils.dpf(136))
                .start(view.profile_parent);

        AXAnimation.create()
                .duration(duration)
                .translationX(Utils.dpf(136))
                .start(view.chat_parent);

        float x = Utils.perX(50) - Utils.dp(65);
        AXAnimation.create()
                .duration(duration)
                .scale(1.5f)
                .translationX(x)
                .start((View) view.profile.getParent());

        AXAnimation.create()
                .duration(duration / 2)
                .scale(0f)
                .translationX(x / 2)
                .start(view.online);

        AXAnimation.create()
                .duration(duration)
                .translationX(Utils.perX(50) - (view.name.getPaint()
                        .measureText(view.name.getText().toString()) / 2) - Utils.dpf(24))
                .translationY(Utils.dp(30) * 1f)
                .start(view.name);

        AXAnimation animation = AXAnimation.create()
                .duration(duration)
                .translationY(Utils.perY(100) - Utils.dpf(200));
        animation.clone().start(view.rv_bg);
        animation.start(view.rv);
        view.rv.suppressLayout(true);

        if (view.profile_picker.getVisibility() == View.VISIBLE) {
            AXAnimation.create()
                    .delay(duration / 2)
                    .duration(duration)
                    .alpha(1f)
                    .start(view.profile_hint);

            animation = AXAnimation.create()
                    .delay(duration / 2)
                    .duration(duration)
                    .alpha(1f)
                    .overshootInterpolator()
                    .scale(1f);

            animation.clone().start((View) view.cancel.getParent());
            animation.clone().start((View) view.edit.getParent());
            animation.start((View) view.apply.getParent());

        } else if (view.request_friend.getVisibility() == View.VISIBLE)
            requestFriendAnimation(true, duration / 2, null);
        else if (view.cancel_request_friend.getVisibility() == View.VISIBLE)
            cancelRequestFriendAnimation(true, duration / 2, null);
        else if (view.ask_request_friend.getVisibility() == View.VISIBLE)
            askRequestFriendAnimation(true, 2 * duration / 3, null);
        else if (view.is_friend.getVisibility() == View.VISIBLE)
            isFriendAnimation(true, duration / 2, null);
    }

    public void hideEditingProfile() {
        if (!isEditingProfile || isAnimating())
            return;

        lastTime = System.currentTimeMillis();
        isEditingProfile = false;

        if (view.profile_picker.getVisibility() == View.VISIBLE) {
            uploadedBitmap = null;
            if (!uploaded) {
                if (getArguments() != null)
                    getArguments().putBoolean("firstTime", true);

                Utils.getColor(profile, colorUpdater);
                Utils.loadAvatar(profile, view.profile);
                uploaded = false;
            }
        }

        AXAnimation.create()
                .duration(duration)
                .translationX(0f)
                .start(view.profile_parent);

        AXAnimation.create()
                .duration(duration)
                .translationX(0f)
                .start(view.chat_parent);

        AXAnimation.create()
                .duration(duration)
                .scale(1f)
                .translationX(0f)
                .start((View) view.profile.getParent());

        view.online.setTranslationX(0);
        AXAnimation.create()
                .delay(duration)
                .duration(duration / 2)
                .scale(1f)
                .start(view.online);

        AXAnimation.create()
                .duration(duration)
                .translationX(0f)
                .translationY(0f)
                .start(view.name);

        AXAnimation animation = AXAnimation.create()
                .delay(duration / 4)
                .duration(duration)
                .translationY(0f);
        animation.clone().start(view.rv_bg);
        animation.addEndAction(a -> view.rv.suppressLayout(false)).start(view.rv);

        if (view.profile_picker.getVisibility() == View.VISIBLE) {
            AXAnimation.create()
                    .duration(duration)
                    .alpha(0f)
                    .start(view.profile_hint);

            animation = AXAnimation.create()
                    .duration(duration)
                    .alpha(0f)
                    .scale(0f);

            animation.clone().start((View) view.cancel.getParent());
            animation.clone().start((View) view.edit.getParent());
            animation.start((View) view.apply.getParent());

        } else if (view.request_friend.getVisibility() == View.VISIBLE)
            requestFriendAnimation(false, 0, null);
        else if (view.cancel_request_friend.getVisibility() == View.VISIBLE)
            cancelRequestFriendAnimation(false, 0, null);
        else if (view.ask_request_friend.getVisibility() == View.VISIBLE)
            askRequestFriendAnimation(false, 0, null);
        else if (view.is_friend.getVisibility() == View.VISIBLE)
            isFriendAnimation(false, 0, null);
    }

    private void isFriendAnimation(boolean show, long delay, AXAnimatorEndListener listener) {
        simpleRequestFriendAnimation(
                view.is_friend_hint,
                view.remove_friend, delay, show, listener);
    }

    private void requestFriendAnimation(boolean show, long delay, AXAnimatorEndListener listener) {
        simpleRequestFriendAnimation(
                view.request_friend_hint,
                view.send_friend_request, delay, show, listener);
    }

    private void cancelRequestFriendAnimation(boolean show, long delay, AXAnimatorEndListener listener) {
        simpleRequestFriendAnimation(
                view.cancel_request_friend_hint,
                view.cancel_friend_request, delay, show, listener);
    }

    private void simpleRequestFriendAnimation(View hint, View btn, long delay,
                                              boolean show, AXAnimatorEndListener listener) {
        if (show) {
            AXAnimation.create()
                    .delay(delay)
                    .duration(duration)
                    .alpha(1f)
                    .start(hint);

            AXAnimation.create()
                    .delay(delay)
                    .duration(duration)
                    .alpha(1f)
                    .overshootInterpolator()
                    .scale(1f)
                    .start((View) btn.getParent());
        } else {
            AXAnimation.create()
                    .duration(duration)
                    .alpha(0f)
                    .start(hint);

            AXAnimation animation = AXAnimation.create()
                    .duration(duration)
                    .alpha(0f)
                    .scale(0f);

            if (listener != null)
                animation.addEndAction(listener);

            animation.start((View) btn.getParent());
        }
    }


    private void askRequestFriendAnimation(boolean show, long delay, AXAnimatorEndListener listener) {
        if (show) {
            AXAnimation.create()
                    .delay(delay)
                    .duration(duration)
                    .alpha(1f)
                    .start(view.ask_request_friend_hint);

            AXAnimation animation = AXAnimation.create()
                    .delay(delay)
                    .duration(duration)
                    .alpha(1f)
                    .overshootInterpolator()
                    .scale(1f);

            animation.clone().start((View) view.reject_friend_request.getParent());
            animation.start((View) view.accept_friend_request.getParent());
        } else {
            AXAnimation.create()
                    .duration(duration)
                    .alpha(0f)
                    .start(view.ask_request_friend_hint);

            AXAnimation animation = AXAnimation.create()
                    .duration(duration)
                    .alpha(0f)
                    .scale(0f);

            if (listener != null)
                animation.addEndAction(listener);

            animation.clone().start((View) view.reject_friend_request.getParent());
            animation.start((View) view.accept_friend_request.getParent());
        }
    }

    public void startChat() {
        if (isEditingProfile || isAnimating())
            return;

        chatFragment.start();
    }

    public boolean isAnimating() {
        return System.currentTimeMillis() - lastTime < 600;
    }

    public void updateTime() {
        lastTime = System.currentTimeMillis();
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public boolean onBack() {
        if (isAnimating())
            return true;

        if (chatFragment.onBack())
            return true;

        if (isEditingProfile) {
            hideEditingProfile();
            return true;
        }

        return super.onBack();
    }

}
