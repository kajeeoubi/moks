package com.example.uts_mobile_02995.ui.onBoarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_mobile_02995.R;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.SlideViewHolder> {

    private final List<OnBoardingSlider> onBoardingSliders;

    public OnBoardingAdapter(List<OnBoardingSlider> onBoardingSliders) {
        this.onBoardingSliders = onBoardingSliders;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_boarding, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        OnBoardingSlider item = onBoardingSliders.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.imgSlide.setImageResource(item.getImage());
    }

    @Override
    public int getItemCount() {
        return onBoardingSliders.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlide;
        TextView tvTitle, tvDescription;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSlide = itemView.findViewById(R.id.imgSlide);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
