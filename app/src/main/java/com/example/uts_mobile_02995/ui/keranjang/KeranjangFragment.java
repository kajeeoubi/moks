package com.example.uts_mobile_02995.ui.keranjang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.LoginActivity;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.data.DataKeranjang;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KeranjangFragment extends Fragment {

    private RecyclerView rvKeranjang;
    private KeranjangAdapter keranjangAdapter;
    private List<DataKeranjang> keranjangList = new ArrayList<>();
    private TextView tvTotalBayar;
    private MaterialButton btnCheckout;
    private BottomNavigationView navView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keranjang, container, false);

        rvKeranjang = view.findViewById(R.id.rvKeranjang);
        rvKeranjang.setLayoutManager(new LinearLayoutManager(getContext()));

        tvTotalBayar = view.findViewById(R.id.tvSubtotal);
        btnCheckout = view.findViewById(R.id.btnPesan);

        btnCheckout.setOnClickListener(v -> {
            if (keranjangList.isEmpty()) {
                Toast.makeText(getContext(), "Minimal harus ada 1 item", Toast.LENGTH_SHORT).show();
            } else {
                // Cek apakah sudah login
                SharedPreferences pref = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
                boolean sudahLogin = pref.getBoolean("sudah_login", false);
                if (!sudahLogin) {
                    Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Navigation.findNavController(view).navigate(R.id.action_order_to_checkout);
                }
            }
        });

        navView = requireActivity().findViewById(R.id.nav_view);
        fetchOrderData();

        return view;
    }

    private void fetchOrderData() {
        // Ambil dari SharedPreferences
        SharedPreferences pref = requireContext().getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
        String keranjangJson = pref.getString("keranjang", "[]");
        keranjangList.clear();
        double total = 0;
        try {
            JSONArray dataArray = new JSONArray(keranjangJson);
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.getJSONObject(i);
                DataKeranjang produk = new DataKeranjang();
                produk.setId(item.optInt("id_produk", 0));
                produk.setJumlah(item.optInt("jumlah", 1));
                produk.setNama_produk(item.optString("nama_produk", ""));
                produk.setHarga(item.optDouble("harga", 0));
                produk.setGambar(item.optString("gambar", ""));
                produk.setBerat(item.optInt("berat", 1000));
                produk.setStok(item.has("stok") ? item.optInt("stok", 0) : null);
                keranjangList.add(produk);
                total += produk.getSubtotal();
            }
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            tvTotalBayar.setText(formatRupiah.format(total).replace("Rp", "Rp. "));
            keranjangAdapter = new KeranjangAdapter(getContext(), keranjangList, KeranjangFragment.this::fetchOrderData, navView);
            rvKeranjang.setAdapter(keranjangAdapter);
        } catch (JSONException e) {
            tvTotalBayar.setText("Rp. 0");
            Toast.makeText(getContext(), "Gagal parsing data keranjang", Toast.LENGTH_SHORT).show();
        }
    }
}