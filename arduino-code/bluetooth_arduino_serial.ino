char Incoming_value = 0;
#define ON   0
#define OFF  1
char last_value1 = 0;
char last_value2 = 0;
void setup()
{
  Serial.begin(9600);
  pinMode(D0, OUTPUT);
  relay_init();
}

void loop()
{
  if (Serial.available() > 0)
  {
    Incoming_value = Serial.read();
    if (Incoming_value == '1') {
      relay1(ON);
      last_value1 = '1';
    }
    else if (Incoming_value == '0') {
      relay1(OFF);
      last_value1 = '0';
    }
    else if (Incoming_value == '2') {
      relay2(OFF);
      last_value2 = '2';
    }
    else if (Incoming_value == '3') {
      relay2(ON);
      last_value2 = '3';
    }
    else if (Incoming_value == 'l') {
      Serial.println(last_value1);
    }
    else if (Incoming_value == 'k') {
      Serial.println(last_value2);
    }
  }
}

void relay_init(void)//method inialisasi relay
{
  pinMode(D0, OUTPUT); //inisialisasi pin sebagai output
  pinMode(D1, OUTPUT); //inisialisasi pin sebagai output
  relay1(OFF);
  relay2(OFF);
}

void relay1(unsigned char status_1) {
  digitalWrite(D0, status_1);
}
void relay2(unsigned char status_2) {
  digitalWrite(D1, status_2);
}



//this code just support read 1 character , u need differents approach
