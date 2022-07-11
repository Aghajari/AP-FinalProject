package com.aghajari.models;

import com.aghajari.util.Toast;
import com.google.gson.Gson;

public class BaseApiModel {

    public boolean success;
    public String error;

    public static void toastError(String res){
        try {
            System.out.println(res);
            BaseApiModel model = new Gson().fromJson(res, BaseApiModel.class);
            if (model.error != null)
                Toast.makeText(model.error);
        }catch (Exception ignore){
            Toast.makeText("Oops, something went wrong!");
        }
    }
}
