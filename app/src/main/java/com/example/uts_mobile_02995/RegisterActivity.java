package com.example.uts_mobile_02995;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    TextView tvLogin;
    EditText etNama, etEmail, etPassword;
    MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi view
        tvLogin = findViewById(R.id.tvLogin);
        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Arahkan ke halaman login saat klik "Login"
        tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Proses submit saat klik "Daftar"
        btnRegister.setOnClickListener(view -> processSubmit(
                etNama.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPassword.getText().toString().trim()
        ));
    }

    // Validasi format email
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@[\\w\\.-]+\\.[a-z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Fungsi kirim data ke backend
    void processSubmit(String vNama, String vEmail, String vPassword) {
        if (vNama.isEmpty() || vEmail.isEmpty() || vPassword.isEmpty()) {
            showAlert("Semua kolom harus diisi!");
            return;
        }

        if (!isEmailValid(vEmail)) {
            showAlert("Email tidak valid!");
            return;
        }

        if (vPassword.length() < 6) {
            showAlert("Password minimal 6 karakter!");
            return;
        }

        // ServerAPI
        DataAPI api = ServerAPI.getApi();
        Call<ResponseBody> call = api.register(vNama, vEmail, vPassword);

        // Panggil API register
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        String result = json.getString("result");

                        if (result.equals("1")) {
                            showAlert("Pendaftaran berhasil!");
                            etNama.setText("");
                            etEmail.setText("");
                            etPassword.setText("");
                        } else {
                            // Jika gagal, tampilkan pesan umum
                            showAlert("Registrasi gagal. Email mungkin sudah terdaftar.");
                        }
                    } else {
                        showAlert("Registrasi gagal: " + response.code());
                    }
                } catch (JSONException | IOException e) {
                    showAlert("Terjadi kesalahan: " + e.getMessage());
                    Log.e("Register", "JSONException: ", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showAlert("Gagal terhubung ke server: " + t.getMessage());
                Log.e("Register", "onFailure: ", t);
            }
        });
    }

    // Fungsi tampil alert
    private void showAlert(String message) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show();
    }
}