package com.khonj;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Splash_screen extends AppCompatActivity {

    ImageView logo;

    FirebaseAuth mauth;
    DatabaseReference root;
    FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        logo = (ImageView) findViewById(R.id.image);
        mauth = FirebaseAuth.getInstance();


        if (isInternetAvailable()) {


            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser == null) {
                Intent i = new Intent(Splash_screen.this, LoginActivity.class);
                startActivity(i);
                finish();

            } else {

                root = FirebaseDatabase.getInstance().getReference();




                Animation myanim = AnimationUtils.loadAnimation(this, R.anim.fadein);
                logo.startAnimation(myanim);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(Splash_screen.this, Homepage.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);


            }

        }
        else
        {
            Intent it = new Intent(getApplicationContext(), nointernate.class);
            startActivity(it);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isInternetAvailable()){
            if(firebaseUser==null)
            {
                Intent i =new Intent(Splash_screen.this,LoginActivity.class);
                startActivity(i);
                finish();

            }
        }else
        {
            Intent it = new Intent(getApplicationContext(), nointernate.class);
            startActivity(it);
        }

    }
    public boolean isInternetAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        assert connectivityManager != null;
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;

    }
}
