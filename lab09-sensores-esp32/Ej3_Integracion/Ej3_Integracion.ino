// Laboratorio 9 - Ejercicio 3: Integracion HC-SR04 + DHT11 (+ LCD 16x2 I2C opcional)
// Librerias: "DHT sensor library" (Adafruit) + "Adafruit Unified Sensor"
//            "LiquidCrystal I2C" (Frank de Brabander)  -> solo si USAR_LCD = 1
// Conexiones:
//   HC-SR04: VCC->VIN, Trig->GPIO5, Echo->GPIO18 (con divisor), GND->GND
//   DHT11:   S->GPIO2, V->3V3, G->GND
//   LCD I2C: VCC->VIN, GND->GND, SDA->GPIO21, SCL->GPIO22

#define USAR_LCD 1   // Pon 0 si no tienes pantalla LCD I2C

#include "DHT.h"
#if USAR_LCD
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd(0x27, 16, 2);  // Si no muestra nada prueba con 0x3F
#endif

#define DHTPIN 2
#define DHTTYPE DHT11
const int trigPin = 5;
const int echoPin = 18;
const float VELOCIDAD_SONIDO = 0.0343;  // cm/us

DHT dht(DHTPIN, DHTTYPE);

float humedad = NAN;
float tempC = NAN;
unsigned long ultimaLecturaDHT = 0;

float medirDistancia() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  long duracion = pulseIn(echoPin, HIGH, 30000);
  if (duracion == 0) return -1;  // sin eco / fuera de rango
  return duracion * VELOCIDAD_SONIDO / 2;
}

void mostrarLCD(const char *linea1, const char *linea2) {
#if USAR_LCD
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(linea1);
  lcd.setCursor(0, 1);
  lcd.print(linea2);
#endif
}

// Rutina de inicializacion: presenta el equipo y verifica cada sensor
void inicializarEquipo() {
  Serial.println("=== Iniciando equipo ===");
  mostrarLCD("Lab 09 - Tecsup", "Iniciando...");
  delay(1500);

  delay(1000);  // el DHT11 necesita ~1 s despues de encender
  bool dhtOk = !isnan(dht.readTemperature());
  Serial.println(dhtOk ? "DHT11 ........ OK" : "DHT11 ........ ERROR");

  bool ultraOk = medirDistancia() > 0;
  Serial.println(ultraOk ? "HC-SR04 ...... OK" : "HC-SR04 ...... SIN ECO");

  mostrarLCD(dhtOk ? "DHT11:   OK" : "DHT11:   ERROR",
             ultraOk ? "HC-SR04: OK" : "HC-SR04: SIN ECO");
  delay(2000);
  Serial.println("=== Equipo listo ===");
}

void setup() {
  Serial.begin(115200);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  dht.begin();
#if USAR_LCD
  lcd.init();
  lcd.backlight();
#endif
  inicializarEquipo();
}

void loop() {
  // DHT11 cada 2 s (es lento); distancia cada 0.5 s
  if (millis() - ultimaLecturaDHT >= 2000) {
    ultimaLecturaDHT = millis();
    float h = dht.readHumidity();
    float t = dht.readTemperature();
    if (!isnan(h) && !isnan(t)) {
      humedad = h;
      tempC = t;
    } else {
      Serial.println("Error al leer el DHT11");
    }
  }

  float distancia = medirDistancia();

  Serial.print("Distancia: ");
  if (distancia < 0) Serial.print("---");
  else Serial.print(distancia, 1);
  Serial.print(" cm  |  Temp: ");
  Serial.print(tempC, 1);
  Serial.print(" C  |  Hum: ");
  Serial.print(humedad, 0);
  Serial.println(" %");

  char linea1[17];
  char linea2[17];
  if (distancia < 0) snprintf(linea1, sizeof(linea1), "Dist: ---");
  else snprintf(linea1, sizeof(linea1), "Dist: %.1f cm", distancia);
  snprintf(linea2, sizeof(linea2), "T:%.1fC  H:%.0f%%", tempC, humedad);
  mostrarLCD(linea1, linea2);

  delay(500);
}
