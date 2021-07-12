#include <Keypad.h>
#include <LiquidCrystal_I2C.h>
#include <Wire.h>
#include <EEPROM.h>
#define pass_length 7
const byte rows = 4;
const byte cols = 3;
char data[pass_length];
char hide[pass_length];
char correct[pass_length] = "";
byte datacount = 0;
char getkey;
int isOpen = 0;
int sta;
bool first =false;
int sta1;
char hexa[rows][cols] = {
  {'1', '2', '3'},
  {'4', '5', '6'},
  {'7', '8', '9'},
  {'*', '0', '#'} 
};
byte rowPin[rows] = {13, 12, 11, 10};
byte colPin[cols] = {7, 6, 5};  
LiquidCrystal_I2C lcd (0x27,16,2);
Keypad key = Keypad(makeKeymap(hexa), rowPin, colPin, rows, cols);

int in1 = 9;
int in2 = 8;
// motor two

int irSensor = 2;
int irSensor1 = 3;
void setup()
{
  lcd.init();
  lcd.backlight();
  lcd.clear();
  for (int i = 0; i < 6; i++)
  {
      correct[i] = char(EEPROM.read(i));
  }
//  EEPROM.begin(512);
  // set all the motor control pins to outputs
  
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode (irSensor, INPUT);
  pinMode (irSensor1, INPUT);
  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  //pinMode(2, INPUT_PULLUP);
  //attachInterrupt(0, stopmotor, LOW); 
  attachInterrupt(1, hang, LOW); 
  Serial.begin(9600);
}
void hang()
{
  if (!first)
  {
    digitalWrite(in1, LOW);
    digitalWrite(in2, HIGH);
    delay(60000);
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    first = true;
  }
    sta1 = digitalRead(irSensor1);
  if(sta1 == 0)
  {
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    //Serial.println(isOpen);
    //delay(1000); 
  }
  if(sta1 == 1)
  {
    delay(700000);
    digitalWrite(in1, HIGH);
    digitalWrite(in2, LOW);
    delay(60000);
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    first = false;
  }
    
    

}
void stopmotor()
{
  if (isOpen == 1)
  {
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    sta = digitalRead(irSensor1);
  if(sta == 1)
  {
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    Serial.println(sta);
    delay(1000); 
  }
  else
  {
    delay(2000);
    digitalWrite(in1, HIGH);
    digitalWrite(in2, LOW);
    delay(400);
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    isOpen =0;
  }
  }
   
    
}
void loop()
{
  lcd.setCursor(0,0);
  lcd.print("Nhap mat khau:");
  digitalWrite(LED_BUILTIN, LOW); 
  getkey = key.getKey();
  if (isOpen == 1)
  {
    
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    sta = digitalRead(irSensor);
  if(sta == 0)
  {
    detachInterrupt(1);
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    //Serial.println(isOpen);
    //delay(1000); 
  }
  if(sta == 1)
  {
    detachInterrupt(1);
    delay(2000);
    Serial.println(isOpen);
    digitalWrite(in1, HIGH);
    digitalWrite(in2, LOW);
    delay(280);
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    attachInterrupt(1, hang, LOW); 
    isOpen =0;
  }
  }
  else if (isOpen == 0)
  {
  
  
  if(Serial.available())
  {
    char a = Serial.read();
    if (a== 49)
    {
      
      Serial.println("1");
      digitalWrite(in1, LOW);
    digitalWrite(in2, HIGH);
    delay(225);
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    isOpen =1;
    while (datacount != 0)
    {
      data[datacount--] = 0;
      hide[datacount--] = 0;
      lcd.clear();
    }
    
   
    }
    else
    {
      String a = Serial.readString();
      //Serial.println(a);
      if (a.substring(6,7) == "*")
      {
        for (int i = 0; i < 6; i++)
        {
          EEPROM.write(i, a[i]);
        }
        for (int i = 0; i < 6; i++)
        {
          correct[i] = char(EEPROM.read(i));
        }
        lcd.clear();
        lcd.setCursor(0,0);
        lcd.print("Password Changed");
        delay(2000);
        lcd.clear();
      }
      
    }
  }
  
  if (getkey)
  {
    data[datacount] = getkey;
    hide[datacount] = '*';
    
    Serial.println(data);
    lcd.setCursor(0,1);
    lcd.print(hide);
    datacount++;
  }
  if (datacount == pass_length-1)
  {
    if (!strcmp(data, correct))
    {
    
      digitalWrite(in1, LOW);
    digitalWrite(in2, HIGH);
    delay(225);
    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);
    isOpen = 1;

    
    }
    else
    {
      digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
      delay(1000);                       // wait for a second
      digitalWrite(LED_BUILTIN, LOW);
      lcd.setCursor(0,1);
      lcd.print("Sai mat khau");// turn the LED off by making the voltage LOW
      delay(1000); 
    }
    while (datacount != 0)
    {
      lcd.setCursor(0,1);
      lcd.print("Sai mat khau");
      data[datacount--] = 0;
      hide[datacount--] = 0;
      lcd.clear();
    }
  }
  }
  
}
