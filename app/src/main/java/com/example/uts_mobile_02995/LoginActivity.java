package com.example.uts_mobile_02995;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String PREF_NAME = "Data Pengguna";

    private ProgressDialog pd;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnLewati;
    private TextView tvRegister, tvLupaPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean sudahLogin = preferences.getBoolean("sudah_login", false);
        if (sudahLogin) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLewati = findViewById(R.id.btnLewati);
        tvRegister = findViewById(R.id.tvRegister);
        tvLupaPassword = findViewById(R.id.tvLupaPassword);

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showAlertDialog("Email dan password tidak boleh kosong!");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showAlertDialog("Format email tidak valid!");
                return;
            }

            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Proses Login...");
            pd.setMessage("Tunggu sebentar...");
            pd.setCancelable(false);
            pd.show();

            prosesLogin(email, password);
        });

        btnLewati.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean("sudah_login", false);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvLupaPassword.setOnClickListener(view ->
                Toast.makeText(LoginActivity.this, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show()
        );
    }

    private void prosesLogin(String email, String password) {
        DataAPI api = ServerAPI.getApi();
        api.login(email, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());

                        if (json.getInt("result") == 1) {
                            JSONObject data = json.getJSONObject("data");

                            if (data.has("id") && data.has("email")) {
                                int idPengguna = data.getInt("id");
                                String email = data.getString("email");

                                SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
                                editor.putBoolean("sudah_login", true);
                                editor.putString("email", email);
                                editor.putInt("id_pengguna", idPengguna);
                                editor.apply();

                                pindahKeMain(email);
                            } else {
                                showAlertDialog("Data login tidak lengkap.");
                            }

                        } else {
                            showAlertDialog("Login gagal. Periksa kembali email dan password.");
                        }
                    } else {
                        showAlertDialog("Gagal mendapatkan respon dari server.");
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    showAlertDialog("Terjadi kesalahan: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Log.e("LoginError", "Error: " + t.getMessage());
                showAlertDialog("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    private void pindahKeMain(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setNegativeButton("Coba Lagi", null)
                .create()
                .show();
    }
}