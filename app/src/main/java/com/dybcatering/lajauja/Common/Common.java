package com.dybcatering.lajauja.Common;

import com.dybcatering.lajauja.Model.User;

public class Common {
    public static User currentUser;

    public static String convertCodeToStatus(String key) {
        if (key.equals("0"))
            return "Recibido";
        else if (key.equals("1"))
            return "En Camino";
        else
            return "Entregado !";

    }
}
