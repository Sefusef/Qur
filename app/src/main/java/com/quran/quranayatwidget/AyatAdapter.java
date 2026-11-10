package com.quran.quranayatwidget;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.ViewHolder> {
	private List<Object> items;
	private OnItemClickListener listener;
	
	public interface OnItemClickListener {
		void onItemClick(Object item);
	}
	
	public AyatAdapter(List<Object> items, OnItemClickListener listener) {
		this.items = items;
		this.listener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		int layoutId = (items.get(0) instanceof QuranDataHelper.Surah)
		? R.layout.item_sura : R.layout.item_ayat;
		View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
		return new ViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Object item = items.get(position);
		Typeface amiriFont = ResourcesCompat.getFont(holder.itemView.getContext(), R.font.amiri);
		
		if (item instanceof QuranDataHelper.Surah) {
			QuranDataHelper.Surah surah = (QuranDataHelper.Surah) item;
			holder.sNum.setText(String.valueOf(surah.id));
			holder.sTranslit.setText(surah.transliteration);
			holder.sTrans.setText(surah.translation);
			holder.sArabic.setText(surah.arabicName);
			holder.sArabic.setTypeface(amiriFont);
			
			holder.itemView.setOnClickListener(v -> {
				if (listener != null) listener.onItemClick(item);
			});
			
			} else if (item instanceof QuranDataHelper.Ayat) {
			QuranDataHelper.Ayat ayat = (QuranDataHelper.Ayat) item;
			holder.aRef.setText(ayat.reference);
			holder.aArabic.setText(ayat.arabicText);
			holder.aTrans.setText(ayat.translation);
			holder.aArabic.setTypeface(amiriFont);
			
			holder.btnShare.setOnClickListener(v -> {
				String shareBody = ayat.arabicText + "\n\n" + ayat.translation + "\n\n" + ayat.reference;
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, shareBody);
				v.getContext().startActivity(Intent.createChooser(intent, "Share Ayah via"));
			});
		}
	}
	
	@Override
	public int getItemCount() { return items.size(); }
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		TextView sNum, sTranslit, sTrans, sArabic;
		TextView aRef, aArabic, aTrans;
		ImageButton btnShare;
		
		ViewHolder(View v) {
			super(v);
			sNum = v.findViewById(R.id.surah_number);
			sTranslit = v.findViewById(R.id.surah_transliteration);
			sTrans = v.findViewById(R.id.surah_translation);
			sArabic = v.findViewById(R.id.surah_arabic);
			aRef = v.findViewById(R.id.item_reference);
			aArabic = v.findViewById(R.id.item_arabic);
			aTrans = v.findViewById(R.id.item_translation);
			btnShare = v.findViewById(R.id.btn_share);
		}
	}
}