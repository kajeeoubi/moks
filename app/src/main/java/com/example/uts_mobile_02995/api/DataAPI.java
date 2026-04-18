package com.example.uts_mobile_02995.api;

import com.example.uts_mobile_02995.data.DataProduk;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DataAPI {

    // register
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> register(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password
    );

    // login
    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // produk
    @GET("produk.php")
    Call<List<DataProduk>> getProduk();

    // profile
    @GET("ambil_profile.php")
    Call<ResponseBody> getProfile(
            @Query("email") String email
    );

    // edit profile
    @FormUrlEncoded
    @POST("perbarui_profile.php")
    Call<ResponseBody> updateProfile(
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("kota") String kota,
            @Field("provinsi") String provinsi,
            @Field("no_hp") String no_hp,
            @Field("kode_pos") String kode_pos,
            @Field("email") String email
    );

    // perbarui viewer
    @FormUrlEncoded
    @POST("perbarui_viewer.php")
    Call<ResponseBody> updateViewer(
            @Field("id") int id
    );

    // produk populer
    @GET("produk_populer.php")
    Call<List<DataProduk>> getProdukPopuler();

    // perbarui avatar
    @Multipart
    @POST("perbarui_avatar.php")
    Call<ResponseBody> updateAvatar(
            @Part("email") RequestBody email,
            @Part MultipartBody.Part avatar
    );

    // check ongkir
    @FormUrlEncoded
    @POST("cek_ongkir.php")
    Call<ResponseBody> checkOngkir(
            @FieldMap Map<String, String> params);

    // checkout pesanan
    @FormUrlEncoded
    @POST("checkout.php")
    Call<ResponseBody> checkout(
            @Field("id_pengguna") int idPengguna,
            @Field("id_alamat") int idAlamat,
            @Field("kurir") String kurir,
            @Field("layanan_kurir") String layananKurir,
            @Field("biaya_ongkir") double biayaOngkir,            
            @Field("total_harga_produk") double totalHargaProduk,
            @Field("total_bayar") double totalBayar,
            @Field("metode_bayar") String metodeBayar,
            @Field("produk") String produkJson
    );

    // upload bukti bayar
    @Multipart
    @POST("upload_bukti_bayar.php")
    Call<ResponseBody> uploadBuktiBayar(
            @Part("id_pesanan") RequestBody idPesanan,
            @Part MultipartBody.Part buktiBayar
    );

    // perbarui sandi
    @FormUrlEncoded
    @POST("perbarui_sandi.php")
    Call<ResponseBody> updatePassword(
            @Field("email") String email,
            @Field("sandi_lama") String oldPassword,
            @Field("sandi_baru") String newPassword
    );

    // riwayat pesanan
    @GET("riwayat_pesanan.php")
    Call<ResponseBody> getRiwayatPesanan(
            @Query("id_pengguna") int idPengguna
    );

    // detail pesanan
    @GET("detail_pesanan.php")
    Call<ResponseBody> getDetailPesanan(
            @Query("id_pesanan") String idPesanan
    );

    // ambil alamat pengiriman 
    @GET("ambil_alamat_pengiriman.php")
    Call<ResponseBody> getAlamatPengiriman(
        @Query("id_pengguna") int idPengguna
    );

    // tambah alamat pengiriman
    @FormUrlEncoded
    @POST("tambah_alamat_pengiriman.php")
    Call<ResponseBody> tambahAlamatPengiriman(
        @Field("id_pengguna") int idPengguna,
        @Field("nama_penerima") String namaPenerima,
        @Field("alamat_lengkap") String alamatLengkap,
        @Field("kota") String kota,
        @Field("provinsi") String provinsi,
        @Field("kode_pos") String kodePos,
        @Field("no_hp") String noHp,
        @Field("alamat_utama") int alamatUtama
    );

    // ambil alamat dari RajaOngkir
    @GET("ambil_provinsi_kota.php")
    Call<ResponseBody> getProvinsiKota(
        @QueryMap Map<String, String> params
    );

    // cari id kota dari alamat_pengiriman
    @GET("ambil_id_kota.php")
    Call<ResponseBody> getCityIdByAlamat(
        @Query("id_alamat") int idAlamat
    );

    // perbarui alamat utama
    @FormUrlEncoded
    @POST("perbarui_alamat_utama.php")
    Call<ResponseBody> setAlamatUtama(
        @Field("id_alamat") int idAlamat
    );

    // hapus alamat pengiriman
    @FormUrlEncoded
    @POST("hapus_alamat_pengiriman.php")
    Call<ResponseBody> hapusAlamatPengiriman(
        @Field("id_alamat") int idAlamat
    );

    // update alamat pengiriman
    @FormUrlEncoded
    @POST("perbarui_alamat_pengiriman.php")
    Call<ResponseBody> updateAlamatPengiriman(
        @Field("id_alamat") int idAlamat,
        @Field("id_pengguna") int idPengguna,
        @Field("nama_penerima") String namaPenerima,
        @Field("alamat_lengkap") String alamatLengkap,
        @Field("kota") String kota,
        @Field("provinsi") String provinsi,
        @Field("kode_pos") String kodePos,
        @Field("no_hp") String noHp,
        @Field("alamat_utama") int alamatUtama
    );
}