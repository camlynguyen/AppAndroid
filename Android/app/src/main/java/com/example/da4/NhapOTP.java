package com.example.da4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.ktx.Firebase;

import java.util.concurrent.TimeUnit;

public class NhapOTP extends AppCompatActivity {


    private boolean otpSend = false;
    private String CoundtryCode = "+84";
    private String id = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhap_otp);


        final EditText mobileET = findViewById(R.id.mobileET);
        final EditText otpET = findViewById(R.id.otpET);
        final Button actionBtn = findViewById(R.id.actionButton);

        FirebaseApp.initializeApp(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(otpSend){
                    final String getOTP = otpET.getText().toString();

                    if(id.isEmpty()){
                        Toast.makeText(NhapOTP.this,"Vui lòng nhập số điện thoại!",Toast.LENGTH_SHORT).show();
                    }
                    else{

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, getOTP);
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    FirebaseUser userDetails = task.getResult().getUser();
                                    Toast.makeText(NhapOTP.this,"Gửi mã OTP thành công!", Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    Toast.makeText(NhapOTP.this, "Quá trình gửi mã đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else {
                    final String getMobile = mobileET.getText().toString();

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).
                            setPhoneNumber(CoundtryCode+ "" + getMobile).setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(NhapOTP.this).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                    Toast.makeText(NhapOTP.this, "Mã OTP đã được gửi về số điện thoại", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {

                                    Toast.makeText(NhapOTP.this,"Lỗi không gửi được mã OTP "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);

                                    otpET.setVisibility(View.VISIBLE);
//                                    actionBtn.setText("Xác minh OTP");

                                    /////////////////////////////////////////////////Chuyển sang màn hình điều

                                    actionBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //open Login activity
                                            startActivity(new Intent(NhapOTP.this, Control.class));
                                        }
                                    });
                                    //////////////////////////////////////////////////

                                    id = s;
                                    otpSend = true;

                                }
                            }).build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }

            }
        });


    }

}