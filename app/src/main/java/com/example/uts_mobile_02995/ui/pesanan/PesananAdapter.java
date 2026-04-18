package com.example.uts_mobile_02995.ui.pesanan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.data.DataPesanan;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {
    private final Context context;
    private final List<DataPesanan> listPesanan;
    private OnItemClickListener listener;

    public PesananAdapter(Context context, List<DataPesanan> listPesanan) {
        this.context = context;
        this.listPesanan = listPesanan;
    }

    public interface OnItemClickListener {
        void onItemClick(DataPesanan pesanan);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataPesanan pesanan = listPesanan.get(position);

        holder.tvIdPesanan.setText("MOKS#" + pesanan.getId());
        holder.tvStatusPesanan.setText(pesanan.getStatus());
        holder.tvTanggalPesanan.setText(pesanan.getTanggal());
        holder.tvAlamatPesanan.setText(pesanan.getAlamat());
        holder.tvItemPesanan.setText(pesanan.getTotalProduk());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvTotalPesanan.setText(formatRupiah.format(pesanan.getTotalBayar()));

        holder.tvNamaPenerima.setText(pesanan.getNamaPenerima());
        holder.tvNoHpPenerima.setText(pesanan.getNoHp());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pesanan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPesanan.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdPesanan, tvStatusPesanan, tvTanggalPesanan;
        TextView tvAlamatPesanan, tvItemPesanan, tvTotalPesanan;
        TextView tvNamaPenerima, tvNoHpPenerima;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdPesanan = itemView.findViewById(R.id.tvIdPesanan);
            tvStatusPesanan = itemView.findViewById(R.id.tvStatusPesanan);
            tvTanggalPesanan = itemView.findViewById(R.id.tvTanggalPesanan);
            tvAlamatPesanan = itemView.findViewById(R.id.tvAlamatPesanan);
            tvItemPesanan = itemView.findViewById(R.id.tvItemPesanan);
            tvTotalPesanan = itemView.findViewById(R.id.tvTotalPesanan);
            tvNamaPenerima = itemView.findViewById(R.id.tvNamaPenerima);
            tvNoHpPenerima = itemView.findViewById(R.id.tvNoHpPenerima);
        }
    }
}
