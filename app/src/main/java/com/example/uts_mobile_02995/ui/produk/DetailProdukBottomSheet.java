package com.example.uts_mobile_02995.ui.produk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataProduk;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProdukBottomSheet extends BottomSheetDialogFragment {

    private DataProduk produk;
    private final String BASE_URL_IMAGE = new ServerAPI().BASE_URL_IMAGE;

    public static DetailProdukBottomSheet newInstance(String produkJson) {
        DetailProdukBottomSheet fragment = new DetailProdukBottomSheet();
        Bundle args = new Bundle();
        args.putString("produk", produkJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_produk, container, false);

        if (getArguments() != null) {
            String json = getArguments().getString("produk");
            produk = new Gson().fromJson(json, DataProduk.class);
        }

        ImageView imgProduk = view.findViewById(R.id.imgProdukDetail);
        TextView tvNamaProduk = view.findViewById(R.id.tvNamaProdukDetail);
        TextView tvDeskripsi = view.findViewById(R.id.tvDeskripsiDetail);
        TextView tvHarga = view.findViewById(R.id.tvHargaDetail);
        TextView tvKategori = view.findViewById(R.id.tvKategoriDetail);
        TextView tvStok = view.findViewById(R.id.tvStokDetail);
        TextView tvStatus = view.findViewById(R.id.tvStatusDetail);
        TextView tvViewer = view.findViewById(R.id.tvViewerDetail);
        MaterialButton btnKeranjang = view.findViewById(R.id.btnKeranjang);

        if (produk != null) {
            tvNamaProduk.setText(produk.getNama_produk());
            tvDeskripsi.setText(produk.getDeskripsi());
            tvKategori.setText(produk.getNama_kategori());
            tvStok.setText("Stok: " + produk.getStok());

            String status = produk.getStok() == 0 ? "Tidak Tersedia" : "Tersedia";
            tvStatus.setText(status);

            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            tvHarga.setText(formatRupiah.format(produk.getHarga()));
            tvViewer.setText("Dilihat: " + produk.getViewer() + "x");

            Glide.with(requireContext())
                    .load(BASE_URL_IMAGE + produk.getGambar())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgProduk);

            btnKeranjang.setOnClickListener(v -> tambahKeranjang());
        }

        return view;
    }

    private void tambahKeranjang() {
        // Simpan ke SharedPreferences lokal, tidak perlu login
        SharedPreferences preferences = requireContext().getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
        String keranjangJson = preferences.getString("keranjang", "[]");
        JSONArray keranjangArray;
        try {
            keranjangArray = new JSONArray(keranjangJson);
            boolean found = false;
            for (int i = 0; i < keranjangArray.length(); i++) {
                JSONObject obj = keranjangArray.getJSONObject(i);
                if (obj.getInt("id_produk") == produk.getId_produk()) {
                    int jumlahSekarang = obj.getInt("jumlah");
                    if (jumlahSekarang >= produk.getStok()) {
                        Toast.makeText(requireContext(), "Jumlah keranjang melebihi stok!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    obj.put("jumlah", jumlahSekarang + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (produk.getStok() < 1) {
                    Toast.makeText(requireContext(), "Stok produk habis!", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject obj = new JSONObject();
                obj.put("id_produk", produk.getId_produk());
                obj.put("nama_produk", produk.getNama_produk());
                obj.put("harga", produk.getHarga());
                obj.put("gambar", produk.getGambar());
                obj.put("jumlah", 1);
                obj.put("berat", produk.getBerat());
                obj.put("stok", produk.getStok());
                keranjangArray.put(obj);
            }
            preferences.edit().putString("keranjang", keranjangArray.toString()).apply();
            Toast.makeText(requireContext(), "Ditambah ke keranjang", Toast.LENGTH_SHORT).show();

            BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
            if (navView != null) {
                navView.post(() -> com.example.uts_mobile_02995.utils.BadgeUtils.updateKeranjangBadge(requireContext(), navView));
            }
        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Gagal menyimpan keranjang", Toast.LENGTH_SHORT).show();
        }
    }
}
