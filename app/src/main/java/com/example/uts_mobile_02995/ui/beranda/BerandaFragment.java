package com.example.uts_mobile_02995.ui.beranda;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.ui.produk.ProdukAdapter;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataProduk;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BerandaFragment extends Fragment {

    private TextView tvPengguna;
    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        boolean isLoggedIn = (email != null && !email.isEmpty());

        View view = inflater.inflate(isLoggedIn ? R.layout.fragment_beranda : R.layout.fragment_beranda_tamu, container, false);

        if (isLoggedIn) {
            tvPengguna = view.findViewById(R.id.tvPengguna);
            getProfile(email);
        }

        // Slider gambar banner
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.banner_1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner_2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner_3, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        // Produk Populer
        RecyclerView rvProdukPopuler = view.findViewById(R.id.rvProdukPopuler);
        rvProdukPopuler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProdukPopuler.setNestedScrollingEnabled(false);
        loadProdukPopuler(rvProdukPopuler);

        // Kategori Toggle Group
        MaterialButtonToggleGroup tgKategori = view.findViewById(R.id.tgKategori);
        loadKategori(tgKategori, view);

        return view;
    }

    private void getProfile(String email) {
        DataAPI api = ServerAPI.getApi();

        api.getProfile(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String jsonStr = response.body().string();
                        JSONObject json = new JSONObject(jsonStr);

                        if (json.getInt("result") == 1) {
                            JSONObject data = json.getJSONObject("data");
                            String nama = data.optString("nama", "Nama Tidak Ditemukan");
                            tvPengguna.setText(nama);
                        } else {
                            Toast.makeText(getContext(), "Gagal memuat nama pengguna", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProdukPopuler(RecyclerView recyclerView) {
        ProdukAdapter.loadProdukPopuler(recyclerView, getContext(), getActivity().findViewById(R.id.nav_view));
    }

    private void loadKategori(MaterialButtonToggleGroup toggleGroup, View rootView) {
        DataAPI api = ServerAPI.getApi();
        api.getProduk().enqueue(new Callback<List<DataProduk>>() {
            @Override
            public void onResponse(@NonNull Call<List<DataProduk>> call, @NonNull Response<List<DataProduk>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DataProduk> produkList = response.body();
                    Set<String> kategoriSet = new HashSet<>();

                    for (DataProduk produk : produkList) {
                        if (produk.getNama_kategori() != null && !produk.getNama_kategori().isEmpty()) {
                            kategoriSet.add(produk.getNama_kategori());
                        }
                    }

                    for (String kategori : kategoriSet) {
                        MaterialButton button = createKategoriButton(kategori);
                        toggleGroup.addView(button);
                    }

                    toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                        if (isChecked) {
                            MaterialButton selected = group.findViewById(checkedId);
                            String selectedKategori = selected.getText().toString();

                            // Navigasi ke ProdukFragment
                            Bundle bundle = new Bundle();
                            bundle.putString("kategori", selectedKategori);

                            Navigation.findNavController(rootView)
                                    .navigate(R.id.navigation_produk, bundle);
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DataProduk>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MaterialButton createKategoriButton(String text) {
        MaterialButton button = new MaterialButton(getContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        button.setText(text);
        button.setId(View.generateViewId());
        button.setCornerRadius(20);
        button.setCheckable(true);
        return button;
    }
}
