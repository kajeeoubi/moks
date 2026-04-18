package com.example.uts_mobile_02995.ui.checkout;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataKeranjang;
import com.example.uts_mobile_02995.utils.RajaOngkir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataCheckout {

    public interface OnAlamatPengirimanLoadedListener {
        void onAlamatPengirimanLoaded(int idAlamat, String namaPenerima, String noHp, String alamat, String userCity, String userProvince, String kodePos);
        void onAlamatPengirimanLoadFailed(String message);
    }

    public interface OnCartItemsLoadedListener {
        void onCartItemsLoaded(List<DataKeranjang> cartItems, double subtotal);
        void onCartItemsLoadFailed(String message);
    }

    public interface OnShippingOptionsLoadedListener {
        void onShippingOptionsLoaded(JSONArray costs);
        void onShippingOptionsLoadFailed(String message);
    }

    public static void loadAlamatPengiriman(Context context, int idPengguna, OnAlamatPengirimanLoadedListener listener) {
        DataAPI api = ServerAPI.getApi();
        api.getAlamatPengiriman(idPengguna).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            JSONArray dataArr = json.getJSONArray("data");
                            JSONObject alamat = null;
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject obj = dataArr.getJSONObject(i);
                                if (obj.has("alamat_utama") && obj.optInt("alamat_utama", 0) == 1) {
                                    alamat = obj;
                                    break;
                                }
                            }
                            if (alamat == null && dataArr.length() > 0) {
                                alamat = dataArr.getJSONObject(0);
                            }
                            if (alamat != null) {
                                int idAlamat = alamat.optInt("id");
                                String namaPenerima = alamat.optString("nama_penerima");
                                String noHp = alamat.optString("no_hp");
                                String alamatLengkap = alamat.optString("alamat_lengkap");
                                String kota = alamat.optString("kota");
                                String provinsi = alamat.optString("provinsi");
                                String kodePos = alamat.optString("kode_pos");
                                listener.onAlamatPengirimanLoaded(idAlamat, namaPenerima, noHp, alamatLengkap, kota, provinsi, kodePos);
                            } else {
                                listener.onAlamatPengirimanLoadFailed("Belum ada alamat pengiriman.");
                            }
                        } else {
                            listener.onAlamatPengirimanLoadFailed("Belum ada alamat pengiriman.");
                        }
                    } else {
                        listener.onAlamatPengirimanLoadFailed("Gagal memuat alamat pengiriman.");
                    }
                } catch (Exception e) {
                    listener.onAlamatPengirimanLoadFailed("Error parsing data alamat: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                listener.onAlamatPengirimanLoadFailed("Gagal memuat alamat: " + t.getMessage());
            }
        });
    }

    public static void loadCartItems(Context context, OnCartItemsLoadedListener listener) {
        // Ambil dari SharedPreferences lokal
        SharedPreferences pref = context.getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
        String keranjangJson = pref.getString("keranjang", "[]");
        try {
            List<DataKeranjang> checkoutList = new ArrayList<>();
            JSONArray array = new JSONArray(keranjangJson);
            double subtotalPesanan = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                DataKeranjang data = new DataKeranjang();
                data.setId(item.optInt("id_produk", 0));
                data.setNama_produk(item.optString("nama_produk", ""));
                data.setHarga(item.optDouble("harga", 0));
                data.setJumlah(item.optInt("jumlah", 1));
                data.setGambar(item.optString("gambar", ""));
                data.setBerat(item.optInt("berat", 1000));
                checkoutList.add(data);
                subtotalPesanan += data.getSubtotal();
            }
            listener.onCartItemsLoaded(checkoutList, subtotalPesanan);
        } catch (JSONException e) {
            listener.onCartItemsLoadFailed("Gagal parsing data keranjang");
        }
    }

    public static void calculateAndLoadShipping(Context context, int idAlamat, List<DataKeranjang> checkoutList, OnShippingOptionsLoadedListener listener) {
        if (idAlamat <= 0 || checkoutList.isEmpty()) {
            listener.onShippingOptionsLoadFailed("Data alamat atau keranjang kosong untuk perhitungan ongkir.");
            return;
        }

        RajaOngkir.calculateShipping(idAlamat, checkoutList,
                new RajaOngkir.OnShippingCostCallback() {
                    @Override
                    public void onShippingCostLoaded(JSONArray costs) {
                        listener.onShippingOptionsLoaded(costs);
                    }

                    @Override
                    public void onError(String message) {
                        listener.onShippingOptionsLoadFailed(message);
                    }
                });
    }
}