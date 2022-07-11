package com.aghajari;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.axanimation.AXAnimation;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.LoginModel;
import com.aghajari.models.MyInfo;
import com.aghajari.models.UploadAvatarModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.Utils;
import com.aghajari.xmlbypass.XmlByPass;
import com.aghajari.xmlbypass.XmlByPassAttr;
import com.aghajari.xmlbypass.XmlLayout;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.InputStream;

@XmlByPass(layouts = {
        @XmlLayout(layout = "*"),
}/*, packageName = "com.aghajari"*/)
@XmlByPassAttr(name = "textColorHint", format = "color", codeName = "hintTextColor")
@XmlByPassAttr(name = "nextFocusDown", format = "reference_id", codeName = "nextFocusDownId")
public class MainActivity extends AppCompatActivity {

    activity_main view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(view = new activity_main(this));
        view.progress.setIndicatorColor(Color.WHITE);

        ApiService.loadUser();

        Glide.with(this)
                .load(Uri.parse("file:///android_asset/space.gif"))
                .centerCrop()
                .into(view.background);

        view.postDelayed(() -> {
            if (ApiService.isLoggedIn())
                logIn();
            else
                start();
        }, 2000);
    }

    public void logIn() {
        if (!SocketApi.getInstance().isConnected()) {
            Toast.makeText(this, "Server isn't running!", Toast.LENGTH_SHORT).show();
            return;
        }

        EasyApi.auth(model -> {
            if (model.get()) {
                startNow();
            }
        });
    }

    Bitmap uploadedBitmap = null;
    String format;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data == null)
                    return;
                format = Utils.getFileFormat(data.getData());

                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap res = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    Glide.with(view)
                            .load(res)
                            .circleCrop()
                            .into(view.profile_img);
                    uploadedBitmap = res;
                } catch (Exception ignore) {
                }
            });

    public void start() {
        profilePicker();

        AXAnimation.create()
                .duration(300)
                .scale(0f)
                .start(view.loading);

        AXAnimation.create()
                .duration(300)
                .firstValueFromView(false)
                .visibility(View.VISIBLE)
                .scale(0f, 1f)
                .start(view.sign);

        view.sign_in_text.setOnClickListener(v -> {
            view.sign_in_text.setTextColor(getResources().getColor(R.color.color2));
            view.sign_up_text.setTextColor(Color.WHITE);

            AXAnimation.create()
                    .duration(250)
                    .translationX(0f)
                    .start(view.selector);

            AXAnimation.create()
                    .duration(250).dp()
                    .translationX(0f)
                    .start(view.sign_in_content);

            AXAnimation.create()
                    .duration(250).dp()
                    .translationX(350f)
                    .start(view.sign_up_content);
        });

        view.login.setOnClickListener(v -> {
            String name = Utils.textOf(view.login_name);
            String pass = Utils.textOf(view.login_pass);

            if (name.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter your username or email!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter your password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.length() < 5) {
                Toast.makeText(v.getContext(), "Username must be at least 5 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.length() < 6) {
                Toast.makeText(v.getContext(), "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            view.login_btn.setLoading(true);

            ApiService.savePassword(pass);

            ApiService.login(name, pass, new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    System.out.println(body);
                    LoginModel model = LoginModel.parse(body);
                    if (model.success) {
                        EasyApi.auth(m -> {
                            if (m.get())
                                startNow();
                        });
                    } else {
                        Toast.makeText(v.getContext(), model.error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(boolean network, int code) {
                    view.login_btn.setLoading(false);
                    Toast.makeText(v.getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError2(boolean network, int code, String res) {
                    view.login_btn.setLoading(false);
                    BaseApiModel.toastError(res);
                }
            });
        });

        view.sign_up_text.setOnClickListener(v -> {
            view.sign_up_text.setTextColor(getResources().getColor(R.color.color2));
            view.sign_in_text.setTextColor(Color.WHITE);

            AXAnimation.create()
                    .duration(250).dp()
                    .translationX(175f)
                    .start(view.selector);

            AXAnimation.create()
                    .duration(250).dp()
                    .translationX(-350f)
                    .start(view.sign_in_content);

            AXAnimation.create()
                    .duration(250).dp()
                    .translationX(0f)
                    .start(view.sign_up_content);
        });

        view.signup.setOnClickListener(v -> {
            String email = Utils.textOf(view.signup_email);
            String pass = Utils.textOf(view.signup_pass);
            String name = Utils.textOf(view.signup_name);

            if (email.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter your password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter your name!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.length() < 5) {
                Toast.makeText(v.getContext(), "Email must be at least 5 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.length() < 2) {
                Toast.makeText(v.getContext(), "Name must be at least 2 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 8) {
                Toast.makeText(v.getContext(), "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidPass(pass)) {
                Toast.makeText(v.getContext(), "Enter an stronger password!", Toast.LENGTH_SHORT).show();
                return;
            }
            ApiService.savePassword(pass);
            view.signup_btn.setLoading(true);

            ApiService.register(name, email, pass, new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    view.signup_btn.setLoading(false);
                    System.out.println(body);
                    LoginModel model = LoginModel.parse(body);
                    if (model.success) {
                        AXAnimation.create()
                                .duration(250).dp()
                                .translationX(-350f)
                                .start(view.main_content);

                        AXAnimation.create()
                                .duration(250).dp()
                                .translationX(0f)
                                .start(view.username_content);
                    } else {
                        Toast.makeText(v.getContext(), model.error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(boolean network, int code) {
                    view.signup_btn.setLoading(false);
                    Toast.makeText(v.getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError2(boolean network, int code, String res) {
                    view.signup_btn.setLoading(false);
                    BaseApiModel.toastError(res);
                }
            });

        });

        Runnable switchToProfile = () -> {
            Glide.with(MainActivity.this)
                    .load(Utils.createBitmap(MyInfo.getInstance().nickname))
                    .circleCrop()
                    .into(view.profile_img);

            AXAnimation.create()
                    .duration(250).dp()
                    .translationX(-350f)
                    .start(view.username_content);

            AXAnimation.create()
                    .duration(250).dp()
                    .translationX(0f)
                    .start(view.profile_content);
        };

        view.skip_username.setOnClickListener(v -> switchToProfile.run());
        view.next_username.setOnClickListener(v -> {
            String username = Utils.textOf(view.username_edt);
            if (!Utils.isValidUsername(username)) {
                Toast.makeText(v.getContext(), "Username isn't valid!", Toast.LENGTH_SHORT).show();
                return;
            }

            view.next_username_btn.setLoading(true);
            ApiService.changeUsername(username, new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    MyInfo.getInstance().username = username;
                    view.next_username_btn.setLoading(false);
                    System.out.println(body);
                    switchToProfile.run();
                }

                @Override
                public void onError(boolean network, int code) {
                    view.next_username_btn.setLoading(false);
                    Toast.makeText(v.getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError2(boolean network, int code, String res) {
                    view.next_username_btn.setLoading(false);
                    BaseApiModel.toastError(res);
                }
            });
        });

        view.next_profile.setOnClickListener(v -> {
            view.next_profile_btn.setLoading(true);

            if (uploadedBitmap != null) {
                File file = Utils.saveBitmap(uploadedBitmap, format);
                if (file == null) {
                    Toast.makeText(v.getContext(), "Can't upload profile!", Toast.LENGTH_SHORT).show();
                    view.next_profile_btn.setLoading(false);
                    return;
                }

                ApiService.uploadAvatar(file, new ApiService.Callback() {
                    @Override
                    public void onResponse(String body) {
                        System.out.println(body);
                        UploadAvatarModel.parse(body);
                        EasyApi.auth(model -> {
                            Utils.ui(() -> view.next_profile_btn.setLoading(false));
                            if (model.get()) {
                                startNow();
                            }
                        });
                    }

                    @Override
                    public void onError(boolean network, int code) {
                        view.next_profile_btn.setLoading(false);
                        Toast.makeText(v.getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError2(boolean network, int code, String res) {
                        view.next_profile_btn.setLoading(false);
                        BaseApiModel.toastError(res);
                    }
                });
            } else {
                EasyApi.auth(model -> {
                    Utils.ui(() -> view.next_profile_btn.setLoading(false));
                    if (model.get()) {
                        startNow();
                    }
                });
            }
        });
    }

    private void profilePicker() {
        uploadedBitmap = null;
        view.profile_img.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(Intent.createChooser(intent, "Select Profile"));
        });
    }

    private void startNow() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}