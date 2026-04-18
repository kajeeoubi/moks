package com.example.uts_mobile_02995.ui.pesanan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.data.DataDetailPesanan.DetailProduk;

import java.util.List;

public class DetailPesananAdapter extends RecyclerView.Adapter<DetailPesananAdapter.ViewHolder> {
    private final List<DetailProduk> listProduk;

    public DetailPesananAdapter(List<DetailProduk> listProduk) {
        this.listProduk = listProduk;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_pesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailProduk produk = listProduk.get(position);
        holder.tvNamaProduk.setText(produk.getNamaProduk());
        holder.tvJumlahProduk.setText("x" + produk.getJumlah());
    }

    @Override
    public int getItemCount() {
        return listProduk.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaProduk, tvJumlahProduk;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvJumlahProduk = itemView.findViewById(R.id.tvJumlahProduk);
        }
    }
}
