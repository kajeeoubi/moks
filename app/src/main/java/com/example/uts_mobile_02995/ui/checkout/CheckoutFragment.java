package com.example.uts_mobile_02995.ui.checkout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.example.uts_mobile_02995.data.DataKeranjang;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

public class CheckoutFragment extends Fragment {

    private TextView tvAlamatPengiriman;
    private TextView tvNamaPenerima;
    private TextView tvNoHp;
    private RadioGroup rgJenisPengiriman, rgMetodePembayaran;
    private TextView tvSubtotalPesanan, tvSubtotalPengiriman, tvTotalPembayaran, tvTotalBayar;
    private TextView tvEstimasiPengiriman;
    private RecyclerView rvItemCheckout;
    private List<DataKeranjang> checkoutList = new ArrayList<>();
    private double subtotalPesanan = 0;
    private double ongkosKirim = 0;
    private String email;
    private String namaPenerima;
    private String noHp;
    private String kota;
    private String provinsi;
    private String selectedEtd;
    private int idAlamat = -1;

    private CheckoutAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        email = sharedPref.getString("email", "");

        initViews(view);
        setupRecyclerView();
        loadInitialData();

        return view;
    }

    private void initViews(View view) {
        tvAlamatPengiriman = view.findViewById(R.id.tvAlamatPengiriman);
        tvNamaPenerima = view.findViewById(R.id.tvNamaPenerima);
        tvNoHp = view.findViewById(R.id.tvNoHp);
        rvItemCheckout = view.findViewById(R.id.rvItemCheckout);
        rgJenisPengiriman = view.findViewById(R.id.rgJenisPengiriman);
        rgMetodePembayaran = view.findViewById(R.id.rgMetodePembayaran);
        tvSubtotalPesanan = view.findViewById(R.id.tvSubtotalPesanan);
        tvSubtotalPengiriman = view.findViewById(R.id.tvSubtotalPengiriman);
        tvTotalPembayaran = view.findViewById(R.id.tvTotalPembayaran);
        tvTotalBayar = view.findViewById(R.id.tvSubtotal);
        tvEstimasiPengiriman = view.findViewById(R.id.tvEstimasiPengiriman);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (isAdded()) requireActivity().onBackPressed();
        });

        MaterialButton btnGantiAlamat = view.findViewById(R.id.btnGantiAlamat);
        btnGantiAlamat.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_checkoutFragment_to_alamatPengirimanFragment);
        });

        rgJenisPengiriman.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = group.findViewById(checkedId);
            if (selectedButton != null) {
                String etd = (String) selectedButton.getTag(R.id.tag_etd);
                calculateShippingCost(etd);
            }
        });
        MaterialButton btnPesan = view.findViewById(R.id.btnPesan);
        btnPesan.setOnClickListener(v -> submitCheckout());
    }

    private void setupRecyclerView() {
        rvItemCheckout.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CheckoutAdapter(requireContext(), checkoutList, this::loadCartItems);
        rvItemCheckout.setAdapter(adapter);
    }

    private void loadInitialData() {
        // Ambil id_pengguna dari shared preferences
        SharedPreferences sharedPref = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        int idPengguna = sharedPref.getInt("id_pengguna", -1);
        if (idPengguna == -1) {
            Toast.makeText(getContext(), "Silakan login dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load alamat pengiriman
        DataCheckout.loadAlamatPengiriman(requireContext(), idPengguna, new DataCheckout.OnAlamatPengirimanLoadedListener() {
            @Override
            public void onAlamatPengirimanLoaded(int idAlamatDb, String name, String phone, String address, String city, String province, String zipCode) {
                if (!isAdded()) return;
                idAlamat = idAlamatDb;
                kota = city;
                provinsi = province;
                namaPenerima = name;
                noHp = phone;
                String fullAddress = String.format("%s\n%s, %s %s",
                        address, kota, provinsi, zipCode);

                if (tvNamaPenerima != null) tvNamaPenerima.setText(name);
                if (tvNoHp != null) tvNoHp.setText(phone);

                tvAlamatPengiriman.setText(fullAddress);

                loadCartItems();
            }

            @Override
            public void onAlamatPengirimanLoadFailed(String message) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        DataCheckout.loadCartItems(requireContext(), new DataCheckout.OnCartItemsLoadedListener() {
            @Override
            public void onCartItemsLoaded(List<DataKeranjang> cartItems, double subtotal) {
                if (!isAdded()) return;
                checkoutList.clear();
                checkoutList.addAll(cartItems);
                subtotalPesanan = subtotal;

                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                tvSubtotalPesanan.setText(formatRupiah.format(subtotalPesanan));
                calculateTotalPayment();
                adapter.notifyDataSetChanged();

                // Setelah cart items, hitung ongkos kirim
                calculateAndLoadShippingOptions();
            }

            @Override
            public void onCartItemsLoadFailed(String message) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAndLoadShippingOptions() {
        if (!isAdded()) return;
        rgJenisPengiriman.removeAllViews();

        DataCheckout.calculateAndLoadShipping(requireContext(), idAlamat, checkoutList, new DataCheckout.OnShippingOptionsLoadedListener() {
            @Override
            public void onShippingOptionsLoaded(JSONArray costs) {
                if (!isAdded()) return;
                setupShippingOptions(costs);
            }

            @Override
            public void onShippingOptionsLoadFailed(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupShippingOptions(JSONArray costs) {
        if (!isAdded()) return;

        requireActivity().runOnUiThread(() -> {
            rgJenisPengiriman.removeAllViews();
            try {
                for (int i = 0; i < costs.length(); i++) {
                    JSONObject service = costs.getJSONObject(i);
                    String courier = service.getString("courier");
                    String serviceName = service.getString("service");
                    JSONObject cost = service.getJSONArray("cost").getJSONObject(0);
                    String etd = cost.getString("etd").replace(" HARI", "");
                    int value = cost.getInt("value");

                    String btnText = String.format("%s - %s (%s hari)",
                            courier, serviceName, etd);

                    RadioButton rb = new RadioButton(getContext());
                    rb.setText(btnText);
                    rb.setTag(value);
                    rb.setTag(R.id.tag_etd, etd);
                    rb.setTag(R.id.tag_courier, courier.toLowerCase());
                    rb.setTag(R.id.tag_service, serviceName);
                    rb.setPadding(36, 24, 36, 24);

                    rgJenisPengiriman.addView(rb);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void calculateShippingCost(String etd) {
        RadioButton selectedButton = rgJenisPengiriman.findViewById(rgJenisPengiriman.getCheckedRadioButtonId());
        if (selectedButton != null) {
            ongkosKirim = (int) selectedButton.getTag();
            selectedEtd = etd;
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            tvSubtotalPengiriman.setText(formatRupiah.format(ongkosKirim));
            tvEstimasiPengiriman.setText(String.format("%s hari", selectedEtd));
            calculateTotalPayment();
        }
    }

    private void calculateTotalPayment() {
        double total = subtotalPesanan + ongkosKirim;
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        tvTotalPembayaran.setText(formatRupiah.format(total));
        tvTotalBayar.setText(formatRupiah.format(total));
    }

    private void submitCheckout() {
        SharedPreferences pref = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        int idPengguna = pref.getInt("id_pengguna", -1);
        if (idPengguna == -1) {
            Toast.makeText(getContext(), "Silakan login dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idAlamat == -1) {
            Toast.makeText(getContext(), "Pilih alamat pengiriman terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String alamatLengkap = tvAlamatPengiriman.getText().toString().trim();

        if (namaPenerima == null || namaPenerima.isEmpty() ||
                noHp == null || noHp.isEmpty() ||
                alamatLengkap.isEmpty()) {
            Toast.makeText(getContext(), "Data pengiriman belum lengkap", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = rgJenisPengiriman.findViewById(rgJenisPengiriman.getCheckedRadioButtonId());
        if (rb == null) {
            Toast.makeText(getContext(), "Pilih metode pengiriman", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil kurir dan layanan dari tag, bukan dari parsing string
        String kurir = (String) rb.getTag(R.id.tag_courier);
        String layananKurir = (String) rb.getTag(R.id.tag_service);

        RadioButton rbCOD = rgMetodePembayaran.findViewById(R.id.rbCOD);
        String metodeBayar = rbCOD.isChecked() ? "cod" : "transfer";

        double totalBayar = subtotalPesanan + ongkosKirim;

        // Siapkan data produk dalam bentuk JSON
        Gson gson = new Gson();
        List<Object> produkList = new ArrayList<>();
        for (DataKeranjang item : checkoutList) {
            produkList.add(new ProdukCheckout(item.getId(), item.getJumlah(), item.getHarga(), item.getSubtotal()));
        }
        String produkJson = gson.toJson(produkList);

        DataAPI api = ServerAPI.getApi();
        api.checkout(
                idPengguna,
                idAlamat,
                kurir,
                layananKurir,
                ongkosKirim,
                subtotalPesanan,
                totalBayar,
                metodeBayar,
                produkJson
        ).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            Toast.makeText(getContext(), "Pesanan berhasil dibuat", Toast.LENGTH_SHORT).show();

                            BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
                            SharedPreferences prefKeranjang = requireContext().getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE);
                            prefKeranjang.edit().putString("keranjang", "[]").apply();
                            com.example.uts_mobile_02995.utils.BadgeUtils.updateKeranjangBadge(requireContext(), navView);

                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Gagal membuat pesanan", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ProdukCheckout {
        int id_produk;
        int jumlah;
        double harga;
        double subtotal;
        ProdukCheckout(int id_produk, int jumlah, double harga, double subtotal) {
            this.id_produk = id_produk;
            this.jumlah = jumlah;
            this.harga = harga;
            this.subtotal = subtotal;
        }
    }

}