package com.example.uts_mobile_02995.data;

public class DataKeranjang {
    private int id;
    private int jumlah;
    private String nama_produk;
    private double harga;
    private String gambar;
    private int berat;
    private Integer stok;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public String getNama_produk() { return nama_produk; }
    public void setNama_produk(String nama_produk) { this.nama_produk = nama_produk; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public String getGambar() { return gambar; }
    public void setGambar(String gambar) { this.gambar = gambar; }

    public int getBerat() { return berat; }
    public void setBerat(int berat) { this.berat = berat; }

    public Integer getStok() { return stok; }
    public void setStok(Integer stok) { this.stok = stok; }

    public double getSubtotal() {
        return harga * jumlah;
    }
}
