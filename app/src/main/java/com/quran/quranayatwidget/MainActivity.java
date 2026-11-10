package com.quran.quranayatwidget;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView surahRV, ayatRV;
    private HorizontalScrollView adScrollView;
    private LinearLayout adContainer;
    
    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private int scrollPosition = 0;
    
    private final String BFS_REFERRAL_URL = "https://www.beforward.jp/stocklist?refer_bfs=1362801&utm_source=sns&utm_medium=social&utm_term=BFS_Share&utm_content=sharelink&utm_campaign=supporters_profilepage";

    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (adScrollView != null && adContainer != null && adContainer.getChildCount() > 0) {
                int childWidth = adContainer.getChildAt(0).getWidth();
                if (childWidth > 0) {
                    scrollPosition += childWidth;
                    if (scrollPosition >= adContainer.getWidth()) {
                        scrollPosition = 0; // Loop back to start
                    }
                    adScrollView.smoothScrollTo(scrollPosition, 0);
                }
                autoScrollHandler.postDelayed(this, 60000); // Slide every 60 seconds
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surahRV = findViewById(R.id.surah_recycler);
        ayatRV = findViewById(R.id.ayat_recycler);
        adScrollView = findViewById(R.id.ad_scroll_view);
        adContainer = findViewById(R.id.ad_container);

        surahRV.setLayoutManager(new LinearLayoutManager(this));
        ayatRV.setLayoutManager(new LinearLayoutManager(this));

        // Native Ad Banner Images
        List<Integer> adImages = Arrays.asList(
            R.drawable.bf_ad2, // Make sure these images exist in res/drawable/
            R.drawable.bf_ad1,
            R.drawable.bf_ad3
        );

        populateAdBanner(adImages);

        new Thread(() -> {
            QuranDataHelper.loadJsonData(this);
            runOnUiThread(() -> {
                List<Object> items = new ArrayList<>(QuranDataHelper.SURAH_LIST);
                surahRV.setAdapter(new AyatAdapter(items, item -> {
                    showAyats(((QuranDataHelper.Surah) item).id);
                }));
            });
        }).start();
    }

    private void populateAdBanner(List<Integer> images) {
        adContainer.removeAllViews();
        for (int imageResId : images) {
            ImageView imageView = new ImageView(this);
            
            // Set layout parameters to match parent height and match screen width
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels, 
                LinearLayout.LayoutParams.MATCH_PARENT
            );
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(imageResId);

            // Open BE FORWARD link with your BFS ID when tapped
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BFS_REFERRAL_URL));
                startActivity(intent);
            });

            adContainer.addView(imageView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoScrollHandler.postDelayed(autoScrollRunnable, 60000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    private void showAyats(int surahId) {
        List<Object> items = new ArrayList<>(QuranDataHelper.getAyatsBySurahId(surahId));
        ayatRV.setAdapter(new AyatAdapter(items, null));
        surahRV.setVisibility(View.GONE);
        ayatRV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (ayatRV.getVisibility() == View.VISIBLE) {
            ayatRV.setVisibility(View.GONE);
            surahRV.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}