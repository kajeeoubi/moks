package com.example.uts_mobile_02995.data;

public class DataAlamatPengiriman {
    private int id;
    private String nama_penerima;
    private String alamat_lengkap;
    private String kota;
    private String provinsi;
    private String kode_pos;
    private String no_hp;
    private int alamat_utama;

    // Getter & Setter
    public int getId() { return id; }
    public String getNama_penerima() { return nama_penerima; }
    public String getAlamat_lengkap() { return alamat_lengkap; }
    public String getKota() { return kota; }
    public String getProvinsi() { return provinsi; }
    public String getKode_pos() { return kode_pos; }
    public String getNo_hp() { return no_hp; }
    public int getAlamat_utama() { return alamat_utama; }

    // setter dan konstruktor
    public void setId(int id) { this.id = id; }
    public void setNama_penerima(String nama_penerima) { this.nama_penerima = nama_penerima; }
    public void setAlamat_lengkap(String alamat_lengkap) { this.alamat_lengkap = alamat_lengkap; }
    public void setKota(String kota) { this.kota = kota; }
    public void setProvinsi(String provinsi) { this.provinsi = provinsi; }
    public void setKode_pos(String kode_pos) { this.kode_pos = kode_pos; }
    public void setNo_hp(String no_hp) { this.no_hp = no_hp; }
    public void setAlamat_utama(int alamat_utama) { this.alamat_utama = alamat_utama; }

    public DataAlamatPengiriman(int id, String nama_penerima, String alamat_lengkap, String kota, String provinsi, String kode_pos, String no_hp, int alamat_utama) {
        this.id = id;
        this.nama_penerima = nama_penerima;
        this.alamat_lengkap = alamat_lengkap;
        this.kota = kota;
        this.provinsi = provinsi;
        this.kode_pos = kode_pos;
        this.no_hp = no_hp;
        this.alamat_utama = alamat_utama;
    }
}
