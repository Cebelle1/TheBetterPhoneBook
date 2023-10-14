package com.sp.thebetterphonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;


import java.util.Locale;
import java.util.Random;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 5000;
    private int rng;
    private TextView greetings;
    private TextToSpeech TTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        greetings = findViewById(R.id.greetings);
        final Random myRandom = new Random();
        rng = myRandom.nextInt(4);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashScreen.this,MainContactList.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);


        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = TTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        TTS.setSpeechRate(0.70F);
                        speak();
                    }
                }else {
                        Log.e("TTS","Initialisation failed");
                    }
                }

    });

}

    public void speak() {
        String greeting = null;

        if (rng ==0)
        {
            greeting = "Welcome to The Better Phone Book";
        }else if (rng ==1){
            greeting = "Have you eaten?";
        } else if (rng ==2){
            greeting = "Remember to drink water today";
        } else if (rng ==3){
            greeting = "Good Day";
        }
        int speech = TTS.speak(greeting,TextToSpeech.QUEUE_FLUSH,null);
        greetings.setText(greeting);
    }

    @Override
    protected void onDestroy(){
        if (TTS != null){
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }
}
