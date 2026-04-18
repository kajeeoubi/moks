package com.example.uts_mobile_02995;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uts_mobile_02995.ui.onBoarding.OnBoardingAdapter;
import com.example.uts_mobile_02995.ui.onBoarding.OnBoardingSlider;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private OnBoardingAdapter onBoardingAdapter;
    private LinearLayout layoutIndicators;
    private MaterialButton buttonNext;
    private MaterialButton buttonSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding);

        layoutIndicators = findViewById(R.id.layoutIndicators);
        buttonNext = findViewById(R.id.buttonNext);
        buttonSkip = findViewById(R.id.buttonSkip);

        setupSlides();

        ViewPager2 slideViewPager = findViewById(R.id.viewPager);
        slideViewPager.setAdapter(onBoardingAdapter);

        setupIndicators();
        setCurrentIndicator(0);

        slideViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                if (position == onBoardingAdapter.getItemCount() - 1) {
                    buttonNext.setText("Mulai");
                } else {
                    buttonNext.setText("Lanjut");
                }
            }
        });

        buttonNext.setOnClickListener(view -> {
            if (slideViewPager.getCurrentItem() + 1 < onBoardingAdapter.getItemCount()) {
                slideViewPager.setCurrentItem(slideViewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });

        buttonSkip.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setupSlides() {
        List<OnBoardingSlider> sliderItems = new ArrayList<>();

        OnBoardingSlider itemOne = new OnBoardingSlider();
        itemOne.setTitle("Selamat Datang di MOKS");
        itemOne.setDescription("Semangat olahraga dimulai dari perlengkapan yang tepat. Temukan semuanya di sini!");
        itemOne.setImage(R.drawable.slide1);

        OnBoardingSlider itemTwo = new OnBoardingSlider();
        itemTwo.setTitle("Lengkap & Berkualitas");
        itemTwo.setDescription("Dari jersey hingga sepatu lari, kami punya perlengkapan terbaik untuk performa maksimal.");
        itemTwo.setImage(R.drawable.slide2);

        OnBoardingSlider itemThree = new OnBoardingSlider();
        itemThree.setTitle("Belanja Mudah & Cepat");
        itemThree.setDescription("Pilih, pesan, dan siap beraksi. Belanja perlengkapan olahraga jadi praktis dari genggaman tangan.");
        itemThree.setImage(R.drawable.slide3);

        sliderItems.add(itemOne);
        sliderItems.add(itemTwo);
        sliderItems.add(itemThree);

        onBoardingAdapter = new OnBoardingAdapter(sliderItems);
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_inactive
                ));
            }
        }
    }
}
