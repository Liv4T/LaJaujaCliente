package com.dybcatering.lajauja.Remote;



import com.dybcatering.lajauja.Model.DataMessage;
import com.dybcatering.lajauja.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA5EdvYqg:APA91bEbR730zAutteesjaRvYbjqOha1AUTt4RiyCh7rXYq4IxD9FJyKc218EO7y5CKngYKmLYgKJP2ZtMrQUpRS0I5r1VxgQlTwGtFjpHgw3s4oZNaAT5NUpH2Al5MfiXMmCHfhSN5F"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
