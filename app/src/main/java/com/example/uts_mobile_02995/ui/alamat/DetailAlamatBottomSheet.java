package com.example.uts_mobile_02995.ui.alamat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataAlamatPengiriman;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailAlamatBottomSheet extends BottomSheetDialogFragment {
    private AutoCompleteTextView spinnerProvinsi, spinnerKota;
    private ArrayList<String> provinsiList = new ArrayList<>();
    private ArrayList<String> kotaList = new ArrayList<>();
    private HashMap<String, String> provinsiMap = new HashMap<>();
    private HashMap<String, String> kotaMap = new HashMap<>();
    private String selectedProvinsiId = "";
    private String selectedKotaId = "";
    private String selectedProvinsiName = "";
    private String selectedKotaName = "";

    public interface OnAlamatAddedListener {
        void onAlamatAdded();
    }

    private OnAlamatAddedListener alamatAddedListener;

    public void setOnAlamatAddedListener(OnAlamatAddedListener listener) {
        this.alamatAddedListener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnAlamatAddedListener) {
            alamatAddedListener = (OnAlamatAddedListener) getParentFragment();
        }
    }

    private DataAlamatPengiriman alamatEdit = null;
    private boolean isEditMode = false;

    public static DetailAlamatBottomSheet newInstance(DataAlamatPengiriman alamat) {
        DetailAlamatBottomSheet fragment = new DetailAlamatBottomSheet();
        Bundle args = new Bundle();
        args.putInt("id", alamat.getId());
        args.putString("nama_penerima", alamat.getNama_penerima());
        args.putString("alamat_lengkap", alamat.getAlamat_lengkap());
        args.putString("kota", alamat.getKota());
        args.putString("provinsi", alamat.getProvinsi());
        args.putString("kode_pos", alamat.getKode_pos());
        args.putString("no_hp", alamat.getNo_hp());
        args.putInt("alamat_utama", alamat.getAlamat_utama());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_alamat, container, false);

        EditText etNamaPenerima = view.findViewById(R.id.etNamaPenerima);
        EditText etAlamatLengkap = view.findViewById(R.id.etAlamatLengkap);
        spinnerProvinsi = view.findViewById(R.id.spinnerProvinsi);
        spinnerKota = view.findViewById(R.id.spinnerKota);
        EditText etKodePos = view.findViewById(R.id.etKodePos);
        EditText etNoHp = view.findViewById(R.id.etNoHp);
        Button btnSimpan = view.findViewById(R.id.btnSimpanAlamat);
        SwitchMaterial switchAlamatUtama = view.findViewById(R.id.switchAlamatUtama);

        Bundle args = getArguments();
        int alamatUtamaValue = 0;
        if (args != null && args.containsKey("id")) {
            isEditMode = true;
            alamatUtamaValue = args.getInt("alamat_utama", 0);
            alamatEdit = new DataAlamatPengiriman(
                args.getInt("id"),
                args.getString("nama_penerima"),
                args.getString("alamat_lengkap"),
                args.getString("kota"),
                args.getString("provinsi"),
                args.getString("kode_pos"),
                args.getString("no_hp"),
                alamatUtamaValue
            );
            etNamaPenerima.setText(alamatEdit.getNama_penerima());
            etAlamatLengkap.setText(alamatEdit.getAlamat_lengkap());
            etKodePos.setText(alamatEdit.getKode_pos());
            etNoHp.setText(alamatEdit.getNo_hp());
            selectedProvinsiName = alamatEdit.getProvinsi();
            selectedKotaName = alamatEdit.getKota();
            switchAlamatUtama.setChecked(alamatUtamaValue == 1);
        } else {
            switchAlamatUtama.setChecked(false);
        }

        loadProvinsi();

        spinnerProvinsi.setOnItemClickListener((parent, view1, position, id) -> {
            if (provinsiList.size() > 0) {
                selectedProvinsiName = provinsiList.get(position);
                selectedProvinsiId = provinsiMap.get(selectedProvinsiName);
                loadKota(selectedProvinsiId);
            }
        });

        spinnerKota.setOnItemClickListener((parent, view12, position, id) -> {
            if (kotaList.size() > 0) {
                selectedKotaName = kotaList.get(position);
                selectedKotaId = kotaMap.get(selectedKotaName);
            }
        });

        btnSimpan.setOnClickListener(v -> {
            String namaPenerima = etNamaPenerima.getText().toString().trim();
            String alamatLengkap = etAlamatLengkap.getText().toString().trim();
            String kodePos = etKodePos.getText().toString().trim();
            String noHp = etNoHp.getText().toString().trim();
            int alamatUtama = switchAlamatUtama.isChecked() ? 1 : 0;

            if (TextUtils.isEmpty(namaPenerima) || TextUtils.isEmpty(alamatLengkap) ||
                TextUtils.isEmpty(selectedKotaName) || TextUtils.isEmpty(selectedProvinsiName) ||
                TextUtils.isEmpty(kodePos) || TextUtils.isEmpty(noHp)) {
                Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences pref = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
            int idPengguna = pref.getInt("id_pengguna", -1);
            if (idPengguna == -1) {
                Toast.makeText(getContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                return;
            }

            DataAPI api = ServerAPI.getApi();
            if (isEditMode && alamatEdit != null) {
                api.updateAlamatPengiriman(
                    alamatEdit.getId(), idPengguna, namaPenerima, alamatLengkap, selectedKotaName, selectedProvinsiName, kodePos, noHp, alamatUtama
                ).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Alamat berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            }
                            dismiss();
                            if (alamatAddedListener != null) {
                                alamatAddedListener.onAlamatAdded();
                            }
                        } else {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Gagal memperbarui alamat", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                api.tambahAlamatPengiriman(
                    idPengguna, namaPenerima, alamatLengkap, selectedKotaName, selectedProvinsiName, kodePos, noHp, alamatUtama
                ).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Alamat berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            }
                            dismiss();
                            if (alamatAddedListener != null) {
                                alamatAddedListener.onAlamatAdded();
                            }
                        } else {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Gagal menambah alamat", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }

    private void loadProvinsi() {
        DataAPI api = ServerAPI.getApi();
        api.getProvinsiKota(new HashMap<String, String>() {{
            put("tipe", "provinsi");
        }}).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                provinsiList.clear();
                provinsiMap.clear();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonStr = response.body().string();
                        JSONObject json = new JSONObject(jsonStr);
                        JSONArray arr = json.getJSONArray("provinsi");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String id = obj.getString("province_id");
                            String name = obj.getString("province");
                            provinsiList.add(name);
                            provinsiMap.put(name, id);
                        }
                        if (getContext() != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, provinsiList);
                            spinnerProvinsi.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Gagal load provinsi", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal koneksi provinsi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadKota(String provinsiId) {
        DataAPI api = ServerAPI.getApi();
        api.getProvinsiKota(new HashMap<String, String>() {{
            put("tipe", "kota");
            put("province", provinsiId);
        }}).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                kotaList.clear();
                kotaMap.clear();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonStr = response.body().string();
                        JSONObject json = new JSONObject(jsonStr);
                        JSONArray arr = json.getJSONArray("kota");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String id = obj.getString("city_id");
                            String name = obj.getString("city_name");
                            kotaList.add(name);
                            kotaMap.put(name, id);
                        }
                        if (getContext() != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, kotaList);
                            spinnerKota.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Gagal load kota", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal koneksi kota", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}