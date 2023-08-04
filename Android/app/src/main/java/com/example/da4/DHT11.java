package com.example.da4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DHT11 extends AppCompatActivity {

    TextView txtper;
    ProgressBar Prog;
//    Button btnCal;
    TextView txtper2;
    ProgressBar Prog2;
    Button btnOutDHT;

    DatabaseReference nhietdodata;
    DatabaseReference doamdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dht11);

        txtper = findViewById(R.id.txtper);
        Prog = findViewById(R.id.Prog);
        txtper2 = findViewById(R.id.txtper2);
        Prog2 = findViewById(R.id.Prog2);

        btnOutDHT = findViewById(R.id.btnOutDHT);

        Prog.setMax(100);
        Prog2.setMax(100);//set giá trị max
        Prog.setProgress(0);
        Prog2.setProgress(0);//set giá trị mặc định



//        btnCal = findViewById(R.id.btnCal);

//        btnCal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                float At = Integer.parseInt(edtNumb.getText().toString());
//                float Tl = Integer.parseInt(edtTotal.getText().toString());
//
//                float percen = (At/Tl)*100;
//                txtper.setText(String.valueOf(String.format("%.2f", percen)+"%"));
//                Prog.setProgress((int) percen);

                nhietdodata = FirebaseDatabase.getInstance().getReference();

                nhietdodata.child("cambiendht/nhietdo").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // đẩy giá trị dht1 trên firebse xuống android chuyển thành string và gán cho biến nhietdo
                        //nhietdo.setText(snapshot.getValue().toString() + " độ C");
                        float Nhietdo = snapshot.getValue(Float.class);
                        Prog.setProgress((int) Nhietdo);
                        txtper.setText(String.valueOf(Nhietdo +  " \u2103"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //lấy giá trị từ firebase
                doamdata = FirebaseDatabase.getInstance().getReference();
                doamdata.child("cambiendht/doam").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // đẩy giá trị dht1 trên firebse xuống android chuyển thành string và gán cho biến nhietdo
                        //nhietdo.setText(snapshot.getValue().toString() + " độ C");
                        float Doam = snapshot.getValue(Float.class);
                        Prog2.setProgress((int) Doam);
                        txtper2.setText(String.valueOf(Doam + "%"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                btnOutDHT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //Khi nhấn nút sẽ chuyển về trang
                    public void onClick(View v) {

                        startActivity(new Intent(DHT11.this, Control.class));
        //                finish();
                    }
                });
            }

    //Xử lý button logout đăng xuất

//        });
//
//    }
}

