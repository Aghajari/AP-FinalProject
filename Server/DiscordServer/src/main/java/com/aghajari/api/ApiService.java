package com.aghajari.api;

import com.aghajari.database.Connection;
import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.UserModel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiService {

    public static final int SOCKET_PORT = 2424;
    public static final String API_ADDRESS = "http://localhost:3154";

    static Api service;

    static Cache<String, UserModel> users = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public interface Callback {
        void onResponse(String body);

        void onError(boolean network, int code);

        default void onError2(boolean network, int code, String res) {
            onError(network, code);
        }
    }

    static void init() {
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

    public static void auth(String token, final Callback callback) {
        init();
        service.auth(token).enqueue(new RetrofitCallback(callback));
    }

    public static Response<ResponseBody> findInfo(String token, Collection<String> ids) throws IOException {
        init();
        return service.findInfo(token, new Gson().toJson(ids)).execute();
    }

    public static void findInfoAsync(String token, Collection<String> ids, final Callback callback) {
        init();
        service.findInfo(token, new Gson().toJson(ids)).enqueue(new RetrofitCallback(callback));
    }

    public static void bot(String link, String json, final Callback callback) {
        init();
        service.bot(link, json).enqueue(new RetrofitCallback(callback));
    }

    public static void findFromAndTo(String token, String from, String to,
                                     FromToResponse callback) {
        HashMap<String, UserModel> data = new HashMap<>();
        ArrayList<String> ids = new ArrayList<>(2);

        UserModel m = users.asMap().get(from);
        if (m != null)
            data.put(from, m);
        else
            ids.add(from);

        if (to != null) {
            m = users.asMap().get(to);
            if (m != null)
                data.put(to, m);
            else
                ids.add(to);
        }

        if (!ids.isEmpty()) {
            findInfoAsync(token, ids, new Callback() {
                @Override
                public void onResponse(String body) {
                    List<UserModel> list = new Gson().fromJson(body,
                            new TypeToken<List<UserModel>>() {
                            }.getType());
                    for (UserModel model : list) {
                        if (model == null)
                            continue;

                        users.put(model.getId(), model);
                        data.put(model.getId(), model);
                    }
                    callback.onNext(data);
                }

                @Override
                public void onError(boolean network, int code) {
                    callback.onNext(data);
                }
            });
        } else {
            callback.onNext(data);
        }
    }

    public interface FromToResponse {
        void onNext(Map<String, UserModel> model);
    }

    public static void fixUsers(String token, String clientId, List<? extends IDFinder> finders) throws IOException {
        if (finders.isEmpty())
            return;

        HashMap<String, IDFinder> ids = new HashMap<>(finders.size());
        for (IDFinder finder : finders) {
            UserModel model = users.asMap().get(finder.getUserId(clientId));
            if (model != null) {
                if (Connection.getBlocked().hasBlocked(model.getId(), clientId))
                    finder.setUser(copy(model));
                else
                    finder.setUser(model);
            } else {
                ids.put(finder.getUserId(clientId), finder);
            }
        }

        if (ids.isEmpty())
            return;

        Response<ResponseBody> body = findInfo(token, ids.keySet());
        if (body.isSuccessful()) {
            try {
                String str = body.body().string();
                //System.out.println(str);
                List<UserModel> list = new Gson().fromJson(str,
                        new TypeToken<List<UserModel>>() {
                        }.getType());
                for (UserModel model : list) {
                    if (model == null)
                        continue;

                    users.put(model.getId(), model);
                    if (Connection.getBlocked().hasBlocked(model.getId(), clientId))
                        ids.get(model.getId()).setUser(copy(model));
                    else
                        ids.get(model.getId()).setUser(model);
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    public static void fixAllUsers(String token, String clientId, List<? extends IDFinder> finders) throws IOException {
        if (finders.isEmpty())
            return;

        HashMap<String, ArrayList<IDFinder>> ids = new HashMap<>(finders.size());
        for (IDFinder finder : finders) {
            UserModel model = users.asMap().get(finder.getUserId(clientId));
            if (model != null) {
                if (Connection.getBlocked().hasBlocked(model.getId(), clientId))
                    finder.setUser(copy(model));
                else
                    finder.setUser(model);
            } else {
                ArrayList<IDFinder> list = ids.computeIfAbsent(finder.getUserId(clientId),
                        k -> new ArrayList<>());
                list.add(finder);
            }
        }

        if (ids.isEmpty())
            return;

        Response<ResponseBody> body = findInfo(token, ids.keySet());
        if (body.isSuccessful()) {
            try {
                String str = body.body().string();
                //System.out.println(str);
                List<UserModel> list = new Gson().fromJson(str,
                        new TypeToken<List<UserModel>>() {
                        }.getType());
                for (UserModel model : list) {
                    if (model == null)
                        continue;

                    users.put(model.getId(), model);
                    UserModel out;
                    if (Connection.getBlocked().hasBlocked(model.getId(), clientId))
                        out = copy(model);
                    else
                        out = model;

                    for (IDFinder finder : ids.get(model.getId())) {
                        finder.setUser(out);
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    public static UserModel getData(String token, String id) throws IOException {
        try {
            return users.get(id, () -> getDataNow(token, id));
        } catch (ExecutionException e) {
            return getDataNow(token, id);
        }
    }

    private static UserModel getDataNow(String token, String id) throws IOException {
        Response<ResponseBody> body = findInfo(token, Collections.singleton(id));
        if (body.isSuccessful()) {
            List<UserModel> list = new Gson().fromJson(body.body().string(),
                    new TypeToken<List<UserModel>>() {
                    }.getType());
            if (list.size() != 0)
                return list.get(0);
        }
        return null;
    }

    static class RetrofitCallback implements retrofit2.Callback<ResponseBody> {
        final Callback callback;

        public RetrofitCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                try {
                    callback.onResponse(response.body().string());
                } catch (IOException e) {
                    callback.onError(false, -1);
                }
            } else {
                try {
                    callback.onError2(false, response.code(), response.errorBody().string());
                } catch (IOException e) {
                    callback.onError(false, -1);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            callback.onError(true, -1);
        }
    }

    public static UserModel copy(UserModel model) {
        UserModel out = new UserModel();
        out.id = model.id;
        out._id = model._id;
        out.nickname = model.nickname;
        out.username = model.username;
        out.avatar = "";
        out.email = model.email;
        return out;
    }

    public static UserModel fullCopy(UserModel model) {
        UserModel out = new UserModel();
        out.id = model.id;
        out._id = model._id;
        out.nickname = model.nickname;
        out.username = model.username;
        out.avatar = model.avatar;
        out.email = model.email;
        return out;
    }
}
