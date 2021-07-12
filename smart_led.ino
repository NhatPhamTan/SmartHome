#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <FirebaseArduino.h>
#include <ArduinoJson.h>
#include <BH1750.h>
#include <Wire.h>
#include <NTPClient.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WebServer.h>
#include <EEPROM.h>

WiFiUDP udp;
NTPClient n(udp, "1.asia.pool.ntp.org", 7*3600); // dich thoi gian 7 tieng
BH1750 lightMeter(0x23);
ESP8266WebServer server(80);
#define FIREBASE_HOST "ledcontrol-2f117-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "Z1TYn6WohBwleZNCp8y9Kioj3nMWWHx9wHE59XfF"

String content;
int statusCode;
String st;
String alm;
String tol;
// define specs:



void setup() {
    Serial.begin(115200);
    //init BH1750 module
    Wire.begin(D4, D3);
    lightMeter.begin();
    if(lightMeter.begin()){
      Serial.println(F("BH1750 initialised"));
    }
    else {
      Serial.println(F("Error initialising BH1750"));
    }
    // define pin
    pinMode(D0, OUTPUT);
    
    //Connect to WiFi
    WiFi.mode(WIFI_AP);
    WiFi.disconnect();
    EEPROM.begin(512);
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
    WiFi.begin(esid.c_str(), epass.c_str());
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    
    if (testWifi())
    {
      Serial.println("Succesfully Connected!!!");
      delay(2000);
      return;
    }
    else
    {
      Serial.println("Turning the HotSpot On");
      delay(500);
      launchWeb();
      setupAP();// Setup HotSpot
    }
  
    Serial.print("Connecting...");
    while (WiFi.status() != WL_CONNECTED) {
      Serial.print(".");
      delay(500);
      server.handleClient();
    }
    Serial.println();
    Serial.print("Connected: ");
    Serial.println(WiFi.localIP());

    //Firebase
    Firebase.stream("/obj"); 
    
    n.begin();
}

void loop() {
    analogWriteRange(10);
    float lux = lightMeter.readLightLevel();
    Firebase.setFloat("/obj/IsAuto/lux", lux);
    String l_mode = Firebase.getString("/obj/IsAuto/mode");
    n.update();
    
    if(Firebase.available()){
      FirebaseObject fboj = Firebase.readEvent();
      String path = fboj.getString("path");
      if(path == "/setAlarmTime"){
        alm = fboj.getString("data");
        Serial.println("Alarm was set at " + alm);
      }
      else if(path == "/setOnTime"){
        tol = fboj.getString("data");
        Serial.println("Turn on the light at " + tol);
      }
    }
      
        String IsAuto = Firebase.getString("/obj/IsAuto/Auto");
        if(IsAuto == "1"){
            Serial.println(String(lux));
            if(lux < 10 && lux >= 0){
              analogWrite(D0, 10);
            }
            else if(lux >= 10 && lux < 30){
              analogWrite(D0, 8);
            }
            else if(lux >= 30 && lux < 50){
              analogWrite(D0, 6);
            }
            else if(lux >= 50 && lux < 70){
              analogWrite(D0, 4);
            }
            else if(lux >= 70 && lux < 90){
              analogWrite(D0, 2);
            }
            else{
              analogWrite(D0, 0);
            }
        }
        else if(IsAuto == "0"){
            Serial.println(String(l_mode));
            if(l_mode == "0"){
              analogWrite(D0, 0);
            }
            else if(l_mode == "1"){
              analogWrite(D0, 2);
            }
            else if(l_mode == "2"){
              analogWrite(D0, 4);
            }
            else if(l_mode == "3"){
              analogWrite(D0, 6);
            }
            else if(l_mode == "4"){
              analogWrite(D0, 8);
            }
            else if(l_mode == "5"){
              analogWrite(D0, 10);
            }
        }
    if(comp_Time(alm)){
      digitalWrite(D0, LOW);
      while(comp_Time(tol) == false){
        delay(500);
      }
    }
    Firebase.setFloat("/obj/IsAuto/lux", lux);
}
bool comp_Time(String gs){
    String s = n.getFormattedTime();
    Serial.println(s);
    int s_hour = s.substring(0,2).toInt();
    int s_min = s.substring(3,5).toInt();
    int s_sec = s.substring(6,8).toInt();
    int gs_hour = gs.substring(0,2).toInt();
    int gs_min = gs.substring(3,5).toInt();
    int gs_sec = gs.substring(6,8).toInt();
    if(((gs_hour == s_hour) && (gs_min == s_min)) && (gs_sec == s_sec)){
      return true;
    }
    else{
      return false;
    }
}
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
  WiFi.softAP("ESP Cua Nhatdeptrai", "");
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
