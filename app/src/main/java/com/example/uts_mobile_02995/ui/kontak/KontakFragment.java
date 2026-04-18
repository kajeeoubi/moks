package com.example.uts_mobile_02995.ui.kontak;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.uts_mobile_02995.R;
import com.google.android.material.button.MaterialButton;

public class KontakFragment extends Fragment {

    public KontakFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kontak, container, false);

        ImageView icMaps = view.findViewById(R.id.icMaps);
        ImageView icEmail = view.findViewById(R.id.icEmail);
        ImageView icWhatsapp = view.findViewById(R.id.icWhatsapp);
        ImageView icInstagram = view.findViewById(R.id.icInstagram);
        ImageView icFacebook = view.findViewById(R.id.icFacebook);
        ImageView icYoutube = view.findViewById(R.id.icYoutube);
        MaterialButton btnKembali = view.findViewById(R.id.btnKembali);

        icMaps.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.app.goo.gl/wkmX2Y5VrJ3F75yU9"));
            startActivity(intent);
        });

        icEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:moks.activewear@gmail.com"));
            startActivity(intent);
        });

        icWhatsapp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send/?phone=6281333133340&text=Halo+MOKS%2C+ada+yang+ingin+saya+tanyakan&type=phone_number&app_absent=0"));
            startActivity(intent);
        });

        icFacebook.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100086202232225"));
            startActivity(intent);
        });

        icYoutube.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/"));
            startActivity(intent);
        });

        icInstagram.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/moks.sport/"));
            startActivity(intent);
        });

        btnKembali.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }
}