  #include "Wire.h"

#include <SoftwareSerial.h>

//SoftwareSerial BTSerial(0, 1);

#define trigPin 12
#define echoPin 13
#define led 9
#define encoder2 2 //Pin 2, donde se conecta el encoder
#define encoder1 3

#define ADELANTE 'a'
#define REVERSA 'r'
#define DERECHA_MIN_A 'b'
#define IZQUIERDA_MIN_A 'c'
#define DERECHA_MED_A 'd'
#define IZQUIERDA_MED_A 'e'
#define DERECHA_MAX_A 'g'
#define IZQUIERDA_MAX_A 'h'
#define DERECHA_MIN_R 'i'
#define IZQUIERDA_MIN_R 'j'
#define DERECHA_MED_R 'k'
#define IZQUIERDA_MED_R 'l'
#define DERECHA_MAX_R 'm'
#define IZQUIERDA_MAX_R 'n'
#define FRENAR 'f'
#define ON 'p'
#define OFF 'o'



#define E1 10  // Enable Pin motor 1 IZQUIERDA
#define I1 7     // Control pin 1 motor izq
#define I2 8     // Control pin 2 motor izq
#define E2 11  // Enable Pin motor 2 DERECHA
#define I3 5     // Control pin 3 motor der
#define I4 6     // Control pin 4 motor der



#define VELGIRO 120
#define VELARRANQUE 175

unsigned long velocidad = 0;
int LDR = 0;
char estado = 0;
boolean encendido = false;
unsigned long distancia, tiempofreno = 0, tiempoarranque = 0, tiemporeversa = 0, tiempoadelante = 0;
boolean choca = false, freno = true, arranco = false;
byte contador2 = 0, contador1 = 0;
unsigned int  tiempo=500, tiempo1 = 0, tiempo2 = 0, rebote2 = 0, rebote1 = 0;
int v1=110, v2=110;
String c1, c2;
void setup()
{

 // Wire.begin();
  Serial.begin (9600);
  //   pinMode(trigPin, OUTPUT);

  pinMode(2, INPUT); // Configuración del pin nº2
  pinMode(3, INPUT); // Configuración del pin nº2
  attachInterrupt(0, rai_encoder2, RISING); // Configuración de la interrupción 0
  attachInterrupt(1, rai_encoder1, RISING); // Configuración de la interrupción 1
  pinMode(A0, INPUT);
  pinMode(echoPin, INPUT);
  pinMode(led, OUTPUT);
  for (int i = 5; i < 13 ; i++)                   // Inicializamos los pines
    pinMode( i, OUTPUT);
}


void rai_encoder2() {
 
  noInterrupts();
  if (digitalRead (encoder2) && (millis() - rebote2 >= 1) && digitalRead (encoder2) ) {

    rebote2 = millis();
    contador2++;

  }
  interrupts();
  
}

void rai_encoder1() {

  noInterrupts();
  if (digitalRead (encoder1) && (millis() - rebote1 >= 1) && digitalRead (encoder1) ) {

    rebote1 = millis();
    contador1++;

  }
  interrupts();
}



void frenar() {

  analogWrite(E1, 0);     // Desactivar motor 1
  analogWrite(E2, 0);     // Desactivar motor 2
  tiempoarranque = millis();
}


void obtener_distancia() {
  noInterrupts();
  digitalWrite(trigPin, HIGH);       // Activar el pulso de salida
  distancia = pulseIn(echoPin, HIGH, 3000) / 58.2  ;
  interrupts();

  //     Serial.println(String(distancia) + " cm.") ;

  if ( distancia < 15 && distancia != 0) {

    // choca=true;
    tiempofreno = millis();

    analogWrite ( led , (255 - (distancia * 8.5)));
  }

  else {

    //choca=false;
    if (distancia < 40 && distancia != 0 )
      analogWrite ( led , (255 - (distancia * 6.375)));
    else
      digitalWrite(led, LOW);
  }

  digitalWrite(trigPin, LOW);


}



void girar() {

  analogWrite(E1, 190);     // activar motor izquierdo
  analogWrite(E2, 0);     // desactivar motor derecho

  digitalWrite(I3, HIGH);     // inversion giro motor izquierdo
  digitalWrite(I4, LOW);

  tiempoarranque = millis();

}

void adelante(int v1, int v2) {



  digitalWrite(I1, LOW);     // girar horario motor izquierdo
  digitalWrite(I2, HIGH);

  digitalWrite(I3, LOW);     // girar horario motor derecho
  digitalWrite(I4, HIGH);







  if ((millis() - tiemporeversa) > 100) {



    if ((millis() - tiempoarranque) < 100 || arranco == false) {

      arranco = true;
      freno = false;
      analogWrite(E1, 153+velocidad);     // Activar motor izquierdo
      analogWrite(E2, 153+velocidad);     // Activar motor derecho

    }
    else
    {
      analogWrite(E1, v1);     // Activar motor izquierdo
      analogWrite(E2, v2);     // Activar motor derecho

    }


    tiempoadelante = millis();

  }
  else {
    frenar();
  }


}








void reversa(int v1, int v2) {
  digitalWrite(I1, HIGH);     // Sentido horario motor izquierdo
  digitalWrite(I2, LOW);

  digitalWrite(I3, HIGH);     // Sentido horario motor derecho
  digitalWrite(I4, LOW);







  if ((millis() - tiempoadelante) > 100) {



    if ((millis() - tiempoarranque) < 100 || arranco == false) {

      arranco = true;
      //   freno=false;
      analogWrite(E1, 180);     // Activar motor izquierdo
      analogWrite(E2, 170);     // Activar motor derecho

    }
    else
    {
      analogWrite(E1, v1);     // Activar motor izquierdo
      analogWrite(E2, v2);     // Activar motor derecho

    }


    tiemporeversa = millis();

  }
  else {
    frenar();
  }



}




void accion() {


  switch (estado) {

    case ADELANTE:

      if ((millis() - tiempofreno) > 150) {

        adelante(v1 + velocidad, v2 + velocidad);

      }
      else {
        girar();
      }

      break;

    case REVERSA: reversa(150, 150);
      break;

    case FRENAR: frenar();
      break;

    case DERECHA_MIN_A:

      if ((millis() - tiempofreno) > 150) {

        adelante(117 + velocidad, 85 + velocidad);

      }
      else {
        girar();
      }

      break;

    case DERECHA_MED_A:

      if ((millis() - tiempofreno) > 100) {

        adelante(130 + velocidad, 80 + velocidad);

      }
      else {
        girar();
      }

      break;

    case DERECHA_MAX_A:

      if ((millis() - tiempofreno) > 100) {

        adelante(135 + velocidad, 70 + velocidad);

      }
      else {
        girar();
      }

      break;

    case IZQUIERDA_MIN_A:

      if ((millis() - tiempofreno) > 150) {

        adelante(100 + velocidad, 100 + velocidad);

      }
      else {
        girar();
      }

      break;


    case IZQUIERDA_MED_A:

      if ((millis() - tiempofreno) > 100) {

        adelante(85 + velocidad, 120 + velocidad);

      }
      else {
        girar();
      }

      break;


    case IZQUIERDA_MAX_A:

      if ((millis() - tiempofreno) > 100) {

        adelante(80 + velocidad, 130 + velocidad);

      }
      else {
        girar();
      }

      break;

    case DERECHA_MIN_R: reversa(105, 95);
    break;

    case DERECHA_MED_R:  reversa(110, 90);
    break;
    
    case DERECHA_MAX_R:reversa(115, 85);
    break;  

    case IZQUIERDA_MIN_R: reversa(95, 105);
    break;

    case IZQUIERDA_MED_R: reversa(90, 110);
    break;

    case IZQUIERDA_MAX_R: reversa(85, 115);
    break;

    case '1':
      velocidad = 25;
      break;

    case '2':
      velocidad = 35;
      break;

    case '3':
      velocidad = 45;
      break;

    case '4':
      velocidad = 55;
      break;
    
    case '5':
      velocidad=100;
    break;
    

    case '0':
      velocidad = 10;
      break;





  }
}





void loop()
{

  if (Serial.available() > 0) {
    estado = (char)Serial.read();
    Serial.println(estado);
  }

  if (estado == ON)
  {
    encendido = true;
  }

  if (estado == OFF)
  {
    encendido = false;
    analogWrite(E1, 0);     // Desactivar motor 1
    analogWrite(E2, 0);     // Desactivar motor 2
  }





  if (encendido) {
    obtener_distancia();


    if ((millis() - tiempofreno) > 100 && analogRead(A0) > 700) {
      adelante(130, 110);
    }
    else {
      analogWrite(E1, 0);     // Desactivar motor 1
      analogWrite(E2, 0);     // Desactivar motor 2
    }

    accion();
   

    

                if (millis() - tiempo2 >= 100) {
                  tiempo2 = millis();
                //  Serial.write(contador2);
               //   String c1="{/}";
                //  c2.concat(contador2);
           //       Serial.print(contador2);
                //  Serial.print();
                 contador1 = 45;
                 contador2 = 13;
                 
              /*   String c1 = {"+"};
                 c1.concat(contador1);
                 String c2 = {"/"};
                 c1.concat(c2);
                 c1.concat(contador2);
                 String c3 = {"#"};
                 c1.concat(c3);*/
                  
              // Serial.print("+" + String(contador1) + "/" + String(contador2) + "#") ;
              Serial.flush();
             // Serial.print("$");
              Serial.print(contador1);
              Serial.print("/");
              Serial.print(contador2);
              Serial.print("#"); 
                }
            
   
   }

}





