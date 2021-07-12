#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WebServer.h>
#include <EEPROM.h>
#include <SPI.h>
#include <Wire.h>
#include <MFRC522.h>
#include <LiquidCrystal_I2C.h>
#include <FirebaseArduino.h>
#include <SoftwareSerial.h>
//Variables
int i = 0;
int statusCode;
SoftwareSerial s(13,15);
//Servo servo1;
LiquidCrystal_I2C lcd (0x27,16,2);
#define FIREBASE_HOST  "ledcontrol-2f117-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH  "Z1TYn6WohBwleZNCp8y9Kioj3nMWWHx9wHE59XfF"
constexpr uint8_t RST_PIN = D3;     // Configurable, see typical pin layout above
constexpr uint8_t SS_PIN = D4;     // Configurable, see typical pin layout above
MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class
MFRC522::MIFARE_Key key;
String tag;
const char* ssid = "Nhan";
const char* passphrase = "nhannguyen";
String st;
String content;
//Function Decalration
bool testWifi(void);
void launchWeb(void);
void setupAP(void);
//Establishing Local server at port 80 whenever required
ESP8266WebServer server(80);
void setup()
{
  Wire.begin(D2,D1);
  //servo.attach(16);
  //servo1.attach(15);
  lcd.init();
  lcd.backlight();
  lcd.clear();
  s.begin(9600);
  Serial.begin(9600); //Initialising if(DEBUG)Serial Monitor
  Serial.println();
  WiFi.mode(WIFI_AP);
  lcd.setCursor(0,0);
  lcd.print("Disconnecting");
  delay(500);
  Serial.println("Disconnecting current wifi connection");
  WiFi.disconnect();
  EEPROM.begin(512); //Initialasing EEPROM
  delay(10);
  pinMode(2, OUTPUT);
  pinMode(0, OUTPUT);
  //pinMode(8, INPUT);
  Serial.println();
  Serial.println();
  Serial.println("Startup");
  
  lcd.clear();
  lcd.print("Startup");
  delay(500);
  //---------------------------------------- Read eeprom for ssid and pass
  Serial.println("Reading EEPROM ssid");
  lcd.clear();
  lcd.print("Getting data");
  delay(500);
  String esid;
  for (int i = 0; i < 32; ++i)
  {
    esid += char(EEPROM.read(i));
  }
  Serial.println();
  Serial.print("SSID: ");
  Serial.println(esid);
  Serial.println("Reading EEPROM pass");
  String epass = "";
  for (int i = 32; i < 96; ++i)
  {
    epass += char(EEPROM.read(i));
  }
  Serial.print("PASS: ");
  //Serial.println(epass);
  WiFi.begin(esid.c_str(), epass.c_str());
  Firebase.begin(FIREBASE_HOST,FIREBASE_AUTH);
  SPI.begin(); // Init SPI bus
  rfid.PCD_Init(); // Init MFRC522
  if (testWifi())
  {
    Serial.println("Succesfully Connected!!!");
    lcd.clear();
    lcd.print("Successfully");
    delay(2000);
    return;
  }
  else
  {
    Serial.println("Turning the HotSpot On");
    lcd.print("Failed");
    lcd.setCursor(0,1);
    lcd.clear();
    lcd.print("Hotspot on");
    delay(500);
    launchWeb();
    setupAP();// Setup HotSpot
  }
  Serial.println();
  Serial.println("Waiting.");
  while ((WiFi.status() != WL_CONNECTED))
  {
    Serial.print(".");
    delay(100);
    server.handleClient();
  }
  
}
void loop() {
  String isAdd = "";
  String opening = "";
  String isChangingPass = "";
  isChangingPass = Firebase.getString("isChange");
  isAdd = Firebase.getString("isAdding");
  opening = Firebase.getString("open");
  if (isChangingPass == "1")
  {
    
    Serial.println("*"+Firebase.getString("newPass")+ "*");
    Firebase.setString("isChange","0");
  }
  if (opening == "1")
  {
    lcd.setCursor(0,0);
    lcd.print("Acccess granted");
    s.write("1");
    Serial.println("1");
    delay(1000);
    Firebase.setString("open","0");
  }
  if (isAdd == "1")
  {
    lcd.setCursor(0,0);
    lcd.print("Insert Card");
    if ( ! rfid.PICC_IsNewCardPresent())
    return;
    if (rfid.PICC_ReadCardSerial()) {
    for (byte i = 0; i < 4; i++) {
      tag += rfid.uid.uidByte[i];
    }
    String nname = "";
    nname =  Firebase.getString("newname");
    Firebase.setString(tag,nname);
    Firebase.setString("isAdding","0");
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("Successfully");
    tag = "";
  }
  }
 
  //digitalWrite(0,HIGH);
  //byte val = digitalRead(D8);
    lcd.clear();
    lcd.print("Working!");
    
    String personStatus = "";
    if ( ! rfid.PICC_IsNewCardPresent())
    return;
  if (rfid.PICC_ReadCardSerial()) {
    for (byte i = 0; i < 4; i++) {
      tag += rfid.uid.uidByte[i];
    }
    //Serial.println(tag);
    personStatus = Firebase.getString(tag);
    if (personStatus.length()!=0) {
      s.write("1");
      Serial.println("1");
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print(personStatus);
      lcd.setCursor(0,1);
      lcd.print("Da vao nha");
      //servo1.write(0);
      delay(1500);
      Firebase.setString("goHome",personStatus);
    } else {
      Serial.println("0");
      lcd.clear();
      lcd.print("Access Denied!");
      delay(2000);
    }
    tag = "";
    rfid.PICC_HaltA();
    rfid.PCD_StopCrypto1();
  }
  
}
//----------------------------------------------- Fuctions used for WiFi credentials saving and connecting to it which you do not need to change
bool testWifi(void)
{
  int c = 0;
  Serial.println("Waiting for Wifi to connect");
  while ( c < 20 ) {
    if (WiFi.status() == WL_CONNECTED)
    {
      return true;
    }
    delay(500);
    Serial.print("*");
    c++;
  }
  Serial.println("");
  Serial.println("Connect timed out, opening AP");
  return false;
}
void launchWeb()
{
  Serial.println("");
  if (WiFi.status() == WL_CONNECTED)
    Serial.println("WiFi connected");
  //Serial.print("Local IP: ");
  //Serial.println(WiFi.localIP());
  //Serial.print("SoftAP IP: ");
  //Serial.println(WiFi.softAPIP());
  createWebServer();
  // Start the server
  server.begin();
  Serial.println("Server started");
}
void setupAP(void)
{
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(100);
  int n = WiFi.scanNetworks();
  Serial.println("scan done");
  if (n == 0)
    Serial.println("no networks found");
  else
  {
    //Serial.print(n);
    Serial.println("networks found");
    for (int i = 0; i < n; ++i)
    {
      // Print SSID and RSSI for each network found
      //Serial.print(i + 1);
      //Serial.print(": ");
      Serial.print(WiFi.SSID(i));
      Serial.print(" (");
      Serial.print(WiFi.RSSI(i));
      Serial.print(")");
      Serial.println((WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*");
      delay(10);
    }
  }
  Serial.println("");
  st = "<ol>";
  for (int i = 0; i < n; ++i)
  {
    // Print SSID and RSSI for each network found
    st += "<li>";
    st += WiFi.SSID(i);
    st += " (";
    st += WiFi.RSSI(i);
    st += ")";
    st += (WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*";
    st += "</li>";
  }
  st += "</ol>";
  delay(100);
  WiFi.softAP("ESP Cua Nhan", "");
  Serial.println("Initializing_softap_for_wifi credentials_modification");
  launchWeb();
  Serial.println("over");
}
void createWebServer()
{
  {
    server.on("/", []() {
      IPAddress ip = WiFi.softAPIP();
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);
      content = "<!DOCTYPE HTML>\r\n<html>Welcome to Wifi Credentials Update page";
      content += "<form action=\"/scan\" method=\"POST\"><input type=\"submit\" value=\"scan\"></form>";
      content += ipStr;
      content += "<p>";
      content += st;
      content += "</p><form method='get' action='setting'><label>SSID: </label><input name='ssid' length=32><input name='pass' length=64><input type='submit'></form>";
      content += "</html>";
      server.send(200, "text/html", content);
    });
    server.on("/scan", []() {
      //setupAP();
      IPAddress ip = WiFi.softAPIP();
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);
      content = "<!DOCTYPE HTML>\r\n<html>go back";
      server.send(200, "text/html", content);
    });
    server.on("/setting", []() {
      String qsid = server.arg("ssid");
      String qpass = server.arg("pass");
      if (qsid.length() > 0 && qpass.length() > 0) {
        Serial.println("clearing eeprom");
        for (int i = 0; i < 96; ++i) {
          EEPROM.write(i, 0);
        }
        Serial.println(qsid);
        Serial.println("");
        Serial.println(qpass);
        Serial.println("");
        Serial.println("writing eeprom ssid:");
        for (int i = 0; i < qsid.length(); ++i)
        {
          EEPROM.write(i, qsid[i]);
          Serial.print("Wrote: ");
          Serial.println(qsid[i]);
        }
        Serial.println("writing eeprom pass:");
        for (int i = 0; i < qpass.length(); ++i)
        {
          EEPROM.write(32 + i, qpass[i]);
          Serial.print("Wrote: ");
          Serial.println(qpass[i]);
        }
        EEPROM.commit();
        content = "{\"Success\":\"saved to eeprom... reset to boot into new wifi\"}";
        statusCode = 200;
        ESP.reset();
      } else {
        content = "{\"Error\":\"404 not found\"}";
        statusCode = 404;
        Serial.println("Sending 404");
      }
      server.sendHeader("Access-Control-Allow-Origin", "*");
      server.send(statusCode, "application/json", content);
    });
  }
}
