package com.aghajari.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.aghajari.api.ApiService;
import com.aghajari.axanimation.AXAnimation;
import com.aghajari.create_join_server;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.UploadAvatarModel;
import com.aghajari.shared.models.RequestToJoinServer;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.views.Utils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.InputStream;

public class ServersFragment extends BaseFragment<create_join_server> {

    Bitmap uploadedBitmap = null;
    String format;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (getActivity() == null || data == null)
                    return;
                format = Utils.getFileFormat(data.getData());
                try {
                    InputStream inputStream = getActivity().getContentResolver()
                            .openInputStream(data.getData());
                    Bitmap res = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    Glide.with(view)
                            .load(res)
                            .circleCrop()
                            .into(view.upload);
                    uploadedBitmap = res;
                } catch (Exception ignore) {
                }
            });

    @Override
    public create_join_server create(Context context) {
        return new create_join_server(context);
    }

    @Override
    public void bind() {
        ((ViewGroup.MarginLayoutParams) view.header.getLayoutParams())
                .topMargin = Utils.getStatusBarHeight();

        view.back.setOnClickListener(v -> back());

        view.invite_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (view.progress_join.isLoading())
                    return;

                String text = Utils.textOf(view.invite_code);
                boolean isVisible = view.progress_join.getScaleX() != 0;
                if (!text.isEmpty() && !isVisible) {
                    AXAnimation.create()
                            .firstValueFromView(false)
                            .duration(250)
                            .scale(0f, 1f)
                            .start(view.progress_join);
                } else if (text.isEmpty() && isVisible) {
                    AXAnimation.create()
                            .firstValueFromView(false)
                            .duration(250)
                            .scale(1f, 0f)
                            .start(view.progress_join);
                }
            }
        });

        view.server_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (view.progress_create.isLoading())
                    return;

                String text = Utils.textOf(view.server_name);
                boolean isVisible = view.progress_create.getScaleX() != 0;
                if (!text.isEmpty() && !isVisible) {
                    AXAnimation.create()
                            .firstValueFromView(false)
                            .duration(250)
                            .scale(0f, 1f)
                            .start(view.progress_create);

                } else if (text.isEmpty() && isVisible) {
                    AXAnimation.create()
                            .firstValueFromView(false)
                            .duration(250)
                            .scale(1f, 0f)
                            .start(view.progress_create);
                }
            }
        });

        view.upload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(Intent.createChooser(intent, "Select Profile"));
        });

        view.done_join.setOnClickListener(v -> {
            if (view.progress_join.isLoading()
                    || view.progress_create.isLoading()
                    || view.progress_join.getScaleX() != 1)
                return;
            view.progress_join.setLoading(true);

            String code = Utils.textOf(view.invite_code);
            if (code.length() <= 4) {
                Toast.makeText(v.getContext(), "Invalid code!", Toast.LENGTH_SHORT).show();
                return;
            }
            EasyApi.joinServer(code, model -> {
                view.progress_join.setLoading(false);
                RequestToJoinServer res = model.get();
                if (res.resultCode == 404) {
                    Toast.makeText(v.getContext(), "Invalid code!", Toast.LENGTH_SHORT).show();
                } else if (res.resultCode == 300) {
                    Toast.makeText(v.getContext(), "You are already a member of \n'" + res.serverModel.name + "' community!", Toast.LENGTH_SHORT).show();
                } else if (res.resultCode == 200) {
                    Toast.makeText(v.getContext(), "Welcome to " + res.serverModel.name, Toast.LENGTH_SHORT).show();
                    showFragment(ProfileFragment.newInstance(res.serverModel));
                }
            });
        });

        view.done_create.setOnClickListener(v -> {
            if (view.progress_join.isLoading()
                    || view.progress_create.isLoading()
                    || view.progress_create.getScaleX() != 1)
                return;
            view.progress_create.setLoading(true);

            String name = Utils.textOf(view.server_name);
            if (name.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter Server Name!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uploadedBitmap != null) {
                File file = Utils.saveBitmap(uploadedBitmap, format);
                if (file == null) {
                    Toast.makeText(v.getContext(), "Can't upload profile!", Toast.LENGTH_SHORT).show();
                    view.progress_create.setLoading(false);
                    return;
                }

                ApiService.uploadImage(file, new ApiService.Callback() {
                    @Override
                    public void onResponse(String body) {
                        if (getContext() == null)
                            return;

                        System.out.println(body);
                        UploadAvatarModel url = UploadAvatarModel.parseOnlyUrl(body);
                        if (url == null || !url.success) {
                            onError(false, 0);
                        } else {
                            createServerNow(url.url);
                        }
                    }

                    @Override
                    public void onError(boolean network, int code) {
                        if (getContext() == null)
                            return;
                        view.progress_create.setLoading(false);
                        Toast.makeText(getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError2(boolean network, int code, String res) {
                        if (getContext() == null)
                            return;
                        view.progress_create.setLoading(false);
                        BaseApiModel.toastError(res);
                    }
                });
            } else {
                createServerNow("");
            }
        });
    }

    private void createServerNow(String image) {
        if (Utils.textOf(view.server_name).isEmpty())
            return;

        EasyApi.createServer(Utils.textOf(view.server_name), image, model -> {
            if (getContext() == null)
                return;

            view.progress_create.setLoading(false);
            ServerModel server = model.get();
            if (server == null) {
                Toast.makeText(getContext(), "Oops, something went wrong :(", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Server created!", Toast.LENGTH_SHORT).show();
                showFragment(ProfileFragment.newInstance(server));
            }
        });
    }

}
