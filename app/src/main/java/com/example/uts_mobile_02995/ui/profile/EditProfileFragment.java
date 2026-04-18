package com.example.uts_mobile_02995.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataPengguna;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private EditText etNama, etAlamat, etKota, etProvinsi, etTelp, etKodePos;
    private MaterialButton btnUpdate, btnKembali;
    private String email;
    private DataAPI api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Ambil email dari SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        // Setup Retrofit
        api = ServerAPI.getApi();

        // Inisialisasi UI
        initUI(view);

        // Load data profil
        getProfile(email);

        // Aksi tombol kembali
        btnKembali.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Aksi tombol update
        btnUpdate.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void initUI(View view) {
        etNama = view.findViewById(R.id.etNama);
        etAlamat = view.findViewById(R.id.etAlamat);
        etKota = view.findViewById(R.id.etKota);
        etProvinsi = view.findViewById(R.id.etProvinsi);
        etTelp = view.findViewById(R.id.etTelp);
        etKodePos = view.findViewById(R.id.etKodePos);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnKembali = view.findViewById(R.id.btnKembali);
    }

    private void getProfile(String vemail) {
        api.getProfile(vemail).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String jsonStr = response.body().string();
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getString("result").equals("1")) {
                            JSONObject data = json.getJSONObject("data");
                            etNama.setText(getSafeString(data, "nama"));
                            etAlamat.setText(getSafeString(data, "alamat"));
                            etKota.setText(getSafeString(data, "kota"));
                            etProvinsi.setText(getSafeString(data, "provinsi"));
                            etTelp.setText(getSafeString(data, "no_hp"));
                            etKodePos.setText(getSafeString(data, "kode_pos"));
                        } else {
                            Toast.makeText(getContext(), "Gagal memuat data profil", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        DataPengguna data = new DataPengguna(
                etNama.getText().toString(),
                etAlamat.getText().toString(),
                etKota.getText().toString(),
                etProvinsi.getText().toString(),
                etTelp.getText().toString(),
                etKodePos.getText().toString(),
                email
        );

        updateProfile(data);
    }

    private void updateProfile(DataPengguna data) {
        api.updateProfile(
                data.getNama(), data.getAlamat(), data.getKota(),
                data.getProvinsi(), data.getNo_hp(), data.getKode_pos(), data.getEmail()
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d("ResponseUpdate", responseString);

                        JSONObject json = new JSONObject(responseString);

                        if (json.has("message")) {
                            Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        }

                        getProfile(data.getEmail());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error saat menyimpan data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Simpan Gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getSafeString(JSONObject obj, String key) {
        try {
            String value = obj.getString(key);
            return (value == null || value.equals("null")) ? "" : value;
        } catch (JSONException e) {
            return "";
        }
    }
}
