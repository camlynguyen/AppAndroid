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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    //Create objiect of DatabaseRefenrence class to access firebase's Realtime Dabase
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://home-light-control-42d69-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //texEdittext
        final EditText fullname = findViewById(R.id.fullname);
        final EditText email = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final EditText conPassword = findViewById(R.id.conPassword);

        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNow);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Lấy dữ liệu từ EditText vào biến String
                final String fullnameTxt = fullname.getText().toString();
                final String emailTxt = email.getText().toString();
                final String phoneTxt = phone.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String conPasswordTxt = conPassword.getText().toString();

                //Kiểm tra xem người dùng có điền vào tất cả các trường trước khi gửi dữ liệu
                // tới firebase không
                if(fullnameTxt.isEmpty()|| emailTxt.isEmpty() || phoneTxt.isEmpty() || passwordTxt.isEmpty())
                {
                    Toast.makeText(Register.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }

                //Kiểm tra xem mật khẩu có khớp với nhau không
                //Nêu không khớp với nhau thì hiển thị thông báo
                else if (!passwordTxt.equals(conPasswordTxt)) {
                    Toast.makeText(Register.this,"Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                }

                else {
                    databaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //Kiểm tra nếu số điện thoại chưa đăng ký trước đó
                            if(snapshot.hasChild(phoneTxt)){
                                Toast.makeText(Register.this,"Số điện thoại đã được đăng ký", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //Gửi dữ liệu đến firebase
                                //sử dụng số điện thoại xác định danh tính người dùng
                                //vì vậy tất cả các thông tin kha để thuộc người sử dụng số điện thoại
                                databaseReference.child("users").child(phoneTxt).child("fullname").setValue(fullnameTxt);
                                databaseReference.child("users").child(phoneTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(phoneTxt).child("password").setValue(passwordTxt);

                                //Hiển thị thông báo đăng ký thành công
                                Toast.makeText(Register.this,"Đăng ký tài khoản thành công!",Toast.LENGTH_SHORT).show();


                                ////////////////////////////////////////////OTP
                                final  String getMobileTxt = phone.getText().toString();

                                //open OTP Verification Activity along with mobile and email
                                Intent intent = new Intent(Register.this, NhapOTP.class);

                                intent.putExtra("Phone",getMobileTxt);

                                startActivity(intent);

                                ///////////////////////////////////////////////////////////////////////////////////////////////


                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////Ban đầu
        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////
    }
}
