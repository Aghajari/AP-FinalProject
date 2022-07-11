package com.aghajari.views;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.Application;
import com.aghajari.R;
import com.aghajari.adapters.BaseAdapter;
import com.aghajari.shared.Profile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static int dp(int size) {
        return (int) (Application.context.getResources().getDisplayMetrics().density * size);
    }

    public static float dpf(int size) {
        return Application.context.getResources().getDisplayMetrics().density * size;
    }

    public static int perY(int size) {
        return (Application.context.getResources().getDisplayMetrics().heightPixels * size / 100);
    }

    public static int perX(int size) {
        return (Application.context.getResources().getDisplayMetrics().widthPixels * size / 100);
    }

    public static boolean isNumeric(String strNum) {
        if (TextUtils.isEmpty(strNum))
            return false;
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = Application.context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = Application.context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setStatusBarLight(Activity activity, boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View v = activity.getWindow().getDecorView();
            if (light) {
                v.setSystemUiVisibility(v.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                v.setSystemUiVisibility(v.getSystemUiVisibility() ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

    }

    public static File getSafeDir() {
        File[] f = ContextCompat.getExternalFilesDirs(Application.context, "");
        if (f.length == 0 || f[0] == null)
            return Application.context.getFilesDir();
        else
            return f[0];
    }

    public static Bitmap createBitmap(String name) {
        String text = name.trim();
        if (text.length() > 2) {
            if (text.contains(" ")) {
                int a = text.indexOf(" ");
                text = "" + text.charAt(0) + text.charAt(a + 1);
            } else {
                text = text.substring(0, 2);
            }
        }
        text = text.toUpperCase();

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, 0, 100, 0,
                Application.context.getResources().getColor(R.color.color1),
                Application.context.getResources().getColor(R.color.color2),
                Shader.TileMode.MIRROR));
        canvas.drawPaint(paint);

        paint.setTypeface(ResourcesCompat.getFont(Application.context, R.font.regular));
        paint.setTextSize(50);
        paint.setColor(Color.WHITE);
        paint.setShader(null);

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, 50 - rect.centerX(), 50 - rect.centerY(), paint);
        return bitmap;
    }

    public static String textOf(TextView tv) {
        if (tv.getText() == null)
            return "";

        return tv.getText().toString().trim();
    }

    private static final String USERNAME_PATTERN =
            "^[a-zA-Z0-9]([_](?![_])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    private static final Pattern password = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

    public static boolean isValidUsername(final String username) {
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static boolean isValidPass(final String p) {
        Matcher matcher = password.matcher(p);
        return matcher.matches();
    }

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void ui(Runnable runnable) {
        handler.post(runnable);
    }

    public static File saveBitmap(Bitmap bitmap, String format) {
        File f = new File(getSafeDir(), "profile." + format);
        try (FileOutputStream out = new FileOutputStream(f)) {
            bitmap.compress(format.equalsIgnoreCase("png") ?
                    Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 90, out);
        } catch (IOException e) {
            f = null;
            e.printStackTrace();
        }
        return f;
    }

    public static void loadAvatar(Profile user, ImageView imageView) {
        if (user.getImage() == null)
            user.setImage(createBitmap(user.getAvatarName()));

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()
                && !user.getAvatar().endsWith("avatar.png")) {
            Glide.with(imageView)
                    .load(user.getAvatar())
                    .thumbnail(
                            Glide.with(imageView)
                                    .load((Bitmap) user.getImage())
                                    .circleCrop()
                    ).circleCrop()
                    .into(imageView);
        } else {
            Glide.with(imageView)
                    .load((Bitmap) user.getImage())
                    .circleCrop()
                    .into(imageView);
        }
    }

    public static void showKeyboard(EditText edt) {
        edt.requestFocus();
        InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void hideSoftKeyboard(View edt) {
        edt.clearFocus();
        InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    public static void animate(ViewGroup viewGroup) {
        viewGroup.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                viewGroup.getContext(), R.anim.layout_anim));
    }

    public static int brighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.3f;
        return Color.HSVToColor(hsv);
    }

    public static void getColor(Profile profile, Consumer<Integer> updateListener) {
        if (profile.getColor() != null) {
            updateListener.accept(profile.getColor());
            return;
        }

        if (profile.getAvatar() != null && !profile.getAvatar().isEmpty()
                && !profile.getAvatar().endsWith("avatar.png")) {

            Glide.with(Application.context)
                    .asBitmap()
                    .load(profile.getAvatar())
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource == null)
                                return false;
                            if (profile.getColor() == null)
                                new ColorThread(profile, resource, updateListener).start();
                            return false;
                        }
                    }).preload();
        } else {
            updateListener.accept(null);
        }
    }

    public static void getColor(Bitmap resource, Consumer<Integer> updateListener) {
        new ColorThread(null, resource, updateListener).start();
    }

    public interface Consumer<T> {
        void accept(T t);
    }

    private static class ColorThread extends Thread {

        private final Profile profile;
        private final Bitmap resource;
        private final Consumer<Integer> updateListener;

        private ColorThread(Profile profile, Bitmap resource, Consumer<Integer> updateListener) {
            this.profile = profile;
            this.resource = resource;
            this.updateListener = updateListener;
        }

        @Override
        public void run() {
            super.run();

            int w = resource.getWidth();
            int h = resource.getHeight();
            int red = 0, green = 0, blue = 0;
            int ignore = 0;

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int color = resource.getPixel(i, j);
                    if (Color.alpha(color) < 250)
                        ignore++;
                    else {
                        red += Color.red(color);
                        green += Color.green(color);
                        blue += Color.blue(color);
                    }
                }
            }

            int count = w * h - ignore;
            if (count != 0) {
                int out = Color.rgb(red / count, green / count, blue / count);
                if (profile != null)
                    profile.setColor(out);

                if (updateListener != null)
                    ui(() -> updateListener.accept(out));
            }
        }
    }

    public static Point getNavigationBarSize() {
        Point appUsableSize = getAppUsableScreenSize();
        Point realScreenSize = getRealScreenSize();

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize() {
        WindowManager windowManager = (WindowManager) Application.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize() {
        WindowManager windowManager = (WindowManager) Application.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }

    public static void addSpace(RecyclerView rv, int top, int bottom) {
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int index = parent.getChildViewHolder(view).getAdapterPosition();
                if (top != 0 && index == 0)
                    outRect.top = Utils.dp(top);
                else if (bottom != 0 && index == ((BaseAdapter<?>) parent.getAdapter()).size() - 1)
                    outRect.bottom = Utils.dp(bottom);
            }
        });
    }

    public static int getTextHeight(TextView tv) {
        StaticLayout sl = new StaticLayout(tv.getText(),
                tv.getPaint(),
                tv.getMeasuredWidth(),
                Layout.Alignment.ALIGN_NORMAL,
                tv.getLineSpacingMultiplier(),
                tv.getLineSpacingExtra(),
                true);
        return sl.getLineTop(sl.getLineCount()) + sl.getLineBottom(0);
    }

    private static final Random rnd = new SecureRandom();
    private static final char[] symbols = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789").toCharArray();

    public static String rnd() {
        StringBuilder builder = new StringBuilder(12);
        for (int idx = 0; idx < 12; ++idx)
            builder.append(symbols[rnd.nextInt(symbols.length)]);
        return builder.toString();
    }

    public static String getFileFormat(Uri uri) {
        try {
            String result = null;
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = Application.context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            result = result.toLowerCase();
            if (result.endsWith(".png"))
                return "png";
        } catch (Exception ignore) {
        }
        return "jpeg";
    }
}
