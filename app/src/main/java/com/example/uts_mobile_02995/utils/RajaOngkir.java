package com.example.uts_mobile_02995.utils;

import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataKeranjang;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RajaOngkir {

    public interface OnShippingCostCallback {
        void onShippingCostLoaded(JSONArray costs);
        void onError(String message);
    }

    public static void calculateShipping(int idAlamat, List<DataKeranjang> products, OnShippingCostCallback callback) {
        if (idAlamat <= 0 || products == null) {
            callback.onError("Data pengiriman tidak lengkap");
            return;
        }

        DataAPI api = ServerAPI.getApi();
        api.getCityIdByAlamat(idAlamat).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            String cityId = json.getJSONObject("data").getString("city_id");
                            getAllCouriersCost(cityId, products, callback);
                        } else {
                            callback.onError("Kota tidak ditemukan");
                        }
                    } else {
                        callback.onError("Respons tidak berhasil");
                    }
                } catch (Exception e) {
                    callback.onError("Kesalahan: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Kesalahan Jaringan");
            }
        });
    }

    private static void getAllCouriersCost(String destination, List<DataKeranjang> products, OnShippingCostCallback callback) {
        int totalWeight = 0;
        for (DataKeranjang produk : products) {
            int berat = produk.getBerat() > 0 ? produk.getBerat() : 1000;
            totalWeight += berat * produk.getJumlah();
        }
        totalWeight = Math.max(totalWeight, 1000);

        String[] couriers = {"jne", "tiki"};
        JSONArray allCosts = new JSONArray();

        for (String courier : couriers) {
            Map<String, String> params = new HashMap<>();
            params.put("courier", courier);
            params.put("destination", destination);
            params.put("weight", String.valueOf(totalWeight));

            DataAPI api = ServerAPI.getApi();
            api.checkOngkir(params).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject json = new JSONObject(response.body().string());

                            if (json.getInt("result") == 1) {
                                JSONArray costs = json.getJSONObject("data")
                                        .getJSONObject("rajaongkir")
                                        .getJSONArray("results")
                                        .getJSONObject(0)
                                        .getJSONArray("costs");

                                for (int i = 0; i < costs.length(); i++) {
                                    JSONObject cost = costs.getJSONObject(i);
                                    cost.put("courier", courier.toUpperCase());
                                    allCosts.put(cost);
                                }

                                if (allCosts.length() > 0) {
                                    callback.onShippingCostLoaded(allCosts);
                                }
                            }
                        }
                    } catch (Exception e) {
                        callback.onError("Gagal parsing biaya: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onError("Gagal mengambil ongkir: " + t.getMessage());
                }
            });
        }
    }
}