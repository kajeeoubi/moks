package com.example.uts_mobile_02995.ui.produk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataProduk;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukFragment extends Fragment {

    private RecyclerView rvProduk;
    private ProdukAdapter produkAdapter;
    private List<DataProduk> produkList = new ArrayList<>();
    private SearchView svProduk;
    private MaterialButtonToggleGroup tgKategori;

    private String kategoriAwal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produk, container, false);

        rvProduk = view.findViewById(R.id.rvProduk);
        rvProduk.setLayoutManager(new GridLayoutManager(getContext(), 2));

        svProduk = view.findViewById(R.id.svProduk);
        tgKategori = view.findViewById(R.id.tgKategori);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        Bundle args = getArguments();
        if (args != null) {
            kategoriAwal = args.getString("kategori", null);
        }

        fetchDataProduk(bottomNavigationView);

        svProduk.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        return view;
    }

    private void fetchDataProduk(BottomNavigationView bottomNavigationView) {
        DataAPI api = ServerAPI.getApi();
        Call<List<DataProduk>> call = api.getProduk();

        call.enqueue(new Callback<List<DataProduk>>() {
            @Override
            public void onResponse(Call<List<DataProduk>> call, Response<List<DataProduk>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    produkList = response.body();
                    produkAdapter = new ProdukAdapter(getContext(), produkList, bottomNavigationView);
                    rvProduk.setAdapter(produkAdapter);

                    setupKategoriButtons();

                } else {
                    Toast.makeText(getContext(), "Data produk kosong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DataProduk>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal mengambil data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API Error", t.getMessage());
            }
        });
    }

    private void setupKategoriButtons() {
        tgKategori.removeAllViews();

        MaterialButton btnSemua = createKategoriButton("Semua");
        btnSemua.setChecked(kategoriAwal == null);
        tgKategori.addView(btnSemua);

        btnSemua.setOnClickListener(v -> {
            if (!btnSemua.isChecked()) btnSemua.setChecked(true);
            filterList(svProduk.getQuery().toString());
        });

        Set<String> kategoriSet = new HashSet<>();
        for (DataProduk produk : produkList) {
            kategoriSet.add(produk.getNama_kategori());
        }

        for (String kategori : kategoriSet) {
            MaterialButton btnKategori = createKategoriButton(kategori);
            tgKategori.addView(btnKategori);

            btnKategori.setOnClickListener(v -> {
                if (!btnKategori.isChecked()) btnKategori.setChecked(true);
                filterList(svProduk.getQuery().toString());
            });
            if (kategori.equalsIgnoreCase(kategoriAwal)) {
                btnKategori.setChecked(true);
            }
        }
        if (kategoriAwal != null) {
            filterList("");
        }
    }

    private MaterialButton createKategoriButton(String text) {
        MaterialButton button = new MaterialButton(getContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        button.setText(text);
        button.setId(View.generateViewId());
        button.setCornerRadius(20);
        button.setCheckable(true);
        return button;
    }

    private void filterList(String query) {
        List<DataProduk> filteredList = new ArrayList<>();
        String activeKategori = getActiveKategori();

        for (DataProduk produk : produkList) {
            boolean matchesSearch = produk.getNama_produk().toLowerCase(Locale.ROOT)
                    .contains(query.toLowerCase(Locale.ROOT));
            boolean matchesCategory = activeKategori.equals("Semua") ||
                    produk.getNama_kategori().equalsIgnoreCase(activeKategori);

            if (matchesSearch && matchesCategory) {
                filteredList.add(produk);
            }
        }

        produkAdapter = new ProdukAdapter(getContext(), filteredList, getActivity().findViewById(R.id.nav_view));
        rvProduk.setAdapter(produkAdapter);
    }

    private String getActiveKategori() {
        int checkedId = tgKategori.getCheckedButtonId();
        if (checkedId == -1) {
            return "Semua";
        }

        MaterialButton checkedButton = tgKategori.findViewById(checkedId);
        return checkedButton.getText().toString();
    }
}
