# Laboratorio 9 – Sensores Digitales Integrados (ESP32)

Programas:

| Ejercicio | Carpeta / sketch | Qué hace |
|---|---|---|
| 1 | `Ej1_Ultrasonido_HCSR04/` | Mide distancia con el HC-SR04 |
| 2 | `Ej2_DHT11/` | Mide temperatura y humedad con el DHT11 |
| 3 | `Ej3_Integracion/` | Ambos sensores + LCD 16x2 I2C (opcional) + rutina de inicialización |

> En Arduino IDE cada `.ino` debe estar dentro de una carpeta con su mismo nombre (ya está así).
> Monitor Serie: **115200 baudios**.

---

## 0. ¿Qué ESP32 tengo? y cómo configurar Arduino IDE

### Identificar la placa
1. **Lee el texto en la tapa metálica** del módulo:
   - `ESP-WROOM-32` / `ESP32-WROOM-32` / `ESP32-WROVER` → es un **ESP32 clásico** (el del laboratorio). Casi siempre es la placa **ESP32 DevKit V1 (DOIT)** de 30 pines o la DevKitC de 38 pines.
   - `ESP32-S3`, `ESP32-C3`, `ESP32-S2` → es otra familia; elige esa placa en el IDE (ej. "ESP32S3 Dev Module") y revisa que los pines GPIO usados existan.
2. **Cuenta los pines**: 30 pines (15 por lado) = DOIT ESP32 DevKit V1; 38 pines = ESP32-DevKitC.
3. **Mira el chip USB** (chip pequeño cerca del conector USB) — define el **driver** que necesitas en Windows:
   - `CP2102` / `CP2104` → driver **CP210x** de Silicon Labs.
   - `CH340` / `CH9102` → driver **CH340/CH341** de WCH.

### Instalar el soporte de ESP32 en Arduino IDE (2.x)
1. **Archivo → Preferencias → "URLs adicionales del gestor de placas"** y pega:
   ```
   https://espressif.github.io/arduino-esp32/package_esp32_index.json
   ```
2. **Herramientas → Placa → Gestor de placas** → busca **esp32** → instala **"esp32 by Espressif Systems"**.
3. **Herramientas → Placa → esp32 → `ESP32 Dev Module`** (sirve para casi todos los ESP-WROOM-32; también puedes usar `DOIT ESP32 DEVKIT V1`).
4. Conecta el ESP32 y elige **Herramientas → Puerto → COMx**. Si no aparece ningún COM: instala el driver del punto anterior y usa un cable USB **de datos** (muchos cables solo cargan).

### Librerías (Programa → Incluir librería → Administrar bibliotecas)
- **DHT sensor library** (Adafruit) → cuando pregunte, acepta **"Instalar todo"** (instala también *Adafruit Unified Sensor*).
- **LiquidCrystal I2C** (Frank de Brabander) → solo si usas la pantalla LCD en el ejercicio 3.
- El HC-SR04 **no necesita librería**.

### Si al subir sale `Failed to connect to ESP32: Timed out waiting for packet header`
Mantén presionado el botón **BOOT** cuando aparezca `Connecting.....` y suéltalo cuando empiece a escribir (`Writing at 0x...`).
Si aun así falla en los ejercicios 2 y 3, **desconecta el cable de datos del DHT11 (GPIO2) mientras subes** el programa (GPIO2 es un pin de arranque del ESP32) y vuelve a conectarlo después.

---

## 1. Armado del circuito (siempre SIN el USB conectado)

### Ejercicio 1 – HC-SR04
| HC-SR04 | ESP32 |
|---|---|
| VCC | **VIN** (5 V del USB) |
| Trig | **D5** (GPIO5) |
| Echo | **D18** (GPIO18) — ver divisor ↓ |
| GND | **GND** |

**Divisor de voltaje para Echo (recomendado):** el HC-SR04 saca 5 V por Echo y el ESP32 trabaja a 3.3 V.
```
Echo ──[ 1 kΩ ]──┬──> D18 (GPIO18)
                 │
              [ 2 kΩ ]   (o 2.2 kΩ, o dos de 1 kΩ en serie)
                 │
                GND
```
5 V × 2k / (1k + 2k) ≈ 3.3 V. Mucha gente lo conecta directo y funciona, pero así no arriesgas el pin.

### Ejercicio 2 – DHT11 (módulo de 3 pines S‑V‑G)
| DHT11 | ESP32 |
|---|---|
| S (señal) | **D2** (GPIO2) |
| V (+) | **3V3** |
| G (−) | **GND** |

Si tu DHT11 es el sensor "suelto" de 4 patas (sin plaquita): pata 1 = VCC, pata 2 = Data, pata 3 = nada, pata 4 = GND, y pon una **resistencia de 10 kΩ entre Data y VCC**.

### Ejercicio 3 – Integración
Es la unión de los dos circuitos anteriores (mismos pines) + LCD opcional. Usa los rieles de la protoboard para repartir GND, 5 V (VIN) y 3.3 V:
| Componente | Pin | ESP32 |
|---|---|---|
| HC-SR04 | VCC / Trig / Echo / GND | VIN / D5 / D18 (con divisor) / GND |
| DHT11 | S / V / G | D2 / 3V3 / GND |
| LCD 16x2 I2C (opcional) | VCC / GND / SDA / SCL | VIN / GND / **D21** / **D22** |

Sin LCD: cambia `#define USAR_LCD 1` a `0` en el programa.
Si la LCD prende pero no muestra texto: gira el potenciómetro azul de atrás (contraste) o cambia la dirección `0x27` por `0x3F`.

---

## 2. Resultados que deberías obtener

### Ejercicio 1
En el Monitor Serie cada 0.5 s:
```
Tiempo: 583 us  |  Distancia: 10.00 cm
Tiempo: 1166 us  |  Distancia: 20.00 cm
```
- Pon un objeto plano (cuaderno) frente al sensor y mídelo con una regla: a 10, 20, 30 y 50 cm la lectura debe coincidir con un error de **±1 cm** aprox.
- Regla práctica: **~58 µs por cada cm** (10 cm ≈ 583 µs, 1 m ≈ 5830 µs).
- Rango útil: **2 cm a ~4 m**. Muy cerca (< 2 cm), muy lejos, o apuntando a algo blando/inclinado → `Sin eco: fuera de rango` o valores que saltan.

### Ejercicio 2
Cada 2 s:
```
Humedad: 68.00 %  |  Temperatura: 22.00 C / 71.60 F  |  Sensacion termica: 22.09 C
```
- Valores típicos de un aula: **temperatura 18–27 °C** y **humedad 50–85 %** (depende de tu ciudad/clima).
- Precisión del DHT11: ±2 °C y ±5 % HR, resolución de 1 unidad (por eso suelen salir números enteros como 22.00).
- Prueba: **sopla/echa aliento** sobre el sensor → la humedad sube rápido (puede pasar 90 %) y la temperatura sube 1–3 °C; luego vuelven solos a su valor.
- Si sale `Error al leer el DHT11`: cable de datos mal puesto, sensor sin alimentación o pin equivocado.

### Ejercicio 3
Al encender (rutina de inicialización):
```
=== Iniciando equipo ===
DHT11 ........ OK
HC-SR04 ...... OK
=== Equipo listo ===
Distancia: 15.3 cm  |  Temp: 22.0 C  |  Hum: 68 %
```
En la LCD: `Lab 09 - Tecsup / Iniciando...` → `DHT11: OK / HC-SR04: OK` → luego
```
Dist: 15.3 cm
T:22.0C  H:68%
```
La distancia se actualiza cada 0.5 s y la temperatura/humedad cada 2 s. Si no hay nada delante del ultrasonido al arrancar saldrá `HC-SR04: SIN ECO` (no es un error del circuito).

---

## 3. Respuestas sugeridas a las preguntas

**A. Funcionamiento del circuito y programa (Ej. 1).**
El HC-SR04 se alimenta con 5 V (VIN) y GND. El ESP32 envía por GPIO5 (Trig) un pulso HIGH de 10 µs; el sensor emite 8 pulsos ultrasónicos de 40 kHz y pone Echo (GPIO18) en HIGH hasta que recibe el rebote. El programa mide con `pulseIn()` cuánto dura ese HIGH (tiempo de ida y vuelta) y calcula `distancia = tiempo × 0.0343 / 2` (velocidad del sonido 343 m/s, dividido entre 2 porque el sonido va y regresa). El resultado en cm se muestra en el Monitor Serie cada 0.5 s.

**B. ¿Qué realiza `pulseIn(8, HIGH);`?**
Espera a que el pin digital 8 pase a HIGH, empieza a contar, y cuando vuelve a LOW devuelve la **duración del pulso en microsegundos**. Si no llega el pulso antes del tiempo límite (1 s por defecto) devuelve 0. En el sensor se usa para medir el tiempo del pulso Echo, que es proporcional a la distancia. (En nuestro ESP32 el Echo está en el pin 18: `pulseIn(18, HIGH, 30000)`.)

**C. Funcionamiento del circuito y programa (Ej. 2).**
El DHT11 se alimenta con 3.3 V y envía sus datos por un solo cable (GPIO2) con un protocolo digital de un hilo. Contiene un sensor de humedad capacitivo, un termistor NTC y un microcontrolador que entrega 40 bits (humedad, temperatura y checksum). La librería de Adafruit (`DHT dht(2, DHT11)`, `dht.begin()`) se encarga de la comunicación; cada 2 s el programa lee humedad y temperatura, verifica que no sean `NaN` (lectura fallida), calcula la sensación térmica y lo muestra por el Monitor Serie.

**D. ¿Qué realiza `dht.readHumidity();`?**
Solicita una lectura al sensor (o usa la última si fue hace menos de 2 s) y devuelve la **humedad relativa en %** como `float`. Si la comunicación falla devuelve `NaN`.

**E. ¿Qué acción realiza `dht.readTemperature();`?**
Lee el sensor y devuelve la **temperatura en grados Celsius** como `float` (`dht.readTemperature(true)` la devuelve en Fahrenheit). Si la lectura falla devuelve `NaN`.

**F. Funcionamiento del circuito y programa (Ej. 3).**
Integra ambos sensores en el mismo ESP32 compartiendo GND: el HC-SR04 (5 V, Trig GPIO5, Echo GPIO18) y el DHT11 (3.3 V, datos GPIO2), y opcionalmente una LCD 16x2 por I2C (SDA GPIO21, SCL GPIO22). Al encender, una **rutina de inicialización** muestra el nombre del equipo y comprueba que cada sensor responda (OK/ERROR). Luego, en el `loop()`, mide la distancia cada 0.5 s y la temperatura/humedad cada 2 s usando `millis()` (sin bloquear el programa con `delay` largos, porque el DHT11 es lento), y muestra los tres valores en el Monitor Serie y en la LCD.

**Tarea – MQTT vs HTTP (resumen).**
MQTT es un protocolo de mensajería ligero de tipo **publicar/suscribir**: los dispositivos publican en "tópicos" (ej. `lab/temperatura`) a un **broker** (Mosquitto, HiveMQ) y quienes están suscritos reciben el dato al instante. HTTP es **petición/respuesta** cliente‑servidor. Diferencias: MQTT mantiene una conexión TCP abierta y sus cabeceras son de apenas 2 bytes (menos consumo de datos y energía), permite comunicación en tiempo real y en ambos sentidos, y tiene niveles de calidad de servicio (QoS 0, 1, 2); HTTP abre una petición por cada envío, con cabeceras grandes, y el servidor no puede enviar datos al dispositivo si este no los pide. Por eso MQTT se prefiere en IoT y HTTP en web/APIs.

**Investigación – ¿Qué es IoT?**
El Internet de las Cosas (IoT) es la red de objetos físicos (sensores, electrodomésticos, máquinas, vehículos) que tienen electrónica, software y conectividad para **recolectar datos e intercambiarlos por Internet** sin intervención humana, permitiendo monitorearlos y controlarlos a distancia. Ejemplo: un ESP32 con DHT11 que envía la temperatura a la nube y se ve desde el celular. Aplicaciones: casas inteligentes, agricultura de precisión, industria 4.0, salud, ciudades inteligentes.

**Observaciones (ejemplos).**
- El Echo del HC-SR04 entrega 5 V; se usó un divisor de voltaje para proteger el ESP32 (3.3 V).
- El DHT11 no puede leerse más de una vez por segundo; lecturas más rápidas devuelven el valor anterior o error.
- El ultrasonido falla con superficies blandas, inclinadas o muy pequeñas.
- GPIO2 es un pin de arranque: con el DHT11 conectado a veces hay que presionar BOOT para subir el programa.

**Conclusiones (ejemplos).**
- Se midió distancia con el HC-SR04 calculándola a partir del tiempo de vuelo del ultrasonido, con un error aproximado de ±1 cm frente a una regla.
- Se leyó temperatura y humedad ambiental con el DHT11 mediante su protocolo digital de un solo hilo y la librería de Adafruit.
- Se integraron ambos sensores y una LCD en un solo ESP32, con una rutina de inicialización que verifica el equipo al encender.
- El ESP32, por tener Wi‑Fi, permite llevar estos datos a una aplicación IoT usando protocolos como MQTT.
