#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

#include <WiFiManager.h>        
#include <EEPROM.h>
#include "RTClib.h"
#include "DHT.h"

#include <FirebaseESP8266.h>

// thư viện để lấy thời gian và chuyển đổi ra ngày tháng từ dữ liệu trên google 
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <time.h>

const long muigio = 3600*7; // GMT+7 MÚI GIỜ CỦA VIỆT NAM 
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org",muigio);// Define NTP Client to get time

// thiet lập firebase
#define FIREBASE_HOST "https://home-light-control-42d69-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "oppJiBSMhXj3mrrdih9kTvjFJENNyRNqANpMnouC"
 
//tao biến tên là firebaseData để lưu các giá trị đọc và ghi lên firebase
FirebaseData firebaseData ;
FirebaseJson json;  

#define DHTPIN D5
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

RTC_DS1307 DS1307_RTC;

// Khai báo địa chỉ I2C của màn hình LCD và kích thước của nó
#define LCD_ADDRESS 0x27  // 0x27 , 0x3F ,0x38 , 0x20
#define LCD_COLUMNS 16
#define LCD_ROWS 2

// Khởi tạo đối tượng LiquidCrystal_I2C với địa chỉ I2C và kích thước
LiquidCrystal_I2C lcd(LCD_ADDRESS, LCD_COLUMNS, LCD_ROWS);
//
int gio , phut , giay , nam , thang , ngay , gio1 , phut1, giay1 ,ngay1 , thang1;
int pwmden , pwmden1 , pwmden2 , pwmden22;

#define PWMDEN D6
#define PWMDEN1 D7
//chân còi
#define BUZZER_PIN D8


//
void setup() {
  pinMode(D3 , INPUT);

  pinMode(PWMDEN , OUTPUT);
  pinMode(PWMDEN1 , OUTPUT);
  pinMode(BUZZER_PIN , OUTPUT);
 
  digitalWrite(PWMDEN , LOW);
  digitalWrite(PWMDEN1 , LOW);
  digitalWrite(BUZZER_PIN , LOW);
  // cài tần số pwm
  analogWriteFreq(25000);
 
  // Khởi động giao tiếp I2C
    Wire.begin();
  // Khởi tạo DHT11
   dht.begin();
  // Khởi động ds1307
  DS1307_RTC.begin();
  // Khởi tạo màn hình LCD
  lcd.begin();
  // Bật đèn nền cho màn hình LCD
  lcd.backlight();lcd.clear(); 
  tone(BUZZER_PIN, 5);
  delay(1000);
  noTone(BUZZER_PIN);
  lcd.setCursor(0, 0);
  lcd.print("  KET NOI WIFI  ");
  lcd.setCursor(0, 1);
  lcd.print("****************");
  delay(2000);
  EEPROM.begin(512);WiFiManager wifiManager;
   //wifiManager.resetSettings();
  wifiManager.autoConnect("DO_AN_LY","123456789");
  kiemtra();delay(3000);lcd.clear();
}
   
/////////////////////////////////////////////////////////////////////////////
void loop() {

  dhtds1307();
  thietlapwifi();
  if(WiFi.isConnected()){ 
    noTone(BUZZER_PIN);
    writefirebase();
    readfirebase();

  }else{
   
    lcd.clear(); 
    lcd.setCursor(0, 0);
    lcd.print("MAT KET NOI WIFI");
    lcd.setCursor(0, 1);
    lcd.print("!!!!!!!!!!!!!!!!");
    tone(BUZZER_PIN, 10);
    delay(1000);
   
    digitalWrite(PWMDEN , LOW);
    digitalWrite(PWMDEN1 , LOW);
    lcd.clear();
  }

}
////////////////////////////////////////////////////////////////////////////
// ham thiết lập wifi 
void thietlapwifi(){

 if(digitalRead(D3)== LOW){
  tone(BUZZER_PIN, 2); 
  delay(1000);
  noTone(BUZZER_PIN);
  
  digitalWrite(PWMDEN , LOW);
  digitalWrite(PWMDEN1 , LOW);
  lcd.clear(); lcd.setCursor(1, 0);lcd.print("THIET LAP WIFI");
 //Khởi động eeprom
  EEPROM.begin(512);WiFiManager wifiManager;
  wifiManager.resetSettings();
  wifiManager.autoConnect("DO_AN_LY","123456789");
  //wifiManager.autoConnect();
  kiemtra();
  delay(3000);
  lcd.clear();
  }

}
// ham đọc thời gian và dht11 
void dhtds1307(){
 float h = dht.readHumidity();
 float t = dht.readTemperature();
  lcd.setCursor(0, 1);
  lcd.print("T:");
  lcd.printf("%2.1f",t);
  lcd.print("C");

  lcd.setCursor(9, 1);
  lcd.print("H:");
  lcd.printf("%2.1f",h);
  lcd.print("%");
  
  DateTime now = DS1307_RTC.now();
  gio1 = now.hour();
  phut1= now.minute();
  giay1= now.second();

  thang1 = now.month();
  ngay1  = now.day();

  lcd.setCursor(0, 0);
  lcd.print("TIME:");
  lcd.printf("%2d:",gio1);
  lcd.printf("%2d",phut1);
 // lcd.printf("%2d" ,giay1);
  lcd.print("|");
  lcd.printf("%2d/",ngay1);
  lcd.printf("%2d",thang1);

}
/// dọc dữ liệu firebase 
void readfirebase(){
   // đọc trạng thái nút đèn 1
   Firebase.getInt(firebaseData, "SWT1");
   int trangthaiden1 = firebaseData.intData();
   delay(10);
   // nếu đèn 1 bật 
   if(trangthaiden1 == 0){
       // tiến hành đọc mức sáng của đèn 
         Firebase.getInt(firebaseData, "MUCSANGDEN1");
          pwmden = firebaseData.intData();
         delay(10);
         // hàm chuyển từ mức sáng 0-100 ra pwm từ 0-255
          pwmden1 = map(pwmden,0,100,0,255);
         delay(10);
         analogWrite(PWMDEN, pwmden1);
         delay(10);
    }
    // nếu đèn 1 tắt thì tắt đèn 
     if ( trangthaiden1 == 1 ){ analogWrite(PWMDEN,0);} 
//////////////////////////////////////////////////////////

      // đọc trạng thái nút đèn 2
    Firebase.getInt(firebaseData, "SWT2");
    int trangthaiden2= firebaseData.intData();
    delay(10);
    // nếu đèn 2 đang bật 
    if ( trangthaiden2 == 0 ){

        Firebase.getInt(firebaseData, "MUCSANGDEN2");
          pwmden2 = firebaseData.intData();
         delay(10);
         // hàm chuyển từ mức sáng 0-100 ra pwm từ 0-255
          pwmden22 = map(pwmden2,0,100,0,255);
         delay(10);
         analogWrite(PWMDEN1,pwmden22);
         delay(10);
    }
    // nếu đèn 1 tắt thì tắt đèn 
     if(trangthaiden2 == 1){analogWrite(PWMDEN1,0);}

}

   
//// ghi dữ liệu lên firebase 
void writefirebase(){
  float humi = dht.readHumidity();
  delay(10);
  float temp = dht.readTemperature();
  delay(10);
   Firebase.setFloat(firebaseData, "cambiendht/nhietdo",temp);
   delay(10);
   Firebase.setFloat(firebaseData, "cambiendht/doam",humi);
   delay(10);
}
// ham kiểm tra kết nối wifi
void kiemtra(){
 // khi kết nối tới wifi thành công thì cập nhập dữ liệu ngày giờ 
   if (WiFi.isConnected()) {
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("  KET NOI WIFI  ");
      lcd.setCursor(0, 1);
      lcd.print("   THANH CONG   ");
      delay(3000);lcd.clear();

      // ket noi firebase
      lcd.setCursor(0, 0);
      lcd.print("KET NOI FIREBASE");
      Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
      delay(3000); lcd.clear();

      lcd.setCursor(0, 0);
      lcd.print("CAP NHAP DU LIEU");
      lcd.setCursor(0, 1);
      lcd.print("   THOI GIAN    ");
      //lấy thời gian và dữ liệu ngày trên web 
      timeClient.begin();
      timeClient.update();
      gio = timeClient.getHours();
      phut =timeClient.getMinutes();
      giay =timeClient.getSeconds();

      //lay gia tri ngay thang nam duoi dang  Epoch time,và dung thu vien time để chuyển 
      time_t epochTime = timeClient.getEpochTime();
      struct tm * timeinfo;
      timeinfo = localtime(&epochTime);
      nam=timeinfo->tm_year + 1900;
      thang=timeinfo->tm_mon + 1;
      ngay=timeinfo->tm_mday;
      DS1307_RTC.adjust(DateTime(nam, thang, ngay, gio, phut, giay));
      delay(2000);
   }
  }