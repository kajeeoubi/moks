package com.example.uts_mobile_02995.ui.produk;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataProduk;
import com.example.uts_mobile_02995.utils.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    private final Context context;
    private final List<DataProduk> produkList;
    private final BottomNavigationView navView;

    public ProdukAdapter(Context context, List<DataProduk> produkList, BottomNavigationView navView) {
        this.context = context;
        this.produkList = produkList;
        this.navView = navView;
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        DataProduk produk = produkList.get(position);
        holder.tvNamaProduk.setText(produk.getNama_produk());
        holder.tvKategoriProduk.setText(produk.getNama_kategori());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvHarga.setText(formatRupiah.format(produk.getHarga()).replace("Rp", "Rp "));
        holder.tvStok.setText("Stok: " + produk.getStok());

        String status = produk.getStok() == 0 ? "Tidak Tersedia" : "Tersedia";
        holder.tvStatusProduk.setText(status);

        holder.tvDilihat.setText("Dilihat: " + produk.getViewer() + "x");

        Glide.with(context)
                .load(ServerAPI.BASE_URL_IMAGE + produk.getGambar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgProduk);

        holder.btnKeranjang.setOnClickListener(v -> {
            // Simpan ke SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
            String keranjangJson = preferences.getString("keranjang", "[]");
            try {
                JSONArray keranjangArray = new JSONArray(keranjangJson);
                boolean found = false;
                for (int i = 0; i < keranjangArray.length(); i++) {
                    JSONObject obj = keranjangArray.getJSONObject(i);
                    if (obj.getInt("id_produk") == produk.getId_produk()) {
                        int jumlahSekarang = obj.getInt("jumlah");
                        if (jumlahSekarang >= produk.getStok()) {
                            Toast.makeText(context, "Jumlah di keranjang sudah melebihi stok!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        obj.put("jumlah", jumlahSekarang + 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (produk.getStok() < 1) {
                        Toast.makeText(context, "Stok produk habis!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("id_produk", produk.getId_produk());
                    obj.put("nama_produk", produk.getNama_produk());
                    obj.put("harga", produk.getHarga());
                    obj.put("gambar", produk.getGambar());
                    obj.put("jumlah", 1);
                    obj.put("berat", produk.getBerat());
                    obj.put("stok", produk.getStok()); // simpan stok
                    keranjangArray.put(obj);
                }
                preferences.edit().putString("keranjang", keranjangArray.toString()).apply();
                Toast.makeText(context, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                if (navView != null) {
                    navView.post(() -> BadgeUtils.updateKeranjangBadge(context, navView));
                } else if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    BottomNavigationView nav = activity.findViewById(R.id.nav_view);
                    if (nav != null) {
                        nav.post(() -> BadgeUtils.updateKeranjangBadge(context, nav));
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Gagal menyimpan keranjang", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            produk.setViewer(produk.getViewer() + 1);
            holder.tvDilihat.setText("Dilihat: " + produk.getViewer() + "x");

            DataAPI dataAPI = ServerAPI.getApi();
            Call<ResponseBody> call = dataAPI.updateViewer(produk.getId_produk());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Gson gson = new Gson();
                    String produkJson = gson.toJson(produk);

                    DetailProdukBottomSheet detailProdukBottomSheet = DetailProdukBottomSheet.newInstance(produkJson);
                    if (context instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) context;
                        detailProdukBottomSheet.show(activity.getSupportFragmentManager(), "DetailProdukBottomSheet");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Gagal update viewer", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static void loadProdukPopuler(RecyclerView recyclerView, Context context, BottomNavigationView navView) {
        DataAPI api = ServerAPI.getApi();
        api.getProdukPopuler().enqueue(new Callback<List<DataProduk>>() {
            @Override
            public void onResponse(@NonNull Call<List<DataProduk>> call, @NonNull Response<List<DataProduk>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProdukAdapter adapter = new ProdukAdapter(context, response.body(), navView);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DataProduk>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Gagal memuat produk populer", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ProdukViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduk;
        TextView tvNamaProduk, tvKategoriProduk, tvHarga, tvStok, tvStatusProduk, tvDilihat;
        ImageButton btnKeranjang;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduk = itemView.findViewById(R.id.imgProduk);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvKategoriProduk = itemView.findViewById(R.id.tvKategoriProduk);
            tvHarga = itemView.findViewById(R.id.tvHargaProduk);
            tvStok = itemView.findViewById(R.id.tvStokProduk);
            tvStatusProduk = itemView.findViewById(R.id.tvStatusProduk);
            tvDilihat = itemView.findViewById(R.id.tvViewer);
            btnKeranjang = itemView.findViewById(R.id.btnKeranjang);
        }
    }
}
