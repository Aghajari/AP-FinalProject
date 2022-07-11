package com.aghajari.bot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

class BotResponse {

    public String to;
    public String text;

    public static List<BotResponse> parse(String res){
        try {
            res = res.trim();

            if (res.startsWith("[")) {
                return new Gson().fromJson(res,
                        new TypeToken<List<BotResponse>>() {
                        }.getType());
            } else {
                ArrayList<BotResponse> list = new ArrayList<>(1);
                list.add(new Gson().fromJson(res, BotResponse.class));
                return list;
            }
        }catch (Exception ignore){}
        return new ArrayList<>(1);
    }
}
