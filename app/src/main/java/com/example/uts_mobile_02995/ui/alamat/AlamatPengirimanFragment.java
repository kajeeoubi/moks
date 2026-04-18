package com.example.uts_mobile_02995.ui.alamat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataAlamatPengiriman;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlamatPengirimanFragment extends Fragment implements DetailAlamatBottomSheet.OnAlamatAddedListener {
    private RecyclerView rvAlamatPengiriman;
    private AlamatPengirimanAdapter adapter;
    private List<DataAlamatPengiriman> alamatList = new ArrayList<>();
    private int idPengguna = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alamat_pengiriman, container, false);
        rvAlamatPengiriman = view.findViewById(R.id.rvAlamatPengiriman);
        rvAlamatPengiriman.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AlamatPengirimanAdapter(alamatList);
        adapter.setOnAlamatUtamaChangedListener(this::refreshAlamat);

        adapter.setOnEditAlamatListener(alamat -> {
            FragmentManager fm = getParentFragmentManager();
            DetailAlamatBottomSheet bottomSheet = DetailAlamatBottomSheet.newInstance(alamat);
            bottomSheet.setOnAlamatAddedListener(this::refreshAlamat);
            bottomSheet.show(fm, "DetailAlamatBottomSheet");
        });

        rvAlamatPengiriman.setAdapter(adapter);

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        View btnTambahAlamat = view.findViewById(R.id.btnTambahAlamat);
        if (btnTambahAlamat != null) {
            btnTambahAlamat.setOnClickListener(v -> {
                FragmentManager fm = getParentFragmentManager();
                DetailAlamatBottomSheet bottomSheet = new DetailAlamatBottomSheet();
                bottomSheet.setOnAlamatAddedListener(this::refreshAlamat);
                bottomSheet.show(fm, "DetailAlamatBottomSheet");
            });
        }

        SharedPreferences pref = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        idPengguna = pref.getInt("id_pengguna", -1);
        if (idPengguna != -1) {
            loadAlamatPengiriman(idPengguna);
        }
        return view;
    }

    @Override
    public void onAlamatAdded() {
        refreshAlamat();
    }

    public void refreshAlamat() {
        if (idPengguna != -1) {
            loadAlamatPengiriman(idPengguna);
        }
    }

    private void loadAlamatPengiriman(int idPengguna) {
        DataAPI api = ServerAPI.getApi();
        api.getAlamatPengiriman(idPengguna).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonStr = response.body().string();
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("result") == 1) {
                            JSONArray dataArr = json.getJSONArray("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<DataAlamatPengiriman>>(){}.getType();
                            List<DataAlamatPengiriman> list = gson.fromJson(dataArr.toString(), type);
                            alamatList.clear();
                            alamatList.addAll(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Belum ada alamat pengiriman", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Gagal parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal memuat alamat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
