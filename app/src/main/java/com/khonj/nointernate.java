package com.khonj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class nointernate extends AppCompatActivity {
    TextView internet;
    Animation fadeinanimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nointernate);
        Button back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(nointernate.this,Splash_screen.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });
        internet=(TextView)findViewById(R.id.internet);
        fadeinanimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
        internet.setVisibility(View.VISIBLE);
        internet.setAnimation(fadeinanimation);

    }
    @Override
    public void onBackPressed() {

        Intent it=new Intent(nointernate.this,Splash_screen.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);


    }
}
