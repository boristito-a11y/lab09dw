// Laboratorio 9 - Ejercicio 2: Sensor de temperatura y humedad DHT11 con ESP32
// Librerias (Gestor de librerias): "DHT sensor library" de Adafruit
//                                  + "Adafruit Unified Sensor"
// Conexiones (modulo DHT11 de 3 pines):
//   S (data) -> GPIO2 (D2)
//   V (+)    -> 3V3
//   G (-)    -> GND

#include "DHT.h"

#define DHTPIN 2
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(115200);
  dht.begin();
  Serial.println("Ejercicio 2 - DHT11 listo");
}

void loop() {
  // El DHT11 solo entrega un dato nuevo cada ~1 s; leemos cada 2 s
  delay(2000);

  float humedad = dht.readHumidity();
  float tempC = dht.readTemperature();      // Celsius
  float tempF = dht.readTemperature(true);  // Fahrenheit

  if (isnan(humedad) || isnan(tempC) || isnan(tempF)) {
    Serial.println("Error al leer el DHT11. Revisa las conexiones.");
    return;
  }

  // Sensacion termica (indice de calor)
  float indiceC = dht.computeHeatIndex(tempC, humedad, false);

  Serial.print("Humedad: ");
  Serial.print(humedad);
  Serial.print(" %  |  Temperatura: ");
  Serial.print(tempC);
  Serial.print(" C / ");
  Serial.print(tempF);
  Serial.print(" F  |  Sensacion termica: ");
  Serial.print(indiceC);
  Serial.println(" C");
}
