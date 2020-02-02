package com.universl.realestate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
    }
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.sinhala:
                if(checked){
                    Intent sl_intent = new Intent(LanguageActivity.this,MainActivity.class);
                    sl_intent.putExtra("language","Sinhala");
                    startActivity(sl_intent);
                    finish();
                    break;
                }
            case R.id.english:
                if(checked){
                    Intent english_intent = new Intent(LanguageActivity.this,MainActivity.class);
                    english_intent.putExtra("language","English");
                    startActivity(english_intent);
                    finish();
                    break;
                }

        }
    }
}
