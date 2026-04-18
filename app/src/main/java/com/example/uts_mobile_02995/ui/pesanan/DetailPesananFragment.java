package com.example.uts_mobile_02995.ui.pesanan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataDetailPesanan;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPesananFragment extends Fragment {
    private TextView tvIdPesanan, tvStatusPesanan, tvTanggalPesanan;
    private TextView tvNamaPenerima, tvNoHpPenerima, tvAlamatLengkapPenerima;
    private TextView tvKurir, tvLayananKurir, tvBiayaOngkir;
    private TextView tvTotalHargaProduk, tvTotalBiayaOngkir, tvTotalBayar;
    private TextView tvMetodePembayaran, tvStatusBayar;
    private RecyclerView rvProdukDetailPesanan;
    private ImageView imgBuktiBayar;
    private Button btnBuktiBayar;
    private CardView cardBuktiBayar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String idPesanan;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_pesanan, container, false);

        initViews(view);
        loadDetailPesanan();

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnBuktiBayar.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
        });

        return view;
    }

    private void initViews(View view) {
        tvIdPesanan = view.findViewById(R.id.tvIdPesanan);
        tvStatusPesanan = view.findViewById(R.id.tvStatusPesanan);
        tvTanggalPesanan = view.findViewById(R.id.tvTanggalPesanan);
        tvNamaPenerima = view.findViewById(R.id.tvNamaPenerima);
        tvNoHpPenerima = view.findViewById(R.id.tvNoHpPenerima);
        tvAlamatLengkapPenerima = view.findViewById(R.id.tvAlamatLengkapPenerima);
        tvKurir = view.findViewById(R.id.tvKurir);
        tvLayananKurir = view.findViewById(R.id.tvLayananKurir);
        tvBiayaOngkir = view.findViewById(R.id.tvBiayaOngkir);
        tvTotalHargaProduk = view.findViewById(R.id.tvTotalHargaProduk);
        tvTotalBiayaOngkir = view.findViewById(R.id.tvTotalBiayaOngkir);
        tvTotalBayar = view.findViewById(R.id.tvTotalBayar);
        tvMetodePembayaran = view.findViewById(R.id.tvMetodePembayaran);
        rvProdukDetailPesanan = view.findViewById(R.id.rvProdukDetailPesanan);
        imgBuktiBayar = view.findViewById(R.id.imgBuktiBayar);
        btnBuktiBayar = view.findViewById(R.id.btnBuktiBayar);
        cardBuktiBayar = view.findViewById(R.id.cardBuktiBayar);
        tvStatusBayar = view.findViewById(R.id.tvStatusBayar);
        rvProdukDetailPesanan.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadDetailPesanan() {
        idPesanan = getArguments() != null ? getArguments().getString("id_pesanan") : null;
        if (idPesanan == null) return;

        DataAPI api = ServerAPI.getApi();
        api.getDetailPesanan(idPesanan).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            Gson gson = new Gson();
                            DataDetailPesanan detail = gson.fromJson(
                                    json.getJSONObject("data").toString(),
                                    DataDetailPesanan.class
                            );
                            displayDetailPesanan(detail);
                        } else {
                            showError(json.getString("message"));
                        }
                    }
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                showError(t.getMessage());
            }
        });
    }

    private void displayDetailPesanan(DataDetailPesanan detail) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        tvIdPesanan.setText(String.format("MOKS#%s", detail.getIdPesanan()));
        tvStatusPesanan.setText(detail.getStatusPesanan());
        tvTanggalPesanan.setText(detail.getTanggalPesanan());
        tvNamaPenerima.setText(detail.getNamaPenerima());
        tvNoHpPenerima.setText(detail.getNoHpPenerima());
        tvAlamatLengkapPenerima.setText(detail.getAlamatLengkapPenerima());
        tvKurir.setText(detail.getKurir());
        tvLayananKurir.setText(detail.getLayananKurir());
        tvBiayaOngkir.setText(formatter.format(detail.getBiayaOngkir()));
        tvTotalHargaProduk.setText(formatter.format(detail.getTotalHargaProduk()));
        tvTotalBiayaOngkir.setText(formatter.format(detail.getBiayaOngkir()));
        tvTotalBayar.setText(formatter.format(detail.getTotalBayar()));
        tvMetodePembayaran.setText(detail.getMetodeBayar());

        if (detail.getMetodeBayar().equalsIgnoreCase("cod")) {
            cardBuktiBayar.setVisibility(View.VISIBLE);
            tvStatusBayar.setVisibility(View.VISIBLE);
            tvStatusBayar.setText("Pembayaran dilakukan saat barang diterima (COD).");
            btnBuktiBayar.setVisibility(View.GONE);
        } else {
            cardBuktiBayar.setVisibility(View.VISIBLE);
            if (detail.getBuktiBayar() != null && !detail.getBuktiBayar().isEmpty()) {
                btnBuktiBayar.setVisibility(View.GONE);
                tvStatusBayar.setVisibility(View.GONE);
                Glide.with(requireContext())
                        .load(ServerAPI.BASE_URL_BUKTI_BAYAR + detail.getBuktiBayar())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imgBuktiBayar);
                imgBuktiBayar.setVisibility(View.VISIBLE);
            } else {
                btnBuktiBayar.setVisibility(View.VISIBLE);
                tvStatusBayar.setVisibility(View.VISIBLE);
                tvStatusBayar.setText("Belum ada bukti pembayaran diunggah.");
                imgBuktiBayar.setVisibility(View.GONE);
            }
        }

        DetailPesananAdapter adapter = new DetailPesananAdapter(detail.getDaftarProduk());
        rvProdukDetailPesanan.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            String realPath = getRealPathFromURI(imageUri);
            if (realPath == null) {
                showError("Gagal mendapatkan path file");
                return;
            }

            File file = new File(realPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("bukti_bayar", file.getName(), requestFile);
            RequestBody idPesananBody = RequestBody.create(MediaType.parse("text/plain"), idPesanan);

            DataAPI api = ServerAPI.getApi();
            api.uploadBuktiBayar(idPesananBody, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            if (jsonResponse.getInt("result") == 1) {
                                loadDetailPesanan();
                                Toast.makeText(getContext(), "Berhasil diunggah", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Gagal mengunggah", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Gagal mengunggah", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        showError(e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    showError(t.getMessage());
                }
            });
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {android.provider.MediaStore.Images.Media.DATA};
            android.database.Cursor cursor = requireActivity().getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null) return null;
            int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        } catch (Exception e) {
            return null;
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
