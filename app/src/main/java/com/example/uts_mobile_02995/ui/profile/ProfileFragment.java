package com.example.uts_mobile_02995.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.uts_mobile_02995.LoginActivity;
import com.example.uts_mobile_02995.R;
import com.example.uts_mobile_02995.api.DataAPI;
import com.example.uts_mobile_02995.api.ServerAPI;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final String[] REQUIRED_PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    private String email;
    private TextView tvNama, tvEmail;
    private ShapeableImageView ivProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Data Pengguna", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        boolean isLoggedIn = !email.isEmpty();

        View view = inflater.inflate(isLoggedIn ? R.layout.fragment_profile : R.layout.fragment_profile_tamu, container, false);

        if (isLoggedIn) {
            initializeViews(view);
            setupClickListeners(view, sharedPreferences);
            getProfile(email);
        } else {
            setupGuestView(view);
        }

        return view;
    }

    private void initializeViews(View view) {
        tvNama = view.findViewById(R.id.tvNama);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivProfile = view.findViewById(R.id.icProfile);
        tvEmail.setText(email);
    }

    private void setupClickListeners(View view, SharedPreferences sharedPreferences) {
        ivProfile.setOnClickListener(v -> showImagePickerDialog());

        view.findViewById(R.id.btnEditProfile).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profile_to_editProfile));

        view.findViewById(R.id.btnEditSandi).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profile_to_editSandi));

        view.findViewById(R.id.btnTentangKami).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profile_to_kontak));

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> handleLogout(sharedPreferences));

        view.findViewById(R.id.btnRiwayatPesanan).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profile_to_riwayatPesanan));

        view.findViewById(R.id.btnAlamatPengiriman).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profile_to_alamatPengiriman));
    }

    private void setupGuestView(View view) {
        view.findViewById(R.id.btnMasuk).setOnClickListener(v ->
                startActivity(new Intent(getContext(), LoginActivity.class)));

        view.findViewById(R.id.btnDaftar).setOnClickListener(v ->
                startActivity(new Intent(getContext(), com.example.uts_mobile_02995.RegisterActivity.class)));
    }

    private void handleLogout(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
        Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showImagePickerDialog() {
        if (!checkPermissions()) return;

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Perbarui Gambar Profil")
                .setItems(new String[]{"Ambil gambar", "Pilih dari Galeri"}, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST) {
            boolean allGranted = true;
            for (int result : results) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                if (requestCode == CAMERA_REQUEST) {
                    openCamera();
                } else {
                    openGallery();
                }
            } else {
                Toast.makeText(getContext(), "Izin ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(REQUIRED_PERMISSIONS,
                        permission.equals(Manifest.permission.CAMERA) ? CAMERA_REQUEST : GALLERY_REQUEST);
                return false;
            }
        }
        return true;
    }

    private void openCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Gagal membuka kamera", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == android.app.Activity.RESULT_OK) {
            Uri imageUri = null;
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(requireContext(), photo);
            } else if (requestCode == GALLERY_REQUEST) {
                imageUri = data.getData();
            }

            if (imageUri != null) {
                uploadImage(imageUri);
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "profile", null);
        return Uri.parse(path);
    }

    private void uploadImage(Uri imageUri) {
        try {
            File imageFile = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", imageFile.getName(), requestFile);
            RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);

            DataAPI api = ServerAPI.getApi();
            api.updateAvatar(emailBody, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseStr = response.body().string();
                            if (responseStr.trim().startsWith("{")) {
                                JSONObject json = new JSONObject(responseStr);
                                if (json.getInt("result") == 1) {
                                    String avatarUrl = json.getString("avatar_url");
                                    loadProfileImage(avatarUrl);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProfileImage(String avatarUrl) {
        if (getContext() != null && ivProfile != null) {
            Glide.with(requireContext())
                    .load(ServerAPI.BASE_URL_AVATAR + avatarUrl)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(ivProfile);
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(requireContext(), uri, proj, null, null, null);
        android.database.Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void getProfile(String email) {
        DataAPI api = ServerAPI.getApi();
        api.getProfile(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject json = new JSONObject(response.body().string());
                        if (json.getInt("result") == 1) {
                            JSONObject data = json.getJSONObject("data");
                            tvNama.setText(data.optString("nama", ""));
                            String avatar = data.optString("avatar", "");
                            if (!avatar.isEmpty()) {
                                loadProfileImage(avatar);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }
}
