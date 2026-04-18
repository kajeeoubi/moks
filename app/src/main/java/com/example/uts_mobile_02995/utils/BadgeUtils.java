package com.example.uts_mobile_02995.utils;

import android.content.Context;

import com.example.uts_mobile_02995.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;

public class BadgeUtils {

    public static void updateKeranjangBadge(Context context, BottomNavigationView navView) {
        // Ambil jumlah item dari SharedPreferences lokal
        int itemCount = 0;
        try {
            String keranjangJson = context.getSharedPreferences("Keranjang Lokal", Context.MODE_PRIVATE)
                    .getString("keranjang", "[]");
            JSONArray arr = new JSONArray(keranjangJson);
            itemCount = arr.length();
        } catch (Exception ignored) {}

        BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_keranjang);
        badge.setBackgroundColor(context.getColor(R.color.icon_red));
        badge.setVisible(itemCount > 0);
        if (itemCount > 0) badge.setNumber(itemCount);
    }
}