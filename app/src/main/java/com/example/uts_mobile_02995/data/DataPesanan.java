package com.example.uts_mobile_02995.data;

import com.google.gson.annotations.SerializedName;

public class DataPesanan {
    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private String status;

    @SerializedName("dibuat_pada")
    private String tanggal;

    @SerializedName("alamat_lengkap")
    private String alamat;

    @SerializedName("total_produk")
    private String totalProduk;

    @SerializedName("total_bayar")
    private double totalBayar;

    @SerializedName("nama_penerima")
    private String namaPenerima;

    @SerializedName("no_hp")
    private String noHp;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getTotalProduk() { return totalProduk; }
    public void setTotalProduk(String totalProduk) { this.totalProduk = totalProduk; }

    public double getTotalBayar() { return totalBayar; }
    public void setTotalBayar(double totalBayar) { this.totalBayar = totalBayar; }

    public String getNamaPenerima() { return namaPenerima; }
    public void setNamaPenerima(String namaPenerima) { this.namaPenerima = namaPenerima; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }
}
