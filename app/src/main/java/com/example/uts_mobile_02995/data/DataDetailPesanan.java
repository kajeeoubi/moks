package com.example.uts_mobile_02995.data;

import java.util.List;

public class DataDetailPesanan {
    private String id_pesanan;
    private String status_pesanan;
    private String tanggal_pesanan;
    private String nama_penerima;
    private String no_hp_penerima;
    private String alamat_lengkap_penerima;
    private String kurir;
    private String layanan_kurir;
    private double biaya_ongkir;
    private double total_harga_produk; 
    private double total_bayar;
    private String metode_bayar;
    private String bukti_bayar;
    private List<DetailProduk> daftar_produk;

    public static class DetailProduk {
        private String nama_produk;
        private int jumlah;
        private double harga_satuan;
        private double subtotal;

        // Getters and setters
        public String getNamaProduk() { return nama_produk; }
        public int getJumlah() { return jumlah; }
        public double getHargaSatuan() { return harga_satuan; }
        public double getSubtotal() { return subtotal; }
    }

    // Getters
    public String getIdPesanan() { return id_pesanan; }
    public String getStatusPesanan() { return status_pesanan; }
    public String getTanggalPesanan() { return tanggal_pesanan; }
    public String getNamaPenerima() { return nama_penerima; }
    public String getNoHpPenerima() { return no_hp_penerima; }
    public String getAlamatLengkapPenerima() { return alamat_lengkap_penerima; }
    public String getKurir() { return kurir; }
    public String getLayananKurir() { return layanan_kurir; }
    public double getBiayaOngkir() { return biaya_ongkir; }
    public double getTotalHargaProduk() { return total_harga_produk; }
    public double getTotalBayar() { return total_bayar; }
    public String getMetodeBayar() { return metode_bayar; }
    public String getBuktiBayar() { return bukti_bayar; }
    public List<DetailProduk> getDaftarProduk() { return daftar_produk; }
}
