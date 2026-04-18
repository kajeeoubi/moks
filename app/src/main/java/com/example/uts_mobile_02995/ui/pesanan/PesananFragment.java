package com.example.uts_mobile_02995.ui.pesanan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataPesanan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananFragment extends Fragment {
    private RecyclerView rvRiwayatPesanan;
    private PesananAdapter adapter;
    private List<DataPesanan> listPesanan;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat_pesanan, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        rvRiwayatPesanan = view.findViewById(R.id.rvRiwayatPesanan);
        rvRiwayatPesanan.setLayoutManager(new LinearLayoutManager(getContext()));
        
        listPesanan = new ArrayList<>();
        adapter = new PesananAdapter(requireContext(), listPesanan);
        adapter.setOnItemClickListener(pesanan -> {
            Bundle bundle = new Bundle();
            bundle.putString("id_pesanan", pesanan.getId());
            
            Navigation.findNavController(requireView())
                .navigate(R.id.action_riwayat_to_detailPesanan, bundle);
        });
        rvRiwayatPesanan.setAdapter(adapter);

        loadRiwayatPesanan();

        return view;
    }

    private void loadRiwayatPesanan() {
        SharedPreferences pref = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        int idPengguna = pref.getInt("id_pengguna", -1);

        if (idPengguna == -1) {
            Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        DataAPI api = ServerAPI.getApi();
        api.getRiwayatPesanan(idPengguna).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        
                        if (json.getInt("result") == 1) {
                            JSONArray data = json.getJSONArray("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<DataPesanan>>(){}.getType();
                            List<DataPesanan> newList = gson.fromJson(data.toString(), type);
                            
                            listPesanan.clear();
                            listPesanan.addAll(newList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
