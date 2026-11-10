package com.quran.quranayatwidget;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuranDataHelper {
	public static List<Surah> SURAH_LIST = new ArrayList<>();
	private static final Map<Integer, List<Ayat>> AYAT_MAP = new LinkedHashMap<>();
	private static final List<Ayat> ALL_AYATS_LIST = new ArrayList<>();
	public static int TOTAL_AYATS_COUNT = 0;
	
	private static final String BISMILLAH = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ";
	
	public static class Surah {
		public final int id;
		public final String arabicName; // Restored
		public final String translation; // Restored
		public final String transliteration;
		
		public Surah(int id, String arabicName, String transliteration, String translation) {
			this.id = id;
			this.arabicName = arabicName;
			this.transliteration = transliteration;
			this.translation = translation;
		}
	}
	
	public static class Ayat {
		public final String arabicText, translation, reference;
		public Ayat(String arabicText, String translation, String reference) {
			this.arabicText = arabicText;
			this.translation = translation;
			this.reference = reference;
		}
	}
	
	public static void loadJsonData(Context context) {
		if (!ALL_AYATS_LIST.isEmpty()) return;
		try {
			InputStream is = context.getAssets().open("ayats.json");
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
			
			JSONArray surahsArray = new JSONArray(new String(buffer, "UTF-8"));
			
			for (int i = 0; i < surahsArray.length(); i++) {
				JSONObject surahObj = surahsArray.getJSONObject(i);
				Surah surah = new Surah(
				surahObj.getInt("id"),
				surahObj.getString("name"),
				surahObj.getString("transliteration"),
				surahObj.getString("translation")
				);
				SURAH_LIST.add(surah);
				
				List<Ayat> ayatsInSurah = new ArrayList<>();
				JSONArray versesArray = surahObj.getJSONArray("verses");
				for (int j = 0; j < versesArray.length(); j++) {
					JSONObject v = versesArray.getJSONObject(j);
					int verseId = v.getInt("id");
					String rawArabic = v.getString("text");
					
					if (j == 0 && surah.id != 1 && surah.id != 9) {
						rawArabic = BISMILLAH + "\n\n" + rawArabic;
					}
					
					String formattedArabic = rawArabic + " \u06DD" + toArabicNumber(verseId);
					Ayat ayat = new Ayat(formattedArabic, v.getString("translation"),
					surah.transliteration + " (" + surah.id + ":" + verseId + ")");
					
					ayatsInSurah.add(ayat);
					ALL_AYATS_LIST.add(ayat);
				}
				AYAT_MAP.put(surah.id, ayatsInSurah);
			}
			TOTAL_AYATS_COUNT = ALL_AYATS_LIST.size();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	// This restores the missing method for your MainActivity
	public static List<Ayat> getAyatsBySurahId(int id) {
		return AYAT_MAP.get(id);
	}
	
	private static String toArabicNumber(int number) {
		String input = String.valueOf(number);
		StringBuilder builder = new StringBuilder();
		for (char c : input.toCharArray()) builder.append((char) ((int) c + 1584));
		return builder.toString();
	}
	
	public static Ayat getAyatByIndex(int index) {
		if (ALL_AYATS_LIST.isEmpty()) return null;
		int idx = (index - 1);
		if (idx < 0 || idx >= ALL_AYATS_LIST.size()) idx = 0;
		return ALL_AYATS_LIST.get(idx);
	}
	
	public static int getNextAyatIndex(int cur) {
		if (TOTAL_AYATS_COUNT == 0) return cur;
		return (cur >= TOTAL_AYATS_COUNT) ? 1 : cur + 1;
	}
}