package com.example.uts_mobile_02995.data;

public class DataPengguna {
    private int id;
    private String nama;
    private String alamat;
    private String kota;
    private String provinsi;
    private String no_hp;
    private String kode_pos;
    private String email;

    // Constructor
    public DataPengguna(String nama, String alamat, String kota, String provinsi, String no_hp, String kode_pos, String email) {
        this.nama = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.provinsi = provinsi;
        this.no_hp = no_hp;
        this.kode_pos = kode_pos;
        this.email = email;
    }

    // Getter dan Setter
    public int getId() { return id;}
    public void setId(int id) { this.id = id;}

    public String getNama() { return nama;}
    public void setNama(String nama) { this.nama = nama;}

    public String getAlamat() {return alamat;}
    public void setAlamat(String alamat) { this.alamat = alamat;}

    public String getKota() { return kota;}
    public void setKota(String kota) { this.kota = kota;}

    public String getProvinsi() { return provinsi;}
    public void setProvinsi(String provinsi) { this.provinsi = provinsi;}

    public String getNo_hp() { return no_hp;}
    public void setNo_hp(String no_hp) { this.no_hp = no_hp;}

    public String getKode_pos() { return kode_pos;}
    public void setKode_pos(String kode_pos) { this.kode_pos = kode_pos;}

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email;}
}