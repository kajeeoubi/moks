package com.example.uts_mobile_02995.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerAPI {
    public static final String BASE_URL = "https://webdomainku.my.id/";
    public static final String BASE_URL_IMAGE = BASE_URL + "gambar/";
    public static final String BASE_URL_AVATAR = BASE_URL + "avatar/";
    public static final String BASE_URL_BUKTI_BAYAR = BASE_URL + "bukti_bayar/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static DataAPI getApi() {
        return getClient().create(DataAPI.class);
    }
}