// Laboratorio 9 - Ejercicio 1: Sensor de ultrasonido HC-SR04 con ESP32
// Conexiones:
//   HC-SR04 VCC  -> VIN (5V)
//   HC-SR04 Trig -> GPIO5  (D5)
//   HC-SR04 Echo -> GPIO18 (D18)  (recomendado: con divisor 1k / 2k)
//   HC-SR04 GND  -> GND

const int trigPin = 5;
const int echoPin = 18;

// Velocidad del sonido: 343 m/s = 0.0343 cm/us
const float VELOCIDAD_SONIDO = 0.0343;

long duracion;
float distanciaCm;

void setup() {
  Serial.begin(115200);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  Serial.println("Ejercicio 1 - HC-SR04 listo");
}

void loop() {
  // Pulso de disparo de 10 us en Trig
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  // Mide cuanto tiempo (us) permanece Echo en HIGH; 30 ms de espera maxima
  duracion = pulseIn(echoPin, HIGH, 30000);

  if (duracion == 0) {
    Serial.println("Sin eco: fuera de rango (> 4 m o < 2 cm)");
  } else {
    // El sonido va y vuelve, por eso se divide entre 2
    distanciaCm = duracion * VELOCIDAD_SONIDO / 2;
    Serial.print("Tiempo: ");
    Serial.print(duracion);
    Serial.print(" us  |  Distancia: ");
    Serial.print(distanciaCm);
    Serial.println(" cm");
  }

  delay(500);
}
