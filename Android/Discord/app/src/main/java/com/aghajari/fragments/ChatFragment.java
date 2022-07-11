package com.aghajari.fragments;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.R;
import com.aghajari.adapters.BaseAdapter;
import com.aghajari.adapters.ChatAdapter;
import com.aghajari.axanimation.AXAnimation;
import com.aghajari.models.MyInfo;
import com.aghajari.profile_fragment;
import com.aghajari.rlottie.AXrLottieDrawable;
import com.aghajari.shared.models.IsTypingModel;
import com.aghajari.shared.models.MessageModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticListeners;
import com.aghajari.views.ClipFrameLayout;
import com.aghajari.views.KeyboardHeightProvider;
import com.aghajari.views.PermissionUtils;
import com.aghajari.views.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

// this one isn't really a fragment
public class ChatFragment {

    private boolean isChatting = false;

    private final ProfileFragment profileFragment;
    private final profile_fragment view;
    private final long duration = 350;
    private ViewGroup.LayoutParams onlineLayoutParams;

    private final int footerBottomMargin;
    private KeyboardHeightProvider heightProvider;
    private ChatAdapter adapter = null;
    private String id;
    private ServerModel.ServerChannel channel = null;

    private ReactionView reactionView = null;

    public ChatFragment(ProfileFragment profileFragment, profile_fragment view) {
        if (profileFragment.getProfile() instanceof UserModel)
            id = ((UserModel) profileFragment.getProfile()).getId();
        else
            id = ((ServerModel) profileFragment.getProfile()).serverId;

        this.profileFragment = profileFragment;
        this.view = view;

        footerBottomMargin = ((FrameLayout.LayoutParams) view.chat_footer.getLayoutParams())
                .bottomMargin = Utils.getNavigationBarSize().y - Utils.getStatusBarHeight();

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.chat_rv.getLayoutParams();
        lp.topMargin = Utils.dp(64) + Utils.getStatusBarHeight();
        lp.bottomMargin = footerBottomMargin + Utils.dp(56 + 24);

        LinearLayoutManager lm = new LinearLayoutManager(view.getContext());
        lm.setStackFromEnd(true);
        view.chat_rv.setLayoutManager(lm);

        Utils.addSpace(view.chat_rv, 24, 24);
        watchMessage();

        view.send.setOnClickListener(v -> {
            String text = Utils.textOf(view.message_edt);
            if (sendMessage(text))
                view.message_edt.setText("");
        });

        StaticListeners.isTypingUpdater = this::notifyTyping;
        StaticListeners.updateBlockUnblock = this::checkForBlockingStatus;
        checkForBlockingStatus();
    }

    public void onDestroy() {
        if (heightProvider != null)
            heightProvider.close();

        StaticListeners.messageUpdater = null;
        StaticListeners.isTypingUpdater = null;
        StaticListeners.updateBlockUnblock = null;
    }

    public boolean onBack() {
        if (reactionView != null) {
            reactionView.close();
            return true;
        }

        if (isChatting) {
            hide();
            return true;
        }
        return false;
    }

    public boolean isChatting() {
        return isChatting;
    }

    public void start() {
        if (profileFragment.isAnimating() || isChatting)
            return;

        checkForBlockingStatus();

        String text = null;
        if (profileFragment.getProfile() == MyInfo.getInstance())
            text = "Saved Messages";
        else if (profileFragment.getProfile() instanceof UserModel)
            text = ((UserModel) profileFragment.getProfile()).nickname;
        else if (channel != null)
            text = channel.name;

        if (TextUtils.isEmpty(text))
            text = view.name.getText().toString();

        view.subtitle.setText(view.subtitle.getTag(R.id.subtitle) != null
                ? view.subtitle.getTag(R.id.subtitle).toString() : text);
        view.subtitle.setTag(text);

        if (adapter == null || !getChatId().equals(adapter.getId()))
            view.chat_rv.setAdapter(adapter = new ChatAdapter(
                    getChatId(), channel != null, this));

        StaticListeners.messageUpdater = this::notifyNewMessage;

        onlineLayoutParams = view.online.getLayoutParams();
        profileFragment.updateTime();
        isChatting = true;

        if (view.send_parent.getBackground() == null)
            view.send_parent.setBackground(cloneDrawable());

        view.rv.suppressLayout(true);

        if (heightProvider != null)
            heightProvider.close();

        heightProvider = new KeyboardHeightProvider(view.message_edt,
                profileFragment.getActivity(), this::animateKeyboard);

        heightProvider.start();

        int headerFrom = 200, headerTo = 64;
        float y = -Utils.dpf(headerFrom - headerTo) + Utils.getStatusBarHeight();
        AXAnimation.create()
                .duration(duration)
                .translationY(y)
                .start(view.header);

        int from = 82, to = 46;
        float scale = Utils.dpf(to) / Utils.dpf(from);

        AXAnimation.create()
                .duration(duration)
                .scale(scale)
                .translationX(-Utils.dpf((from - to) / 2))
                .translationY(-Utils.dpf(77) - Utils.dpf(headerTo))
                .start((View) view.profile.getParent());

        AXAnimation.create()
                .duration(duration)
                .scale((Utils.dpf(to) + (Utils.dpf(8) * scale)) / Utils.dpf(to))
                .start(view.profile);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                Utils.dp(12), Utils.dp(12));
        lp.leftMargin = Utils.dp(21) + Utils.dp(to) - Utils.dp(8);
        lp.topMargin = Utils.dp(headerTo) + Utils.getStatusBarHeight()
                - lp.width - Utils.dp(10);

        AXAnimation.create()
                .duration(duration)
                .toLayoutParams(lp)
                .start(view.online);

        AXAnimation.create()
                .duration(duration)
                .scale(Utils.dpf(24) / Utils.dpf(36))
                .translationY(-Utils.dpf(154) - Utils.dpf(headerTo))
                .start(view.name);

        AXAnimation.create()
                .duration(duration)
                .scale(Utils.dpf(24) / Utils.dpf(36))
                .translationY(-Utils.dpf(154) - Utils.dpf(headerTo) - Utils.dpf(12))
                .alpha(1f)
                .start(view.subtitle);

        AXAnimation animation = AXAnimation.create()
                .duration(duration)
                .alpha(0f)
                .scale(0f)
                .translationY(y);
        animation.clone().start(view.chat_parent);
        animation.start(view.profile_parent);

        AXAnimation.create()
                .duration(duration)
                .translationY(0f)
                .withEndAction(v -> view.parallax_header.setElevation(Utils.dp(10)))
                .start(view.parallax_header);

        AXAnimation.create()
                .duration(duration)
                .translationY(y - Utils.dpf(105))
                .start(view.rv_bg);

        AXAnimation.create()
                .duration(duration)
                .accelerateDecelerateInterpolator()
                .alpha(0f)
                .translationY(Utils.perY(100) + Utils.dpf(24))
                .start(view.rv);

        view.chat_footer.setAlpha(0f);
        view.chat_footer.setVisibility(View.VISIBLE);
        view.chat_footer.setTranslationY(Utils.dpf(56));
        AXAnimation.create()
                .duration(duration)
                .translationY(0f)
                .alpha(1f)
                .start(view.chat_footer);

        view.chat_rv.setAlpha(1f);
        view.chat_rv.setTranslationY(0f);
        view.chat_rv.setVisibility(View.VISIBLE);

        if (view.chat_rv.getAdapter() != null)
            ((ChatAdapter) view.chat_rv.getAdapter()).animate();
    }

    private void hide() {
        if (profileFragment.isAnimating() || !isChatting)
            return;

        Utils.hideSoftKeyboard(view.message_edt);

        if (heightProvider != null)
            heightProvider.close();
        animateKeyboard(0);

        heightProvider = null;

        profileFragment.updateTime();
        isChatting = false;

        int parallax = profileFragment.getArguments().getInt("parallax", 0);
        boolean visible = profileFragment.getArguments().getBoolean("visible", true);

        parallax = -Math.min(parallax, Utils.dp(304));

        if (visible)
            view.parallax_header.setElevation(Utils.dp(10));
        else
            view.parallax_header.setElevation(0);

        AXAnimation.create()
                .duration(duration)
                .translationY(parallax / 2f)
                .start(view.parallax_header);

        AXAnimation.create()
                .duration(duration)
                .translationY(0f)
                .start(view.header);

        AXAnimation.create()
                .duration(duration)
                .scale(1f)
                .translationX(0f)
                .translationY(0f)
                .start((View) view.profile.getParent());

        AXAnimation.create()
                .duration(duration)
                .scale(1f)
                .start(view.profile);

        AXAnimation.create()
                .duration(duration)
                .toLayoutParams(onlineLayoutParams)
                .start(view.online);

        AXAnimation.create()
                .duration(duration)
                .scale(1f)
                .translationY(0f)
                .start(view.name);

        AXAnimation.create()
                .duration(duration)
                .alpha(0f)
                .scale(1f)
                .translationY(0f)
                .start(view.subtitle);

        AXAnimation animation = AXAnimation.create()
                .duration(duration)
                .alpha(1f)
                .scale(visible ? 1f : 0f)
                .translationY(0f);
        animation.clone().start(view.chat_parent);
        animation.start(view.profile_parent);

        AXAnimation.create()
                .duration(duration)
                .translationY(parallax * 1f)
                .start(view.rv_bg);

        AXAnimation.create()
                .duration(duration)
                .decelerateInterpolator()
                .alpha(1f)
                .translationY(0f)
                .withEndAction(anim -> view.rv.suppressLayout(false))
                .start(view.rv);

        AXAnimation.create()
                .duration(duration / 2)
                .alpha(0f)
                .translationY(Utils.dpf(56))
                .withEndAction(anim -> view.chat_footer.setVisibility(View.GONE))
                .start(view.chat_footer);

        AXAnimation.create()
                .duration(duration / 2)
                .alpha(0f)
                .translationY(Utils.dpf(56))
                .withEndAction(anim -> view.chat_rv.setVisibility(View.GONE))
                .start(view.chat_rv);
    }

    private ValueAnimator animator, animator2;

    private void watchMessage() {
        view.message_edt.addTextChangedListener(new TextWatcher() {

            long lastTime = 0;
            boolean isTyping = false;

            final Runnable run = () -> {
                if (System.currentTimeMillis() - lastTime > 1000) {
                    EasyApi.requestIsTyping(isTyping = false, getChatId(),
                            profileFragment.getProfile() instanceof ServerModel
                                    ? ((ServerModel) profileFragment.getProfile()).serverId
                                    : null);
                } else {
                    start();
                }
            };

            private void start() {
                view.message_edt.postDelayed(run, 500);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                animateHeight(Math.max(Utils.dp(56),
                        Math.min(Utils.dp(160),
                                Utils.getTextHeight(view.message_edt))));

                if (channel == null || channel.type != 0) {
                    lastTime = System.currentTimeMillis();
                    if (!isTyping)
                        EasyApi.requestIsTyping(isTyping = true, getChatId(),
                                profileFragment.getProfile() instanceof ServerModel
                                        ? ((ServerModel) profileFragment.getProfile()).serverId
                                        : null);
                    start();
                }
            }
        });
    }

    private void animateHeight(int height) {
        if (animator2 != null)
            animator2.cancel();

        View m = (View) view.message_edt.getParent();
        FrameLayout.LayoutParams lp0 = (FrameLayout.LayoutParams) m.getLayoutParams();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.chat_footer.getLayoutParams();
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) view.chat_rv.getLayoutParams();

        int currentHeight = lp0.height;
        if (currentHeight == height) {
            animator2 = null;
            return;
        }
        view.message_edt.getLayoutParams().height = height;

        animator2 = ValueAnimator.ofInt(currentHeight, height);
        animator2.setDuration(100);
        animator2.addUpdateListener(animation -> {
            if (profileFragment.getContext() == null) {
                animation.cancel();
                return;
            }

            lp0.height = (int) animation.getAnimatedValue();
            lp.height = lp0.height + Utils.dp(48);
            lp2.bottomMargin = lp.bottomMargin + lp.height - Utils.dp(24);

            m.requestLayout();
            view.chat_footer.requestLayout();
            view.chat_rv.requestLayout();
        });
        animator2.start();
    }

    private void animateKeyboard(int keyboardHeight) {
        if (animator != null)
            animator.cancel();

        FrameLayout.LayoutParams lp =
                (FrameLayout.LayoutParams) view.chat_footer.getLayoutParams();
        if (lp.bottomMargin == footerBottomMargin + keyboardHeight) {
            animator = null;
            return;
        }

        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) view.chat_rv.getLayoutParams();

        animator = ValueAnimator.ofInt(lp.bottomMargin, footerBottomMargin + keyboardHeight);
        animator.setDuration(duration / 2);
        animator.addUpdateListener(animation -> {
            if (profileFragment.getContext() == null) {
                animation.cancel();
                return;
            }

            lp.bottomMargin = (int) animation.getAnimatedValue();
            lp2.bottomMargin = lp.bottomMargin + view.chat_footer.getMeasuredHeight() - Utils.dp(24);
            view.chat_footer.requestLayout();
            view.chat_rv.requestLayout();
        });
        animator.start();
    }

    boolean notifyNewMessage(MessageModel model) {
        if (adapter == null)
            return false;

        try {
            boolean scrollToEnd = false;
            try {
                scrollToEnd = ((LinearLayoutManager) view.chat_rv.getLayoutManager())
                        .findLastVisibleItemPosition() >= adapter.size() - 1;
            } catch (Exception ignore) {
            }
            if (adapter.notifyNewMessage(model) && scrollToEnd)
                Utils.ui(() -> view.chat_rv.smoothScrollToPosition(adapter.size() - 1));
        } catch (Exception ignore) {
        }
        return model.fromId.equals(adapter.getId()) || model.toId.equals(adapter.getId());
    }

    void notifyTyping(IsTypingModel.IsTypingResponse model) {
        if (!getChatId().equals(model.id))
            return;

        if (channel != null && channel.type == 0)
            return;

        if (model.typings == null || model.typings.isEmpty() ||
                (model.typings.size() == 1 && model.typings.keySet().
                        iterator().next().equals(MyInfo.getInstance().getId()))) {

            view.subtitle.setTag(R.id.subtitle, null);
            view.subtitle.setText(view.subtitle.getTag().toString());
        } else {
            if (model.typings.size() == 1 && profileFragment.getProfile() instanceof UserModel) {
                String text = "is typing...";

                view.subtitle.setTag(R.id.subtitle, text);
                view.subtitle.setText(text);
                return;
            }

            StringBuilder names = new StringBuilder();
            int count = 0;
            for (Map.Entry<String, String> entry : model.typings.entrySet()) {
                if (entry.getKey().equals(MyInfo.getInstance().getId()))
                    continue;
                names.append(entry.getValue()).append(", ");
                count++;
                if (count == 3)
                    break;
            }
            names = new StringBuilder(names.substring(0, names.length() - 2));
            if (count == 3 && model.typings.size() > 3) {
                names.append(" and ").append(model.typings.size() - 3).append(" others");
            }
            if (count > 1)
                names.append(" are typing...");
            else
                names.append(" is typing...");

            view.subtitle.setTag(R.id.subtitle, names.toString());
            if (channel != null)
                names.insert(0, channel.name + ": ");

            view.subtitle.setText(names);
        }
    }

    public GradientDrawable cloneDrawable() {
        GradientDrawable gd = (GradientDrawable) view.header.getBackground()
                .getConstantState().newDrawable().mutate();
        gd.setCornerRadius(Utils.dp(100));
        return gd;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }

    public void showReactionView(BaseAdapter.VH vh, View view, MessageModel model) {
        if (reactionView != null)
            reactionView.close();

        reactionView = new ReactionView(vh, view, model);
    }

    public boolean canSendMessage() {
        return view.access_panel.getVisibility() == View.GONE;
    }

    public boolean sendMessage(String text) {
        if (adapter == null || !isChatting || TextUtils.isEmpty(text))
            return false;
        if (!canSendMessage())
            return false;

        MessageModel messageModel = new MessageModel(text,
                System.currentTimeMillis(),
                MyInfo.getInstance().getId(),
                getChatId());

        EasyApi.sendMessage(messageModel, done -> {
            MessageModel res = done.get();
            if (res == null)
                return;

            boolean scrollToEnd = false;
            try {
                scrollToEnd = ((LinearLayoutManager) view.chat_rv.getLayoutManager())
                        .findLastVisibleItemPosition() >= adapter.size() - 1;
            } catch (Exception ignore) {
            }
            if (scrollToEnd) {
                adapter.addMessage(res);
                view.chat_rv.smoothScrollToPosition(adapter.size() - 1);
            }
        });
        return true;
    }

    public ServerModel.ServerChannel getChannel() {
        return channel;
    }

    public void setChannel(ServerModel.ServerChannel channel) {
        this.channel = channel;
    }

    public String getId() {
        return id;
    }

    public String getChatId() {
        return id + (channel == null ? "" : "#" + channel.id);
    }

    public void checkForBlockingStatus() {
        checkForBlockingStatus(profileFragment.getProfile() instanceof UserModel
                ? (UserModel) profileFragment.getProfile() : null);
    }

    public void checkForBlockingStatus(UserModel user) {
        view.access.setOnClickListener(v -> { // DO NOTHING
        });

        if (profileFragment.getProfile() instanceof UserModel
                && this.id.equals(user.getId())) {
            EasyApi.getBlockStatus(id, model -> {
                int status = model.get();
                if (status != 0) {
                    view.access_panel.setVisibility(View.VISIBLE);
                    Utils.hideSoftKeyboard(view.message_edt);
                    animateHeight(Utils.dp(56));
                    animateKeyboard(0);

                    if (status == 1)
                        view.access.setText("You have blocked " + user.getName() + "!");
                    else
                        view.access.setText("You don't have access!");
                } else {
                    view.access_panel.setVisibility(View.GONE);
                }
            });
        } else if (profileFragment.getProfile() instanceof ServerModel && channel != null) {
            view.access.setText("You don't have access!");
            view.access_panel.setVisibility(
                    PermissionUtils.canSendMessage(
                            (ServerModel) profileFragment.getProfile(), channel)
                            ? View.GONE : View.VISIBLE);

            if (view.access_panel.getVisibility() == View.VISIBLE) {
                Utils.hideSoftKeyboard(view.message_edt);
                animateHeight(Utils.dp(56));
                animateKeyboard(0);
            }
        }
    }

    private class ReactionView extends FrameLayout {

        public ReactionView(@NonNull BaseAdapter.VH vh, View v, MessageModel model) {
            super(v.getContext());
            setElevation(Utils.dp(100)); // go on top

            ClipFrameLayout frame = new ClipFrameLayout(v.getContext());
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(v.getResources().getColor(R.color.background_dark));
            gd.setCornerRadius(Utils.dp(56));
            frame.setBackground(gd);
            frame.setElevation(Utils.dp(4));

            int[] loc = new int[2];
            v.getLocationOnScreen(loc);

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    Utils.dp(200), Utils.dp(56));
            boolean fromMe = loc[0] > Utils.dp(100);
            if (fromMe) {
                loc[0] -= lp.width - Utils.dp(12);
            } else {
                loc[0] += v.getMeasuredWidth() - Utils.dp(12);
            }

            lp.leftMargin = Math.min(Utils.perX(100) - Utils.dp(212),
                    Math.max(0, loc[0]));
            lp.topMargin = Math.max(Utils.dp(68), loc[1] - (lp.height / 2));
            frame.setAlpha(0);
            frame.setScaleX(0);
            frame.setScaleY(0);
            addView(frame, lp);
            view.addView(this, new FrameLayout.LayoutParams(-1, -1));

            RecyclerView rv = new RecyclerView(frame.getContext());
            rv.setLayoutManager(new LinearLayoutManager(frame.getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    int index = parent.getChildViewHolder(view).getAdapterPosition();
                    if (index == 0)
                        outRect.left = Utils.dp(6);
                }
            });
            frame.addView(rv);
            rv.setAdapter(new Adapter(v0 -> {
                if (adapter == null || reactionView == null)
                    return;

                int finalI = (int) v0.getTag();

                if (model.reactions == null)
                    model.reactions = new HashMap<>();

                Integer old = model.reactions.get(MyInfo.getInstance().getId());
                if (old != null && old == finalI)
                    model.reactions.remove(MyInfo.getInstance().getId());
                else
                    model.reactions.put(MyInfo.getInstance().getId(), finalI);

                EasyApi.reaction(model.index, model.reactions
                        .containsKey(MyInfo.getInstance().getId()) ? finalI : 0);
                adapter.notifyItemChanged(vh.getAdapterPosition());
                close();
            }));

            AXAnimation.create()
                    .duration(duration)
                    .alpha(1f)
                    .overshootInterpolator()
                    .scale(1f)
                    .start(frame);

            setOnClickListener(v2 -> close());
        }

        public void close() {
            if (!isClickable())
                return;

            setClickable(false);

            AXAnimation.create()
                    .duration(duration / 2)
                    .alpha(0f)
                    .scale(0f)
                    .withEndAction(a -> view.removeView(this))
                    .start(getChildAt(0));
            reactionView = null;
        }

        private class Adapter extends RecyclerView.Adapter<BaseAdapter.VH> {

            private final View.OnClickListener clickListener;

            private Adapter(OnClickListener clickListener) {
                this.clickListener = clickListener;
            }

            @NonNull
            @Override
            public BaseAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                AppCompatImageView lottieImageView = new AppCompatImageView(parent.getContext());
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        Utils.dp(44), Utils.dp(44));
                lp.rightMargin = lp.topMargin = lp.bottomMargin = Utils.dp(6);
                lottieImageView.setLayoutParams(lp);
                return new BaseAdapter.VH(lottieImageView);
            }

            @Override
            public void onBindViewHolder(@NonNull BaseAdapter.VH holder, int position) {
                AppCompatImageView lottieImageView = holder.get();
                lottieImageView.setImageDrawable(
                        AXrLottieDrawable.fromAssets(lottieImageView.getContext(),
                                "reactions/" + (position + 1) + ".json")
                                .setAutoRepeat(1)
                                .setAutoStart(true)
                                .build()
                );
                lottieImageView.setTag(position + 1);
                lottieImageView.setOnClickListener(clickListener);
            }

            @Override
            public int getItemCount() {
                return 18;
            }
        }
    }
}
