package com.subha.sanskritisafar;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class temp extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);

        Button testEnglishButton = findViewById(R.id.btn_test_english);
        Button testSpanishButton = findViewById(R.id.btn_test_spanish);
        Button testFrenchButton = findViewById(R.id.btn_test_french);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Log.d("TTS", "TextToSpeech initialized successfully.");
            } else {
                Log.e("TTS", "Initialization failed.");
            }
        });

        String eng = "Namaste! The Qutub Minar, a soaring 73-meter tower in Delhi, stands as a symbol of victory and architectural brilliance.  Built in the early 13th century by Qutb-ud-din Aibak, founder of the Delhi Sultanate, it marked the beginning of Muslim rule in India. Its red sandstone and marble structure, adorned with intricate carvings and verses from the Quran, showcases the skills of the era's artisans.  The Minar's construction continued under Iltutmish and Firoz Shah Tughlaq, resulting in a blend of architectural styles.  A UNESCO World Heritage Site, it whispers tales of a pivotal moment in Indian history, a testament to the passage of time and changing empires.";
        String beng = "নমস্তে!দিল্লির একটি 73৩ মিটার টাওয়ার, কুতুব মিনার বিজয় এবং স্থাপত্যের উজ্জ্বলতার প্রতীক হিসাবে দাঁড়িয়ে আছে।দিল্লি সুলতানেটের প্রতিষ্ঠাতা কুতব-উদ-দীন আইবাক ১৩ শতকের গোড়ার দিকে নির্মিত, এটি ভারতে মুসলিম শাসনের সূচনা হিসাবে চিহ্নিত হয়েছিল।এর লাল বেলেপাথর এবং মার্বেল কাঠামো, কুরআনের জটিল খোদাই এবং আয়াত দিয়ে সজ্জিত, যুগের কারিগরদের দক্ষতা প্রদর্শন করে।মিনারের নির্মাণটি ইল্টুতমিশ এবং ফিরোজ শাহ তুঘলকের অধীনে অব্যাহত ছিল, যার ফলে স্থাপত্য শৈলীর মিশ্রণ ঘটে।ইউনেস্কো ওয়ার্ল্ড হেরিটেজ সাইট, এটি ভারতীয় ইতিহাসের এক গুরুত্বপূর্ণ মুহুর্তের গল্পগুলি ফিসফিস করে, সময়ের সাথে সাথে এবং সাম্রাজ্যের পরিবর্তনের একটি প্রমাণ।";
        String hindi = "नमस्ते! कुतुब मीनार, दिल्ली में स्थित एक 73 मीटर ऊँचा टॉवर, विजय और वास्तुशिल्प कौशल का प्रतीक है। इसे 13वीं शताब्दी की शुरुआत में दिल्ली सल्तनत के संस्थापक कुतुब-उद-दीन ऐबक द्वारा बनवाया गया था, जो भारत में मुस्लिम शासन की शुरुआत का प्रतीक था। लाल बलुआ पत्थर और संगमरमर से बनी इस मीनार पर जटिल नक्काशी और कुरान की आयतें उकेरी गई हैं, जो उस समय के कारीगरों की उत्कृष्ट कला को दर्शाती हैं। इसका निर्माण इल्तुतमिश और फिरोज शाह तुगलक के समय में जारी रहा, जिससे इसमें वास्तुशिल्पीय शैलियों का अद्भुत मिश्रण दिखाई देता है। एक यूनेस्को विश्व धरोहर स्थल, यह मीनार भारतीय इतिहास के एक महत्वपूर्ण काल की कहानियों को बयां करती है और समय और बदलते साम्राज्यों का प्रमाण है।";

        // Set button click listeners to test different languages
        testEnglishButton.setOnClickListener(v -> testTTS(eng, "en"));
        testSpanishButton.setOnClickListener(v -> testTTS(beng, "bn"));
        testFrenchButton.setOnClickListener(v -> testTTS(hindi, "hi"));
    }

    private void testTTS(String text, String languageCode) {
        if (textToSpeech != null) {
            Locale locale = new Locale(languageCode);
            int result = textToSpeech.setLanguage(locale);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported: " + languageCode);
                return;
            }

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e("TTS", "TextToSpeech not initialized.");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}

