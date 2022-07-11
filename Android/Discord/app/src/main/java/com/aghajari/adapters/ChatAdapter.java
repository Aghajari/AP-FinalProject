package com.aghajari.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.R;
import com.aghajari.axanimation.AXAnimation;
import com.aghajari.fragments.ChatFragment;
import com.aghajari.message_first_mine;
import com.aghajari.message_first_others;
import com.aghajari.message_first_others_server;
import com.aghajari.message_last_others;
import com.aghajari.message_mine;
import com.aghajari.message_others;
import com.aghajari.message_text;
import com.aghajari.message_time;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.MessageModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.EmptyView;
import com.aghajari.views.Utils;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatAdapter extends BaseAdapter<BaseAdapter.VH> {

    private static final int TYPE_TIME = 0;
    private static final int TYPE_MY_FIRST_MESSAGE = 1;
    private static final int TYPE_MY_MESSAGE = 2;
    private static final int TYPE_OTHERS_FIRST_MESSAGE = 3;
    private static final int TYPE_OTHERS_MESSAGE = 4;
    private static final int TYPE_OTHERS_LAST_MESSAGE = 5;
    private static final int TYPE_SERVER_FIRST_LAST_MESSAGE = 6;
    private static final int TYPE_FIRST_LAST_MESSAGE = 7;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.US);

    private RecyclerView rv;
    private final IndexedLinkedHashMap<String, MessageInfo> messages = new IndexedLinkedHashMap<>();
    private final boolean fromServer;
    private final String id;
    private final ChatFragment chatFragment;

    public ChatAdapter(String id, boolean fromServer, ChatFragment chatFragment) {
        super(chatFragment.getProfileFragment());
        this.chatFragment = chatFragment;

        setLoading(true);
        EasyApi.getMessages(this.id = id, this.fromServer = fromServer, model -> {
            List<MessageModel> list = model.get();
            RecyclerView r = rv;
            rv = null;
            messages.clear();

            for (MessageModel m : list)
                addMessage(m);

            rv = r;
            Utils.ui(() -> {
                setLoading(false);
                if (rv != null)
                    rv.scrollToPosition(size() - 1);
                animate();
            });
        });
    }

    public String getId() {
        return id;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = recyclerView;
        rv.scrollToPosition(size() - 1);
        animate();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        rv = null;
    }

    public void animate() {
        if (rv == null)
            return;
        Utils.animate(rv);
    }

    @Override
    public int size() {
        return messages.size();
    }

    @Override
    public VH create(Context context, int viewType) {
        switch (viewType) {
            case TYPE_TIME:
                return new VH(new message_time(context));
            case TYPE_MY_MESSAGE:
                return new VH(new message_mine(context));
            case TYPE_MY_FIRST_MESSAGE:
                return new VH(new message_first_mine(context));
            case TYPE_OTHERS_FIRST_MESSAGE:
            case TYPE_SERVER_FIRST_LAST_MESSAGE:
                if (fromServer)
                    return new VH(new message_first_others_server(context));
                else
                    return new VH(new message_first_others(context));
            case TYPE_OTHERS_MESSAGE:
                return new VH(new message_others(context));
            case TYPE_OTHERS_LAST_MESSAGE:
            case TYPE_FIRST_LAST_MESSAGE:
                return new VH(new message_last_others(context));
        }
        return new VH(new View(context));
    }

    @Override
    public void bind(VH viewHolder, int position) {
        MessageInfo info = messages.getValueAt(position);

        String time_text = timeFormat.format(new Date(info.model.time));
        message_text text = null;

        switch (viewType(position)) {
            case TYPE_TIME:
                message_time time = viewHolder.get();
                time.date.setText(messages.getKeyAt(position));
                break;
            case TYPE_MY_MESSAGE:
                message_mine mine = viewHolder.get();
                text = mine.message;
                mine.time.setText(time_text);
                break;
            case TYPE_MY_FIRST_MESSAGE:
                message_first_mine first_mine = viewHolder.get();
                text = first_mine.message;
                first_mine.time.setText(time_text);
                break;
            case TYPE_OTHERS_FIRST_MESSAGE:
                if (fromServer) {
                    message_first_others_server first_others = viewHolder.get();
                    text = first_others.message;
                    first_others.time.setText(time_text);
                    if (info.model.getUser() != null) {
                        Utils.loadAvatar(info.model.getUser(), first_others.avatar);
                        first_others.title.setText(info.model.getUser().getName());
                    }
                } else {
                    message_first_others first_others = viewHolder.get();
                    text = first_others.message;
                    first_others.time.setText(time_text);
                }
                break;
            case TYPE_SERVER_FIRST_LAST_MESSAGE:
                message_first_others_server first_others = viewHolder.get();
                ((View) first_others.message.getParent())
                        .setBackgroundResource(R.drawable.message_last_others);

                text = first_others.message;
                first_others.time.setText(time_text);
                if (info.model.getUser() != null) {
                    Utils.loadAvatar(info.model.getUser(), first_others.avatar);
                    first_others.title.setText(info.model.getUser().getName());
                }
                break;
            case TYPE_OTHERS_MESSAGE:
                message_others others = viewHolder.get();
                text = others.message;
                others.time.setText(time_text);
                if (fromServer) {
                    others.getChildAt(0).setTranslationX(Utils.dp(34));
                    others.getChildAt(1).setTranslationX(Utils.dp(34));
                }
                break;
            case TYPE_OTHERS_LAST_MESSAGE:
                message_last_others last_others = viewHolder.get();
                text = last_others.message;
                last_others.time.setText(time_text);
                if (fromServer) {
                    last_others.getChildAt(0).setTranslationX(Utils.dp(34));
                    last_others.getChildAt(1).setTranslationX(Utils.dp(34));
                }
                break;
            case TYPE_FIRST_LAST_MESSAGE:
                message_last_others last_others2 = viewHolder.get();
                ((ViewGroup.MarginLayoutParams) last_others2.getLayoutParams()).topMargin
                        = Utils.dp(8);
                text = last_others2.message;
                last_others2.time.setText(time_text);
                if (fromServer) {
                    last_others2.getChildAt(0).setTranslationX(Utils.dp(34));
                    last_others2.getChildAt(1).setTranslationX(Utils.dp(34));
                }
                break;
        }

        if (text != null) {
            text.text.setText(info.model.text);
            text.reactions.removeAllViews();

            ((View) text.getParent()).setOnClickListener(v ->
                    chatFragment.showReactionView(viewHolder, v, info.model));

            if (info.model.reactions != null && info.model.reactions.size() > 0) {
                text.reactions.setVisibility(View.VISIBLE);
                for (Integer reaction : info.model.reactions.values()) {
                    AppCompatImageView img = new AppCompatImageView(text.getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            Utils.dp(16), Utils.dp(16));
                    lp.rightMargin = Utils.dp(4);
                    text.reactions.addView(img, lp);

                    Glide.with(img)
                            .load(Uri.parse("file:///android_asset/reactions/" + reaction + ".png"))
                            .into(img);
                }
            } else {
                text.reactions.setVisibility(View.GONE);
            }
        }

        if (info.animate) {
            info.animate = false;
            viewHolder.itemView.setAlpha(0f);
            viewHolder.itemView.setTranslationY(Utils.dp(56));

            AXAnimation.create()
                    .duration(250)
                    .alpha(1f)
                    .translationY(0f)
                    .start(viewHolder.itemView);
        }
    }

    @Override
    public int viewType(int position) {
        int type = messages.getValueAt(position).viewType;
        if (position == size() - 1 &&
                (type == TYPE_OTHERS_MESSAGE || type == TYPE_OTHERS_FIRST_MESSAGE))
            return type == TYPE_OTHERS_FIRST_MESSAGE ? (fromServer
                    ? TYPE_SERVER_FIRST_LAST_MESSAGE
                    : TYPE_FIRST_LAST_MESSAGE)
                    : TYPE_OTHERS_LAST_MESSAGE;

        return type;
    }

    @Override
    public VH createEmpty(Context context) {
        EmptyView view = new EmptyView(context, EmptyView.Type.NO_CHAT);
        ViewGroup btn = view.findViewById(R.id.chat_rv);
        btn.setBackground(chatFragment.cloneDrawable());
        View v = btn.getChildAt(0);
        v.setBackgroundResource(resolveAttribute(context, R.attr.selectableItemBackground));
        if (!chatFragment.canSendMessage())
            btn.setVisibility(View.GONE);
        v.setOnClickListener(v0 -> chatFragment.sendMessage("Hello"));
        return new VH(view);
    }

    private int resolveAttribute(Context context, int attrId) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, outValue, true);
        return outValue.resourceId;
    }

    @Override
    public VH createViewHolder(View view) {
        return new VH(view);
    }

    @Override
    public int loadingTopMargin() {
        return 0;
    }

    @Override
    public int loadingGravity() {
        return Gravity.CENTER;
    }

    private int year = 0, day = 0;
    private String type = "null";

    public synchronized void addMessage(MessageModel model) {
        boolean fromMe = model.fromId.equals(MyInfo.getInstance().getId());
        String lastType = type;
        type = fromMe ? "me" : model.fromId;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.time);
        int y = calendar.get(Calendar.YEAR);
        int d = calendar.get(Calendar.DAY_OF_YEAR);

        String date = null;
        if (year != y || day != d) {
            lastType = "null";
            year = y;
            day = d;

            date = dateFormat.format(new Date(model.time));
        }

        int size = messages.size();

        boolean same = lastType.equals(type);
        int position = messages.getPosition();

        RecyclerView.ItemAnimator animator = rv == null ? null : rv.getItemAnimator();

        if (!same) {
            MessageInfo last = messages.lastValue();
            if (last != null && (last.viewType == TYPE_OTHERS_MESSAGE ||
                    last.viewType == TYPE_OTHERS_FIRST_MESSAGE)) {
                messages.lastValue().viewType =
                        last.viewType == TYPE_OTHERS_FIRST_MESSAGE ? (fromServer
                                ? TYPE_SERVER_FIRST_LAST_MESSAGE
                                : TYPE_FIRST_LAST_MESSAGE)
                                : TYPE_OTHERS_LAST_MESSAGE;

                if (!isLoading() && rv != null) {
                    rv.setItemAnimator(null);
                    notifyItemChanged(messages.getPosition() - 1);
                    if (animator != null)
                        rv.postDelayed(() -> rv.setItemAnimator(animator), 150);
                }
            }

            if (date != null)
                messages.put(date, new MessageInfo(TYPE_TIME, model, true));

            messages.put(String.valueOf(model.index),
                    new MessageInfo(fromMe ? TYPE_MY_FIRST_MESSAGE
                            : TYPE_OTHERS_FIRST_MESSAGE, model, true));
        } else {
            if (date != null)
                messages.put(date, new MessageInfo(TYPE_TIME, model, true));

            messages.put(String.valueOf(model.index),
                    new MessageInfo(fromMe ? TYPE_MY_MESSAGE
                            : TYPE_OTHERS_MESSAGE, model, true));
        }

        if (!isLoading() && rv != null) {
            if (size == 0) {
                notifyDataSetChanged();
                return;
            }

            rv.setItemAnimator(null);
            int count = 1 + (date == null ? 0 : 1);
            notifyItemRangeInserted(position, count);
            rv.invalidateItemDecorations();
            if (animator != null)
                rv.postDelayed(() -> rv.setItemAnimator(animator), 150);
        }
    }

    public boolean notifyNewMessage(MessageModel model) {
        if ((model.fromId.equals(id) && model.toId.equals(MyInfo.getInstance().getId()))
                || (model.fromId.equals(MyInfo.getInstance().getId()) && model.toId.equals(id))
                || (model.toId.contains("#") && model.toId.equals(id))) {

            String key = String.valueOf(model.index);
            MessageInfo inf = messages.get(key);

            if (inf != null) {
                inf.model = model;
                for (Map.Entry<Integer, String> entry : messages.indexes.entrySet()) {
                    if (entry.getValue().equals(key)) {
                        Utils.ui(() -> {
                            if (!isLoading())
                                notifyItemChanged(entry.getKey());
                        });
                        break;
                    }
                }
            } else {
                Utils.ui(() -> addMessage(model));
                return true;
            }
        }
        return false;
    }

    private static class MessageInfo {
        private int viewType;
        private MessageModel model;
        private boolean animate;

        public MessageInfo(int viewType, MessageModel model) {
            this(viewType, model, false);
        }

        public MessageInfo(int viewType, MessageModel model, boolean animate) {
            this.viewType = viewType;
            this.model = model;
            this.animate = animate;
        }
    }

    private static class IndexedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

        private final HashMap<Integer, K> indexes = new HashMap<>();
        private int pos = 0;

        @Override
        public V put(K key, V val) {
            V out = super.put(key, val);
            indexes.put(pos++, key);
            return out;
        }

        public K getKeyAt(int i) {
            return indexes.get(i);
        }

        public V getValueAt(int i) {
            return super.get(indexes.get(i));
        }

        public V lastValue() {
            if (pos == 0)
                return null;

            return getValueAt(pos - 1);
        }

        public int getPosition() {
            return pos;
        }
    }
}
