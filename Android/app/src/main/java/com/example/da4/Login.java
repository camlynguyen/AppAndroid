package com.example.da4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://home-light-control-42d69-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText phone = findViewById(R.id.phone);
        final TextInputEditText password = findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.loginBtn);
        final TextView registerNowbtn = findViewById(R.id.registerNowBtn);

        //Qên mk OTP
        final TextView forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);




        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  String phoneTxt = phone.getText().toString();
                final  String passwordTxt = password.getText().toString();

                if(phoneTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(Login.this, "Vui lòng nhập số điện thoại hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //check số điện thoại đăng ký
                            if(snapshot.hasChild(phoneTxt))
                            {
                                //sdt đã tồn tại trên firebase
                                //Sử dụng mật khẩu từ firebase và kết nối với mật khẩu người dùng nhập

                                final String getPassword = snapshot.child(phoneTxt).child("password").getValue(String.class);
                                if(getPassword.equals(passwordTxt))
                                {
                                    Toast.makeText(Login.this,"Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                    //open MainActivity on success
                                    startActivity(new Intent(Login.this, Control.class));

                                }
                                else
                                {
                                    Toast.makeText(Login.this,"Mật khẩu không chính xác",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(Login.this,"Số điện thoại không chính xác",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


        registerNowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register activity
                startActivity(new Intent(Login.this, Register.class));
            }
        });


    }
}