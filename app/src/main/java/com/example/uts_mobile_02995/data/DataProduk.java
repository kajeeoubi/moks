package com.example.uts_mobile_02995.data;

import java.io.Serializable;

public class DataProduk implements Serializable {

    private int id;
    private String nama_produk;
    private String nama_kategori;
    private String deskripsi;
    private double harga;
    private int stok;
    private String gambar;
    private int viewer;
    private int berat;

    // Getter & Setter
    public int getId_produk() { return id;}

    public String getNama_produk() { return nama_produk;}
    public String getNama_kategori() { return nama_kategori;}

    public String getDeskripsi() { return deskripsi;}

    public double getHarga() { return harga;}

    public int getStok() { return stok;}

    public String getGambar() { return gambar;}

    public int getViewer() { return viewer;}
    public void setViewer(int viewer) { this.viewer = viewer;}

    public int getBerat() { return berat; }
}
