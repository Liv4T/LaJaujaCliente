package com.dybcatering.lajauja.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dybcatering.lajauja.Model.User;
import com.dybcatering.lajauja.Remote.APIService;
import com.dybcatering.lajauja.Remote.IGeoCoordinates;
import com.dybcatering.lajauja.Remote.IGoogleService;
import com.dybcatering.lajauja.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {

        public static String topicName = "News";
        public static String currentKey;

    public static User currentUser;

    public static final String DELETE = "Eliminar";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static final String EMAIL = "Email";
    public static final String DIRECTION = "MyDirection";
    public static final String NAME = "Name";
    public static final String LAST_NAME = "LastName";
    public static final String DATE = "Date";
    public static final String DOCUMENT = "Document";

    public static  String restaurantSelected ="";

    public static String PHONE_TEXT = "userPhone";

    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static final String maps = "https://maps.googleapis.com";

    public static IGoogleService getGoogleMapsApi(){
        return RetrofitClient.getClient(maps).create(IGoogleService.class);
    }

    public static final String INTENT_FOOD_ID = "FoodId";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String code){
        if (code.equals("0")) {
            return "Recibido";
        }else if (code.equals("1")){
            return "En camino";
        }else if (code.equals("2")){
            return "Enviando";
        } else
            return "Entregado";
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

    public static  String getDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm"
                ,calendar)
                .toString());

        return date.toString();
    }

    public static final String baseUrl = "https://maps.googleapis.com";


    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

}
