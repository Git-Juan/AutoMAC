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
#define LDR_MODE 'v'



#define E1 10  // Enable Pin motor 1 IZQUIERDA
#define I1 7     // Control pin 1 motor izq
#define I2 8     // Control pin 2 motor izq
#define E2 11  // Enable Pin motor 2 DERECHA
#define I3 5     // Control pin 3 motor der
#define I4 6     // Control pin 4 motor der


unsigned long vel = 3, velocidad=0;
int LDR = 0;
char estado = 0;
boolean encendido = false;
unsigned long distancia, tiempo_giro = 0, tiempo_arranque = 0, tiempo_reversa = 0, tiempo_adelante = 0, tiempo_led=0, tiempo_calibracion=0;
boolean choca = false, freno = true, arranco = false;
unsigned long contador2 = 0, contador1 = 0, puls1=0, puls2=0;
unsigned long   tiempomuestra=0 , tiempo1 = 0, tiempo2 = 0, rebote2 = 0, rebote1 = 0;
int PWM1=100, PWM2=100;
String c1, c2;
unsigned contador1_req=5, contador2_req=5;
void setup()
{

 // Wire.begin();
  Serial.begin (9600);
  //   pinMode(trigPin, OUTPUT);

  pinMode(encoder2, INPUT); // Configuración del pin nº2
  pinMode(encoder1, INPUT); // Configuración del pin nº2
  attachInterrupt(0, rai_encoder2, RISING); // Configuración de la interrupción 0
  attachInterrupt(1, rai_encoder1, RISING); // Configuración de la interrupción 1
  pinMode(A0, INPUT);
  pinMode(echoPin, INPUT);
  pinMode(led, OUTPUT);
  pinMode(trigPin, OUTPUT);
  
  
  for (int i = 5; i < 12 ; i++)                   // Inicializamos los pines
    pinMode( i, OUTPUT);
}


void rai_encoder2() {
 
  noInterrupts();
  if (digitalRead (encoder2) && (millis() - rebote2 >= 1) && digitalRead (encoder2) ) {                 //

    rebote2 = millis();
    contador2++;
   // puls2++;

  }
  interrupts();
  
}

void rai_encoder1() {

  noInterrupts();
  if (digitalRead (encoder1) && (millis() - rebote1 >= 1) && digitalRead (encoder1) ) {

    rebote1 = millis();
    contador1++;
  //  puls1++;

  }
  interrupts();
}

void calibracion(int offset1, int offset2){
   if(contador2<contador2_req + offset1 && contador2!=0){  //Si los pulsos son < que los pulsos requeridos para esa velocidad, incrementar el PWM del motor
          PWM2=constrain(PWM2+2,80,255);            
        }
        else{
               if(contador2>contador2_req+ offset2 && contador2!=0){
                  PWM2=constrain(PWM2-2,80,255);          //Si los pulsos son > que los pulsos requeridos para esa velocidad, decrementar el PWM del motor
               }
        }
       

  if(contador1<contador1_req+offset1 && contador1!=0){
          PWM1=constrain(PWM1+2,80,255);
            
        }
        else{
             if(contador1>contador2_req+offset1 && contador1!=0){
              PWM1=constrain(PWM1-2,80,255);
             } 
        }
        
       
      
       contador2=0; //Resetear contadores de pulsos
       contador1=0;
        
  
}

void frenar() {

  analogWrite(E1, 0);     // Desactivar motor izquierdo
  analogWrite(E2, 0);     // Desactivar motor derecho
  tiempo_arranque = millis(); //Actualizar tiempo para el arranque
}




void obtener_distancia() {
  noInterrupts();
  digitalWrite(trigPin, HIGH);       // Activar el pulso de salida
  distancia = pulseIn(echoPin, HIGH, 3000) / 58.2  ;
  interrupts();
  
  //     Serial.println(String(distancia) + " cm.") ;

  if ( distancia < 15 && distancia != 0) {            //Distancia < 15 cm

    
    tiempo_giro = millis(); //Actualizar tiempo de frenado

    analogWrite ( led , (255 - (distancia * 8.5)));
  }

  else {

    
    if (distancia < 40 && distancia != 0 )               //Empezar a encender led
      analogWrite ( led , (255 - (distancia * 6.375)));
    else
      digitalWrite(led, LOW);
  }

  digitalWrite(trigPin, LOW);


}



void girar() {

  analogWrite(E1, 170);     // Activar motor izquierdo
  analogWrite(E2, 0);     // Desactivar motor derecho

  digitalWrite(I3, HIGH);     // Inversion giro motor izquierdo
  digitalWrite(I4, LOW);

  tiempo_arranque = millis();
  
  

}

void adelante(int pwm1, int pwm2) {



  digitalWrite(I1, LOW);     // Girar horario motor izquierdo
  digitalWrite(I2, HIGH);

  digitalWrite(I3, LOW);     // Girar horario motor derecho
  digitalWrite(I4, HIGH);







  if ((millis() - tiempo_reversa) > 100) {            //Si el tiempo de la ultima vez que estuvo en dirección reversa > 100



    if ((millis() - tiempo_arranque) < 100 || arranco == false) {      //Tiempo de duración del arranque 100 ms

      arranco = true;
      freno = false;
      analogWrite(E1, 150);     // Activar motor izquierdo
      analogWrite(E2,150);     // Activar motor derecho

    }
    else
    {
      analogWrite(E1, pwm1);     // Activar motor izquierdo
      analogWrite(E2, pwm2);     // Activar motor derecho

    }


    tiempo_adelante = millis();    //Actualizar tiempo en dirección adelante

  }
  else {                 //Si el tiempo de la ultima vez que estuvo en reversa no es > 100, frenar
    frenar();         
  }


}








void reversa(int pwm1, int pwm2) {
  digitalWrite(I1, HIGH);     // Sentido horario motor izquierdo
  digitalWrite(I2, LOW);

  digitalWrite(I3, HIGH);     // Sentido horario motor derecho
  digitalWrite(I4, LOW);







  if ((millis() - tiempo_adelante) > 100) {           //Si el tiempo de la ultima vez que estuvo en dirección adelante > 100



    if ((millis() - tiempo_arranque) < 50 || arranco == false) {

      arranco = true;
      //   freno=false;
      analogWrite(E1, 150);     // Activar motor izquierdo
      analogWrite(E2, 135);     // Activar motor derecho

    }
    else
    {
      analogWrite(E1, pwm1);     // Activar motor izquierdo
      analogWrite(E2, pwm2);     // Activar motor derecho

    }


    tiempo_reversa = millis();          //Actualizar tiempo en dirección reversa

  }
  else {                  //Si el tiempo de la ultima vez que estuvo en reversa no es > 100, frenar
    frenar();
  }



}




void accion() {


  switch (estado) {

    case ADELANTE:

      if ((millis() - tiempo_giro) > 150) {                  //Si el tiempo girando > 150 ms, mover hacia adelante

        adelante(PWM1+velocidad, PWM2+velocidad);
            
                    if(millis() - tiempo_calibracion > 100){      
                      tiempo_calibracion=millis();
                      calibracion(0,0);                     //Relación de realimentación
                    }

      }
      else {
        girar();
        tiempo_calibracion=millis();
        
      }

      break;

    case REVERSA: reversa(PWM1+30, PWM2+50);
            
                    if(millis() - tiempo_calibracion > 100){
                      tiempo_calibracion=millis();
                      calibracion(0,0);
                    }

      break;

    case FRENAR: frenar();
      break;

    case DERECHA_MIN_A:

      if ((millis() - tiempo_giro) > 150) {

        adelante(PWM1+15+velocidad, PWM2-15+velocidad);
            
                    if(millis() - tiempo_calibracion > 100){
                      tiempo_calibracion=millis();
                      calibracion(1,0);
                    }

      }
      else {
        girar();
      }

      break;
    case DERECHA_MED_A:

      if ((millis() - tiempo_giro) > 150) {

        adelante(PWM1+30+velocidad, PWM2-15+velocidad);
            
                    if(millis() - tiempo_calibracion > 100){
                      tiempo_calibracion=millis();
                      calibracion(2,0);
                    }

      }
      else {
        girar();
      }

      break;
    case DERECHA_MAX_A:

      if ((millis() - tiempo_giro) > 150) {

        adelante(PWM1+35, PWM2-2);
            
                    if(millis() - tiempo_calibracion > 100){
                      tiempo_calibracion=millis();
                      calibracion(2,-1);
                    }

      }
      else {
        girar();
      }

      break;
    case IZQUIERDA_MIN_A:

      if ((millis() - tiempo_giro) > 150) {

        adelante(PWM1+velocidad, PWM2+10+velocidad);
            
                    if(millis() - tiempo_calibracion > 100){
                      tiempo_calibracion=millis();
                      calibracion(0,1);
                    }

      }
      else {
        girar();
      }

      break;
    case IZQUIERDA_MED_A:

      if ((millis() - tiempo_giro) > 150) {

        adelante(PWM1-15+velocidad, PWM2+20+velocidad);
            
                    if(millis() - tiempo_calibracion > 100){
                      tiempo_calibracion=millis();
                      calibracion(0,2);
                    }

      }
      else {
        girar();
      }

      break;
    case IZQUIERDA_MAX_A:

      if ((millis() - tiempo_giro) > 150) {

        adelante(PWM1 - 10 + velocidad, PWM2 + 30 + velocidad);
            
                    if(millis() - tiempo_calibracion > 100){
                      tiempo_calibracion=millis();
                      calibracion(-1,2);
                    }

      }
      else {
        girar();
      }

      break;

    case DERECHA_MIN_R: reversa(105, 95);
    break;

    case DERECHA_MED_R: reversa(110, 90);
    break;
    
    case DERECHA_MAX_R: reversa(115, 85);
    break;  

    case IZQUIERDA_MIN_R: reversa(95, 105);
    break;

    case IZQUIERDA_MED_R: reversa(90, 110);
    break;

    case IZQUIERDA_MAX_R: reversa(85, 115);
    break;

    case '1':
      contador1_req=contador2_req= 6;
      
      velocidad= 25;
      
      break;

    case '2':
      contador1_req=contador2_req=8;
      velocidad = 35;
      break;

    case '3':
      contador1_req=contador2_req=10;
      velocidad = 45;
      break;

    case '4':
      contador1_req=contador2_req = 12;
      velocidad = 55;
      break;
    
    case '5':
      contador1_req=contador2_req=14;
      velocidad=100;
    break;
    

   

    case 'z':
      if((millis() - tiempo_led) > 100){ //Tiempo para intemritencia del LED
        digitalWrite(9,HIGH);  
        tiempo_led=millis();
      }
      else{
        digitalWrite(9,LOW); 
         
      }
      
      break;
    case 'y':
      digitalWrite(9,LOW);     //Apagar LED
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
    //analogWrite(E1, 0);     // Desactivar motor 1
   // analogWrite(E2, 0);     // Desactivar motor 2
  }





  if (encendido) {
   
  obtener_distancia();
           
  accion();

  }

  else{
    
    if (analogRead(A0) > 700){        //Si hay luz
        obtener_distancia();
                    if((millis() - tiempo_giro) > 150){
                      adelante(PWM1+25,PWM2+25);
                        if(millis() - tiempo_calibracion > 100){
                                            tiempo_calibracion=millis();
                                            calibracion(0,0);
                                        }
                
                    }
                    else{
                     girar();
                    }
        
                }
                else{
                frenar(); 
                }
  }
 
   
 /* if ((millis() - tiempomuestra) >1000){
  //  Serial.print(puls1);
  //  Serial.print("/");
  //  Serial.print(puls2);
    puls1=0;
    puls2=0;
    tiempomuestra=millis();
  }

*/

         
            
   

}





