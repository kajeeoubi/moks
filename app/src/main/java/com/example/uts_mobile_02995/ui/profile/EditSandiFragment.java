package com.example.uts_mobile_02995.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSandiFragment extends Fragment {
    private TextInputEditText etSandiLama, etSandiBaru;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_sandi, container, false);
        
        etSandiLama = view.findViewById(R.id.etSandiLama);
        etSandiBaru = view.findViewById(R.id.etSandiBaru);
        MaterialButton btnUpdateSandi = view.findViewById(R.id.btnUpdateSandi);
        MaterialButton btnKembali = view.findViewById(R.id.btnKembali);

        btnUpdateSandi.setOnClickListener(v -> updatePassword());
        btnKembali.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return view;
    }

    private void updatePassword() {
        String sandiLama = etSandiLama.getText().toString().trim();
        String sandiBaru = etSandiBaru.getText().toString().trim();

        if (sandiLama.isEmpty() || sandiBaru.isEmpty()) {
            Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        DataAPI api = ServerAPI.getApi();
        api.updatePassword(email, sandiLama, sandiBaru).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            Toast.makeText(getContext(), "Kata sandi berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        } else {
                            Toast.makeText(getContext(), json.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
