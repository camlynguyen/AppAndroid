package com.example.da4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OTPVerification extends AppCompatActivity {

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s.length() > 0)
            {
                if(selectedETPostition == 0)
                {
                    selectedETPostition = 1;
                    showkeyboard(otpEt2);
                }
                else if (selectedETPostition == 1) {
                    selectedETPostition = 2;
                    showkeyboard(otpEt3);
                }
                else if (selectedETPostition == 2) {
                    selectedETPostition = 3;
                    showkeyboard(otpEt4);
                }
            }
        }
    };

    private EditText otpEt1, otpEt2, otpEt3, otpEt4;
    private TextView resendBtn;

    //true after every 60 seconds
    private boolean resendEnabled = false;

    // thời gian gửi lại mã là 60 giây
    private  int resendTime = 60;

    private int selectedETPostition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        otpEt1 = findViewById(R.id.otpET1);
        otpEt2 = findViewById(R.id.otpET2);
        otpEt3 = findViewById(R.id.otpET3);
        otpEt4 = findViewById(R.id.otpET4);

        resendBtn = findViewById(R.id.resendBtn);
        final Button verifyBtn = findViewById(R.id.verifyBtn);

        final TextView otpEmail = findViewById(R.id.otpEmail);
        final TextView otpMobile = findViewById(R.id.otpMobile);

        //getting email and mobile from register activity through internet
        final String getEmail = getIntent().getStringExtra("Email");
        final String getMobile = getIntent().getStringExtra("Mobile");

        //setting email and mobile to TextView
        otpEmail.setText(getEmail);
        otpMobile.setText(getMobile);

        otpEt1.addTextChangedListener(textWatcher);
        otpEt2.addTextChangedListener(textWatcher);
        otpEt3.addTextChangedListener(textWatcher);
        otpEt4.addTextChangedListener(textWatcher);

        //by default open keyboard at otpEt1
        showkeyboard(otpEt1);

        // start resend count down timer
        startCounDownTimer();

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resendEnabled)
                {
                    // handle your resend code here

                    //start resend cound down timer
                    startCounDownTimer();
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final  String generateOtp = otpEt1.getText().toString()+otpEt2.getText().toString()+otpEt3.getText().toString()+otpEt4.getText().toString();

                if(generateOtp.length() == 4)
                {
                    // handle your otp verification here

                }
            }
        });
    }

    private void showkeyboard(EditText otpET)
    {
        otpET.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCounDownTimer()
    {
        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime*1000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Gửi lại mã ("+(millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText("Gửi lại mã");
                resendBtn.setTextColor(getResources().getColor(R.color.White));
            }
        }.start();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_DEL)
        {
            if(selectedETPostition == 3)
            {
                selectedETPostition = 2;
                showkeyboard(otpEt3);
            }
            else if (selectedETPostition == 2)
            {
                selectedETPostition = 1;
                showkeyboard(otpEt2);

            } else if (selectedETPostition == 1)
            {
                selectedETPostition = 0;
                showkeyboard(otpEt1);
            }

            return true;
        }
        else
        {
            return super.onKeyUp(keyCode, event);
        }

    }
}