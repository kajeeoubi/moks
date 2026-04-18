package com.example.uts_mobile_02995.ui.checkout;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private final Context context;
    private final List<DataKeranjang> checkoutList;
    private final Runnable onItemDeleted;
    private final DataAPI api;

    public CheckoutAdapter(Context context, List<DataKeranjang> checkoutList, Runnable onItemDeleted) {
        this.context = context;
        this.checkoutList = checkoutList;
        this.onItemDeleted = onItemDeleted;
        this.api = ServerAPI.getApi();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataKeranjang produk = checkoutList.get(position);

        holder.tvNamaProduk.setText(produk.getNama_produk());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvHargaProduk.setText(formatRupiah.format(produk.getHarga()).replace("Rp", "Rp. "));
        holder.tvJumlahProduk.setText("x" + produk.getJumlah());

        Glide.with(context)
                .load(ServerAPI.BASE_URL_IMAGE + produk.getGambar())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgProduk);

        holder.btnDelete.setOnClickListener(v -> {
            if (checkoutList.size() <= 1) {
                Toast.makeText(context, "Minimal harus ada 1 item", Toast.LENGTH_SHORT).show();
                return;
            }

            deleteItem(position, produk.getId());
        });
    }

    private void deleteItem(int position, int idKeranjang) {
        // Hapus dari SharedPreferences lokal
        SharedPreferences pref = context.getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
        String keranjangJson = pref.getString("keranjang", "[]");
        try {
            JSONArray arr = new JSONArray(keranjangJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getInt("id_produk") == idKeranjang) {
                    arr.remove(i);
                    // Sinkronkan dengan list dan tampilan
                    checkoutList.remove(position);
                    pref.edit().putString("keranjang", arr.toString()).apply();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, checkoutList.size());
                    Toast.makeText(context, "Item dihapus", Toast.LENGTH_SHORT).show();
                    if (onItemDeleted != null) onItemDeleted.run();
                    // Update badge setelah hapus di checkout
                    com.example.uts_mobile_02995.utils.BadgeUtils.updateKeranjangBadge(context,
                        ((android.app.Activity) context).findViewById(R.id.nav_view));
                    break;
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, "Gagal hapus keranjang", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return checkoutList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduk;
        ImageButton btnDelete;
        TextView tvNamaProduk, tvHargaProduk, tvJumlahProduk;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduk = itemView.findViewById(R.id.imgProduk);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvHargaProduk = itemView.findViewById(R.id.tvHargaProduk);
            tvJumlahProduk = itemView.findViewById(R.id.tvJumlahProduk);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}