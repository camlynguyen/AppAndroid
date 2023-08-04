package com.example.da4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://home-light-control-42d69-default-rtdb.firebaseio.com/");

    //Khai báo
    private Button btnlogout;
    private TextView nhietdo;
    private TextView phantramden1;
    private TextView phantramden2;
    private Switch swden1;
    private Switch swden2;
    private SeekBar thanhden1;
    private SeekBar thanhden2;
    boolean sw1data_status=false;
    boolean sw2data_status=false;
    DatabaseReference nhietdodata;
    DatabaseReference den1_data;
    DatabaseReference den2_data;
    DatabaseReference sw1data;
    DatabaseReference sw2data;

    int dosang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        btnlogout=findViewById(R.id.btnlogout);
        nhietdo=findViewById(R.id.nhietdo);
        phantramden1=findViewById(R.id.phantramden1);
        phantramden2= findViewById(R.id.phantramden2);
        swden2 = findViewById(R.id.swden2);
        swden1=findViewById(R.id.swden1);
        thanhden1= findViewById(R.id.thanhden1);
        thanhden2= findViewById(R.id.thanhden2);

        swden1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                sw1data_status =! sw1data_status;//nếu thay đổi so với trạng thái ban đầu
                sw1_onoff();//Thực hiện hàm con
            }
        });

        //xử lý switch 2
        swden2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
//                if(isChecked){
//                    swden2.setChecked(true);
//                }
//                else {
//                    swden2.setChecked(false);
//                }
                sw2data_status =! sw2data_status;
                sw2_onoff();
            }
        });

        //Trượt thanh ngang đèn 2
        den2_data = FirebaseDatabase.getInstance().getReference();//Nhận dữ liệu từ firebase
        thanhden2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //i: biến thay đổi giá trị
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                den2_data.child("THANHDEN2").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dosang= snapshot.getValue(Integer.class);
                        if(i<10)//neu i<10
                        {
                            //biến den1_data sẽ truy tới mức giá trị của đèn 1 trên firebase và set giá trị thành 0
                            den2_data.child("MUC SANG DEN 2").setValue(0);
                            dosang=0;//mức sáng = 0
                            phantramden2.setText(dosang+"%");
                        } else if (i>=10 && i<20) {
                            den2_data.child("MUC SANG DEN 2").setValue(10);
                            dosang=10;
                            phantramden2.setText(dosang+"%");
                        }
                        else if (i>=20 && i<30) {
                            den2_data.child("MUC SANG DEN 2").setValue(20);
                            dosang=20;
                            phantramden2.setText(dosang+"%");
                        }
                        else if (i>=30 && i<40) {
                            den2_data.child("MUC SANG DEN 2").setValue(30);
                            dosang=30;
                            phantramden2.setText(dosang+"%");
                        }
                        else if (i>=40 && i<50) {
                            den2_data.child("MUC SANG DEN 2").setValue(40);
                            dosang=40;
                            phantramden2.setText(dosang+"%");
                        }
                        else if (i>=50 && i<60) {
                            den2_data.child("MUC SANG DEN 1").setValue(50);
                            dosang=50;
                            phantramden2.setText(dosang+"%");
                        }
                        else if (i>=60 && i<70) {
                            den2_data.child("MUC SANG DEN 2").setValue(60);
                            dosang=60;
                            phantramden2.setText(dosang+"%");
                        }
                        else if (i>=80 && i<90) {
                            den2_data.child("MUC SANG DEN 2").setValue(70);
                            dosang=70;
                            phantramden2.setText(dosang+"%");
                        }
                        else if (i>=90 && i<100) {
                            den2_data.child("MUC SANG DEN 2").setValue(90);
                            dosang=90;
                            phantramden2.setText(dosang+"%");
                        }
                        else {
                            den2_data.child("MUC SANG DEN 2").setValue(100);
                            dosang=100;
                            phantramden2.setText(dosang+"%");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Đọc nhệt độ
        // biến nhiệt độ data sẽ nhận nhiệt độ từ firebase
        nhietdodata = FirebaseDatabase.getInstance().getReference();
        //lấy giá trị từ firebase
        nhietdodata.child("ND DHT11: ").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // đẩy giá trị dht1 trên firebse xuống android chuyển thành string và gán cho biến nhietdo
                nhietdo.setText(String.valueOf(snapshot.getValue()) + " do C");
                //nhietdo.setText(snapshot.getValue().toString() + " do C");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Xử lý button logout đăng xuất
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            //Khi nhấn nút sẽ chuyển về trang
            public void onClick(View v) {
                finish();
            }
        });
    }


    //Hàm con sw2_data
    private void sw2_onoff(){
        //Nhận dữ liệu trên database
        sw2data = FirebaseDatabase.getInstance().getReference();
        if(!sw2data_status)//nếu trạng thái của nút nhấn thay đổi
        {
            sw2data.child("SWITCH 2").setValue(1);//set giá trị thành 1
        }else {
            sw2data.child("SWITCH 2").setValue(0);//ngược lại là 0
        }
    }

    private void sw1_onoff(){
        sw1data = FirebaseDatabase.getInstance().getReference();
        if(!sw1data_status){
            sw1data.child("SWITCH 1").setValue(1);
        }else {
            sw1data.child("SWITCH 1").setValue(0);
        }
    }

}
