package com.dybcatering.lajauja.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dybcatering.lajauja.Model.User;
import com.dybcatering.lajauja.Remote.APIService;
import com.dybcatering.lajauja.Remote.RetrofitClient;

public class Common {
    public static User currentUser;

    public static final String DELETE = "Eliminar";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String key) {
        if (key.equals("0"))
            return "Recibido";
        else if (key.equals("1"))
            return "En Camino";
        else
            return "Entregado !";

    }

    public static boolean IsConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info !=null){
                for (int i=0; i<info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
