package com.aghajari.api;

import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.UserModel;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiService {

    public static final String SOCKET_IP = "localhost";
    public static final int SOCKET_PORT = 2424;
    public static final String API_ADDRESS = "http://localhost:3154";

    static Api service;
    static Call<ResponseBody> call;

    public interface Callback {
        void onResponse(String body);

        void onError(boolean network, int code);

        default void onError2(boolean network, int code, String res) {
            onError(network, code);
        }
    }


    static void init() {
        if (call != null) {
            try {
                call.cancel();
            } catch (Exception ignore) {
            }
        }

        if (service == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .callTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(API_ADDRESS)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(Api.class);
        }
    }

    public static void login(String username, String pass, final Callback callback) {
        init();
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("password", pass);
        (call = service.login(toJson(map))).enqueue(new RetrofitCallback(callback));
    }

    public static void register(String name, String email, String pass, final Callback callback) {
        init();
        HashMap<String, Object> map = new HashMap<>();
        map.put("nickname", name);
        map.put("email", email);
        map.put("password", pass);
        (call = service.register(toJson(map))).enqueue(new RetrofitCallback(callback));
    }

    public static void search(String name, final Callback callback) {
        init();
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", name);
        (call = service.search(getToken(), toJson(map)))
                .enqueue(new RetrofitCallback(callback));
    }


    public static void changeUsername(String name, final Callback callback) {
        init();
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", name);
        (call = service.changeUsername(getToken(), toJson(map)))
                .enqueue(new RetrofitCallback(callback));
    }

    public static void changePass(String pass, String pass2, final Callback callback) {
        init();
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", MyInfo.getUsername(""));
        map.put("password", pass);
        map.put("newpassword", pass2);
        (call = service.changepass(getToken(), toJson(map)))
                .enqueue(new RetrofitCallback(callback));
    }

    public static void uploadAvatar(File file, final Callback callback) {
        init();
        System.out.println("Upload avatar");
        (call = service.uploadAvatar(getToken(), upload(file))).enqueue(new RetrofitCallback(callback));
    }

    private static MultipartBody.Part upload(File file) {
        String name = file.getName().toLowerCase();
        String extension = (name.endsWith(".jpeg") || name.endsWith(".png")
                || name.endsWith(".jpg")) ? name.substring(name.lastIndexOf(".") + 1) : "jpeg";
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/" + extension), file);
        return MultipartBody.Part.createFormData("image", file.getName(), requestFile);
    }

    public static void uploadImage(File file, final Callback callback) {
        init();

        String fn = (new Random().nextInt(90000000) + 10000000)
                + file.getName().substring(file.getName().lastIndexOf('.'));
        System.out.println(fn);

        (call = service.uploadImage(getToken(), fn, upload(file))).enqueue(new RetrofitCallback(callback));
    }

    static class RetrofitCallback implements retrofit2.Callback<ResponseBody> {
        final Callback callback;

        public RetrofitCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            System.out.println(response.code());
            if (response.isSuccessful()) {
                Platform.runLater(() -> {
                    try {
                        callback.onResponse(response.body().string());
                    } catch (IOException e) {
                        callback.onError(false, -1);
                    }
                });
            } else {
                Platform.runLater(() -> {
                    try {
                        callback.onError2(false, response.code(), response.errorBody().string());
                    } catch (IOException e) {
                        callback.onError(false, -1);
                    }
                });
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            System.out.println("NULL");
            Platform.runLater(() -> callback.onError(true, -1));

        }
    }

    private static String toJson(Map<String, Object> map) {
        return new Gson().toJson(map);
    }

    public static void saveUser(UserModel model) {
        Preferences prefs = Preferences.userRoot().node(ApiService.class.getName());
        prefs.put("id", model.getId());
        prefs.put("username", model.username);
        prefs.put("nickname", model.nickname);
        prefs.put("email", model.email);
        prefs.put("avatar", model.avatar);
    }

    public static void loadUser() {
        Preferences prefs = Preferences.userRoot().node(ApiService.class.getName());
        UserModel model = MyInfo.getInstance();
        model.id = prefs.get("id", model.getId());
        model.username = prefs.get("username", model.username);
        model.nickname = prefs.get("nickname", model.nickname);
        model.email = prefs.get("email", model.email);
        model.avatar = prefs.get("avatar", model.avatar);
    }

    public static void saveToken(String token) {
        Preferences prefs = Preferences.userRoot().node(ApiService.class.getName());
        prefs.put("token", token);
        prefs.putLong("time", System.currentTimeMillis() + (23 * 60 * 60 * 1000));
    }

    public static void savePassword(String pass) {
        Preferences prefs = Preferences.userRoot().node(ApiService.class.getName());
        prefs.put("password", pass);
    }

    public static boolean isLoggedIn() {
        Preferences prefs = Preferences.userRoot().node(ApiService.class.getName());
        return prefs.getLong("time", 0) > System.currentTimeMillis()
                && !MyInfo.getInstance().getId().isEmpty();
    }

    public static void logOut(){
        Preferences prefs = Preferences.userRoot().node(ApiService.class.getName());
        MyInfo.getInstance().id = "";
        MyInfo.getInstance()._id = "";
        prefs.remove("id");
        prefs.remove("username");
        prefs.remove("nickname");
        prefs.remove("token");
        prefs.remove("time");
        prefs.remove("email");
        prefs.remove("avatar");
        prefs.remove("password");
        SocketApi.closeSocket();
    }

    public static String getToken() {
        Preferences prefs = Preferences.userRoot().node(ApiService.class.getName());
        return prefs.get("token", "");
    }
}
