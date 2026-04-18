package com.example.uts_mobile_02995.ui.alamat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataAlamatPengiriman;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlamatPengirimanAdapter extends RecyclerView.Adapter<AlamatPengirimanAdapter.ViewHolder> {
    private final List<DataAlamatPengiriman> alamatList;
    private OnAlamatUtamaChangedListener alamatUtamaChangedListener;
    private OnEditAlamatListener onEditAlamatListener;

    public interface OnAlamatUtamaChangedListener {
        void onAlamatUtamaChanged();
    }

    public void setOnAlamatUtamaChangedListener(OnAlamatUtamaChangedListener listener) {
        this.alamatUtamaChangedListener = listener;
    }

    public interface OnEditAlamatListener {
        void onEdit(DataAlamatPengiriman alamat);
    }

    public void setOnEditAlamatListener(OnEditAlamatListener listener) {
        this.onEditAlamatListener = listener;
    }

    public AlamatPengirimanAdapter(List<DataAlamatPengiriman> alamatList) {
        this.alamatList = alamatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alamat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataAlamatPengiriman data = alamatList.get(position);
        holder.tvNamaPenerima.setText(data.getNama_penerima());
        holder.tvNoHp.setText(data.getNo_hp());
        holder.tvAlamatLengkap.setText(
            data.getAlamat_lengkap() + "\n" +
            data.getKota() + ", " + data.getProvinsi() + " " + data.getKode_pos()
        );

        if (data.getAlamat_utama() == 1) {
            holder.cardView.setStrokeColor(holder.itemView.getContext().getResources().getColor(R.color.primary_button));
            holder.cardView.setStrokeWidth(6);
        } else {
            holder.cardView.setStrokeColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            holder.cardView.setStrokeWidth(0);
        }

        // Listener untuk jadikan alamat utama
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            new AlertDialog.Builder(context)
                .setTitle("Jadikan Alamat Utama")
                .setMessage("Yakin ingin menjadikan alamat ini sebagai alamat utama?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    DataAPI api = ServerAPI.getApi();
                    api.setAlamatUtama(data.getId()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(context, "Alamat utama diperbarui", Toast.LENGTH_SHORT).show();
                            if (alamatUtamaChangedListener != null) {
                                alamatUtamaChangedListener.onAlamatUtamaChanged();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(context, "Gagal memperbarui alamat utama", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
        });

        // Listener untuk hapus alamat
        holder.btnDelete.setOnClickListener(v -> {
            Context context = v.getContext();
            new AlertDialog.Builder(context)
                .setTitle("Hapus Alamat")
                .setMessage("Yakin ingin menghapus alamat ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    DataAPI api = ServerAPI.getApi();
                    api.hapusAlamatPengiriman(data.getId()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(context, "Alamat dihapus", Toast.LENGTH_SHORT).show();
                            if (alamatUtamaChangedListener != null) {
                                alamatUtamaChangedListener.onAlamatUtamaChanged();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(context, "Gagal menghapus alamat", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
        });

        // Aksi pada tombol edit
        holder.btnEdit.setOnClickListener(v -> {
            if (onEditAlamatListener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEditAlamatListener.onEdit(alamatList.get(adapterPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alamatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaPenerima, tvNoHp, tvAlamatLengkap;
        android.widget.ImageButton btnDelete, btnEdit;
        MaterialCardView cardView;
        ViewHolder(View itemView) {
            super(itemView);
            tvNamaPenerima = itemView.findViewById(R.id.tvNamaPenerima);
            tvNoHp = itemView.findViewById(R.id.tvNoHp);
            tvAlamatLengkap = itemView.findViewById(R.id.tvAlamatLengkap);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            cardView = (MaterialCardView) itemView;
        }
    }
}
