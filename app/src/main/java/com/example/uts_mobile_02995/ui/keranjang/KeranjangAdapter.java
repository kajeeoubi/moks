package com.example.uts_mobile_02995.ui.keranjang;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataKeranjang;
import com.example.uts_mobile_02995.utils.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.KeranjangViewHolder> {

    private final Context context;
    private final List<DataKeranjang> keranjangList;
    private final Runnable onKeranjangUpdated;
    private final BottomNavigationView navView;
    private final DataAPI api;

    public KeranjangAdapter(Context context, List<DataKeranjang> keranjangList, Runnable onKeranjangUpdated, BottomNavigationView navView) {
        this.context = context;
        this.keranjangList = keranjangList;
        this.onKeranjangUpdated = onKeranjangUpdated;
        this.navView = navView;
        this.api = ServerAPI.getApi();
    }

    @NonNull
    @Override
    public KeranjangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keranjang, parent, false);
        return new KeranjangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeranjangViewHolder holder, int position) {
        DataKeranjang produk = keranjangList.get(position);

        holder.tvKeranjangNama.setText(produk.getNama_produk());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvKeranjangHarga.setText(formatRupiah.format(produk.getHarga()).replace("Rp", "Rp "));
        holder.tvQty.setText(String.valueOf(produk.getJumlah()));

        Glide.with(context)
                .load(ServerAPI.BASE_URL_IMAGE + produk.getGambar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgKeranjang);

        holder.btnTambah.setOnClickListener(v -> updateJumlah(produk, produk.getJumlah() + 1));

        holder.btnKurang.setOnClickListener(v -> {
            if (produk.getJumlah() > 1) {
                updateJumlah(produk, produk.getJumlah() - 1);
            } else {
                deleteItem(produk, position);
            }
        });

        holder.btnHapus.setOnClickListener(v -> deleteItem(produk, position));
    }

    private void updateJumlah(DataKeranjang produk, int jumlahBaru) {
        SharedPreferences pref = context.getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
        String keranjangJson = pref.getString("keranjang", "[]");
        try {
            JSONArray arr = new JSONArray(keranjangJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getInt("id_produk") == produk.getId()) {
                    // Ambil stok dari produkList
                    int stok = produk.getStok() != null ? produk.getStok() : Integer.MAX_VALUE;
                    
                    // Cek apakah jumlah baru melebihi stok
                    if (jumlahBaru > stok) {
                        Toast.makeText(context, "Jumlah melebihi stok produk!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (jumlahBaru > 0) {
                        obj.put("jumlah", jumlahBaru);
                        produk.setJumlah(jumlahBaru);
                        pref.edit().putString("keranjang", arr.toString()).apply();
                        notifyItemChanged(i);
                        notifyUpdate();
                        Toast.makeText(context, "Jumlah diperbarui", Toast.LENGTH_SHORT).show();
                    } else {
                        arr.remove(i);
                        int pos = getAdapterPositionById(produk.getId());
                        if (pos != -1) {
                            keranjangList.remove(pos);
                            pref.edit().putString("keranjang", arr.toString()).apply();
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, keranjangList.size());
                            notifyUpdate();
                            Toast.makeText(context, "Produk dihapus", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
            }
            if (navView != null) BadgeUtils.updateKeranjangBadge(context, navView);
        } catch (JSONException e) {
            Toast.makeText(context, "Gagal update keranjang", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem(DataKeranjang produk, int position) {
        SharedPreferences pref = context.getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
        String keranjangJson = pref.getString("keranjang", "[]");
        try {
            JSONArray arr = new JSONArray(keranjangJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getInt("id_produk") == produk.getId()) {
                    arr.remove(i);
                    int pos = getAdapterPositionById(produk.getId());
                    if (pos != -1) {
                        keranjangList.remove(pos);
                        pref.edit().putString("keranjang", arr.toString()).apply();
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, keranjangList.size());
                        notifyUpdate();
                        Toast.makeText(context, "Produk dihapus", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
            if (navView != null) BadgeUtils.updateKeranjangBadge(context, navView);
        } catch (JSONException e) {
            Toast.makeText(context, "Gagal hapus keranjang", Toast.LENGTH_SHORT).show();
        }
    }

    private int getAdapterPositionById(int idProduk) {
        for (int i = 0; i < keranjangList.size(); i++) {
            if (keranjangList.get(i).getId() == idProduk) {
                return i;
            }
        }
        return -1;
    }

    private void notifyUpdate() {
        if (onKeranjangUpdated != null) onKeranjangUpdated.run();
        if (navView != null) BadgeUtils.updateKeranjangBadge(context, navView);
    }

    @Override
    public int getItemCount() {
        return keranjangList.size();
    }

    public static class KeranjangViewHolder extends RecyclerView.ViewHolder {
        ImageView imgKeranjang;
        TextView tvKeranjangNama, tvKeranjangHarga, tvQty;
        ImageButton btnHapus, btnTambah, btnKurang;

        public KeranjangViewHolder(@NonNull View itemView) {
            super(itemView);
            imgKeranjang = itemView.findViewById(R.id.imgKeranjang);
            tvKeranjangNama = itemView.findViewById(R.id.tvNamaKeranjang);
            tvKeranjangHarga = itemView.findViewById(R.id.tvHargaKeranjang);
            tvQty = itemView.findViewById(R.id.tvQty);
            btnHapus = itemView.findViewById(R.id.btnHapus);
            btnTambah = itemView.findViewById(R.id.btnTambah);
            btnKurang = itemView.findViewById(R.id.btnKurang);
        }
    }
}