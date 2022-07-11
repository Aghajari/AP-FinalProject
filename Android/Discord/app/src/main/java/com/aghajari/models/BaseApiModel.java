package com.aghajari.models;

import android.widget.Toast;

import com.aghajari.Application;
import com.google.gson.Gson;

public class BaseApiModel {

    public boolean success;
    public String error;

    public static void toastError(String res) {
        try {
            System.out.println(res);
            BaseApiModel model = new Gson().fromJson(res, BaseApiModel.class);
            if (model.error != null)
                Toast.makeText(Application.context, model.error, Toast.LENGTH_SHORT).show();
        } catch (Exception ignore) {
            Toast.makeText(Application.context, "Oops, something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
