package com.khonj;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {


    EditText phone_edit,otp_edit;
    FrameLayout send;
    TextView resend;

    String phone;
    TextView otp_tv;

    FirebaseAuth mAuth;
    View view;
    ProgressDialog pbar;
    private int checker=0; //check weather otp request is sent or not
    private String verificationid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //fron xml
        phone_edit=findViewById(R.id.phone);
        otp_edit=findViewById(R.id.otp);
        resend=findViewById(R.id.resend);
        send=findViewById(R.id.send);
        otp_tv=findViewById(R.id.otp_tv);
        view=findViewById(R.id.view);

        pbar=new ProgressDialog(this,R.style.progress_style);
        pbar.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();



        //login
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone=phone_edit.getText().toString();
                if(!isInternetAvailable())
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Connection Not Available")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.app_name)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
                else
                {
                    if(checker==0) {

                        if (phone.length() != 10) {
                            phone_edit.requestFocus();
                            phone_edit.setError("Enter valid number");
                        } else {
                            phone = "+91" + phone;
                            pbar.setMessage("Sending OTP..");
                            pbar.show();
                            sendverificationcode(phone);

                        }
                    }
                    else
                    {
                        pbar.setMessage("Validating User..");
                        pbar.show();
                        verifycode(otp_edit.getText().toString());
                    }

                }



            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone=phone_edit.getText().toString();
                if(phone.length()==10)
                {

                if (isInternetAvailable()) {
                    checker = 0;
                    pbar.setMessage("Sending OTP..");
                    pbar.show();
                    sendverificationcode("+91"+phone);

                } else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Connection Not Available")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.app_name)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();

                }
                }
                else
                {
                    phone_edit.requestFocus();
                    phone_edit.setError("Enter valid number");
                }
            }
        });

    }

    //sending verification code
    private void sendverificationcode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mcallBack

        );
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mcallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull @NotNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            checker = 1;
            Animation myanim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.right_left);

            otp_tv.setVisibility(View.VISIBLE);
            otp_edit.setVisibility(View.VISIBLE);
            resend.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            otp_edit.startAnimation(myanim);
            resend.startAnimation(myanim);
            otp_tv.startAnimation(myanim);
            view.startAnimation(myanim);
            Toast.makeText(LoginActivity.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
            phone_edit.setClickable(false);
            verificationid = s;
            pbar.dismiss();
            //   mResendToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code;
            code = phoneAuthCredential.getSmsCode();
            if (code != null) {

                pbar.setMessage("please wait..");
                pbar.show();
                Toast.makeText(LoginActivity.this, "OTP Verification Completed", Toast.LENGTH_SHORT).show();
                otp_edit.setText(code);
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            pbar.dismiss();
            checker = 0;
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }};
    private void verifycode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
            signinwithcredential(credential);
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            otp_edit.setError("Invalid otp");
            otp_edit.requestFocus();
            pbar.dismiss();
        }
    }

    private void signinwithcredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pbar.dismiss();
                            Intent it=new Intent(getApplicationContext(),Homepage.class);
                            startActivity(it);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbar.dismiss();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }



    //internet check method
    public boolean isInternetAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        assert connectivityManager != null;
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;


    }

}
