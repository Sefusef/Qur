package com.quran.quranayatwidget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdBannerAdapter extends RecyclerView.Adapter<AdBannerAdapter.AdViewHolder> {
    private final List<Integer> imageResList;
    private final String referralUrl;

    public AdBannerAdapter(List<Integer> imageResList, String referralUrl) {
        this.imageResList = imageResList;
        this.referralUrl = referralUrl;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_banner, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        int imageResId = imageResList.get(position);
        holder.adImageView.setImageResource(imageResId);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(referralUrl));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageResList.size();
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView adImageView;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adImageView = itemView.findViewById(R.id.ad_image);
        }
    }
}