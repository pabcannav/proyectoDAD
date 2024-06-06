#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <WiFiUdp.h>
#include <PubSubClient.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

#define DHTTYPE DHT11

// Replace 0 by ID of this current device
const int DEVICE_ID = 1;
const int ID_SENSOR = 1;
const int ID_ACTUADOR = 1;
const int ID_GROUP = 1;

const int relayPin = 15;
const int dht11Pin = 13;

DHT dht(dht11Pin, DHTTYPE);

int test_delay = 1000; // so we don't spam the API
boolean describe_tests = true;

// Replace 0.0.0.0 by your server local IP (ipconfig [windows] or ifconfig [Linux o MacOS] gets IP assigned to your PC)
String serverName = "http://192.168.1.38:8084/";
HTTPClient http;

// Replace WifiName and WifiPassword by your WiFi credentials
#define STASSID "Red_MCJ"    //"Your_Wifi_SSID"
#define STAPSK "021Ca!lEte?29831Me7sA(" //"Your_Wifi_PASSWORD"

//#define STASSID "324"    //"Your_Wifi_SSID"
//#define STAPSK "EWtruz32ka" //"Your_Wifi_PASSWORD"
// MQTT configuration
WiFiClient espClient;
PubSubClient client(espClient);

// Server IP, where de MQTT broker is deployed
const char *MQTT_BROKER_ADRESS = "192.168.1.38";
const uint16_t MQTT_PORT = 1883;

// Name for this MQTT client
const char *MQTT_CLIENT_NAME = "ArduinoClient_"+DEVICE_ID;

String response;

String serializeSensorValueBody(int idSensor, int idGroup, double temperatura, double humedad)
{
  // StaticJsonObject allocates memory on the stack, it can be
  // replaced by DynamicJsonDocument which allocates in the heap.
  //
  DynamicJsonDocument doc(2048);

  // Add values in the document
  //
  // doc["idSensor"] = idSensor;
  // doc["timestamp"] = timestamp;
  // doc["value"] = value;
  // doc["removed"] = false;
  doc["idSensor"] = idSensor;
  doc["idGroup"] = idGroup;
  doc["temperatura"] = temperatura;
  doc["humedad"] = humedad;

  // Generate the minified JSON and send it to the Serial port.
  //
  String output;
  serializeJson(doc, output);
  Serial.println(output);

  return output;
}

String serializeActuatorStatusBody(int idActuador, int idGroup, bool activo)
{
  DynamicJsonDocument doc(2048);

  doc["idActuador"] = idActuador;
  doc["idGroup"] = idGroup;
  doc["activo"] = activo;
  //doc["removed"] = false;

  String output;
  serializeJson(doc, output);
  return output;
}

String serializeDeviceBody(String deviceSerialId, String name, String mqttChannel, int idGroup)
{
  DynamicJsonDocument doc(2048);

  doc["deviceSerialId"] = deviceSerialId;
  doc["name"] = name;
  doc["mqttChannel"] = mqttChannel;
  doc["idGroup"] = idGroup;

  String output;
  serializeJson(doc, output);
  return output;
}

void deserializeActuatorStatusBody(String responseJson)
{
  if (responseJson != "")
  {
    DynamicJsonDocument doc(2048);

    // Deserialize the JSON document
    DeserializationError error = deserializeJson(doc, responseJson);

    // Test if parsing succeeds.
    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // Fetch values.
    //int idActuatorState = doc["idActuatorState"];
    //float status = doc["status"];
    //bool statusBinary = doc["statusBinary"];
    //int idActuator = doc["idActuator"];
    //long timestamp = doc["timestamp"];
    int idActuador = doc["idActuador"];
    int idGroup = doc["idGroup"];
    bool activo = doc["activo"];

    Serial.println(("Actuator status deserialized: [idActuador: " + String(idActuador) + ", idGroup: " + String(idGroup) + ", activo: " + String(activo) + "]").c_str());
  }
}

void deserializeDeviceBody(int httpResponseCode)
{

  if (httpResponseCode > 0)
  {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    DynamicJsonDocument doc(2048);

    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    int idDevice = doc["idDevice"];
    String deviceSerialId = doc["deviceSerialId"];
    String name = doc["name"];
    String mqttChannel = doc["mqttChannel"];
    int idGroup = doc["idGroup"];

    Serial.println(("Device deserialized: [idDevice: " + String(idDevice) + ", name: " + name + ", deviceSerialId: " + deviceSerialId + ", mqttChannel" + mqttChannel + ", idGroup: " + idGroup + "]").c_str());
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}

void deserializeSensorsFromDevice(int httpResponseCode)
{

  if (httpResponseCode > 0)
  {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    // allocate the memory for the document
    DynamicJsonDocument doc(ESP.getMaxAllocHeap());

    // parse a JSON array
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // extract the values
    JsonArray array = doc.as<JsonArray>();
    for (JsonObject sensor : array)
    {
      int idSensor = sensor["idSensor"];
      int idGroup = sensor["idGroup"];
      double temperatura = sensor["temperatura"];
      double humedad = sensor["humedad"];

      Serial.println(("Sensor deserialized: [idSensor: " + String(idSensor) + ", idGroup: " + String(idGroup) + ", temperatura: " + String(temperatura) + ", humedad: " + String(humedad) + "]").c_str());
    }
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}

void deserializeActuatorsFromDevice(int httpResponseCode)
{

  if (httpResponseCode > 0)
  {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    // allocate the memory for the document
    DynamicJsonDocument doc(ESP.getMaxAllocHeap());

    // parse a JSON array
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // extract the values
    JsonArray array = doc.as<JsonArray>();
    for (JsonObject sensor : array)
    {
      int idActuador = sensor["idActuador"];
      int idGroup = sensor["idGroup"];
      bool activo = sensor["activo"];

      Serial.println(("Actuator deserialized: [idActuator: " + String(idActuador) + ", idGroup: " + String(idGroup) + ", activo: " + String(activo) + "]").c_str());
    }
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}

void test_response(int httpResponseCode)
{
  delay(test_delay);
  if (httpResponseCode > 0)
  {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String payload = http.getString();
    Serial.println(payload);
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}

void describe(char *description)
{
  if (describe_tests)
    Serial.println(description);
}

void GET_tests()
{
  // describe("Test GET full device info");
  String serverPath = serverName;
  // http.begin(serverPath.c_str());
  // // test_response(http.GET());
  // deserializeDeviceBody(http.GET());

  describe("Test GET sensors");
  serverPath = serverName + "api/sensores";
  http.begin(serverPath.c_str());
  deserializeSensorsFromDevice(http.GET());

  describe("Test GET sensores por idSensor");
  serverPath = serverName + "api/sensores/" + ID_SENSOR;
  http.begin(serverPath.c_str());
  deserializeSensorsFromDevice(http.GET());

  describe("Test GET sensores por idGroup");
  serverPath = serverName + "api/sensores/grupo/" + ID_GROUP;
  http.begin(serverPath.c_str());
  deserializeSensorsFromDevice(http.GET());

  describe("Test GET actuadores");
  serverPath = serverName + "api/actuadores";
  http.begin(serverPath.c_str());
  deserializeActuatorsFromDevice(http.GET());

  describe("Test GET actuadores por idActuador");
  serverPath = serverName + "api/actuadores/" + ID_ACTUADOR;
  http.begin(serverPath.c_str());
  deserializeActuatorsFromDevice(http.GET());

  describe("Test GET actuadores por idGroup");
  serverPath = serverName + "api/actuadores/grupo/" + ID_GROUP;
  http.begin(serverPath.c_str());
  deserializeActuatorsFromDevice(http.GET());

  // describe("Test GET sensors from deviceID and Type");
  // serverPath = serverName + "api/devices/" + String(DEVICE_ID) + "/sensors/Temperature";
  // http.begin(serverPath.c_str());
  // deserializeSensorsFromDevice(http.GET());

  // describe("Test GET actuators from deviceID");
  // serverPath = serverName + "api/devices/" + String(DEVICE_ID) + "/actuators/Relay";
  // http.begin(serverPath.c_str());
  // deserializeActuatorsFromDevice(http.GET());
}

void POST_tests()
{
  String actuator_states_body = serializeActuatorStatusBody(77,77,true);
  describe("Test POST actuadores");
  String serverPath = serverName + "api/actuadores";
  http.begin(serverPath.c_str());
  test_response(http.POST(actuator_states_body));

  sleep(10);

  String sensor_value_body = serializeSensorValueBody(77,77,70.1,70.2);
  describe("Test POST sensores");
  serverPath = serverName + "api/sensores";
  http.begin(serverPath.c_str());
  test_response(http.POST(sensor_value_body));

  // String device_body = serializeDeviceBody(String(DEVICE_ID), ("Name_" + String(DEVICE_ID)).c_str(), ("mqtt_" + String(DEVICE_ID)).c_str(), 12);
  // describe("Test POST with path and body and response");
  // serverPath = serverName + "api/device";
  // http.begin(serverPath.c_str());
  // test_response(http.POST(actuator_states_body));
}

void DELETE_tests(){
  describe("Test DELETE sensores");
  String serverPath = serverName + "api/sensores/"+77; //Aquí pongo 77 porque es el valor que estoy usando para probar los tests, pero podría ir el idSensor por ejemplo
  http.begin(serverPath.c_str());
  test_response(http.sendRequest("DELETE"));

  describe("Test DELETE actuadores");
  serverPath = serverName + "api/actuadores/"+77; //Aquí pongo 77 porque es el valor que estoy usando para probar los tests, pero podría ir el idActuador por ejemplo
  http.begin(serverPath.c_str());
  test_response(http.sendRequest("DELETE"));

}

float aux1 = 0.5;
bool aux2 = false;

void PUT_tests(){
  String actuator_states_body = serializeActuatorStatusBody(1,1,aux2);
  aux2 = !aux2; //Para que vayan cambiando los valores ya que estará en un loop
  describe("Test PUT actuadores");
  String serverPath = serverName + "api/actuadores";
  http.begin(serverPath.c_str());
  test_response(http.PUT(actuator_states_body));

  sleep(10);

  String sensor_value_body = serializeSensorValueBody(1,1,20.0*aux1,20.0*aux1);
  aux1 = (aux1 < 1) ? 2.0:0.5; //Esto es basicamente para que vayan cambiando los valores a los que se les hace el put, ya que está en un loop
  describe("Test PUT sensores");
  serverPath = serverName + "api/sensores";
  http.begin(serverPath.c_str());
  test_response(http.PUT(sensor_value_body));
}

void postOneSensor (String serializado){
  String serverPath = serverName + "api/sensores";
  http.begin(serverPath.c_str());
  test_response(http.POST(serializado));
}

void postOneActuador(String serializado){
  String serverPath = serverName + "api/actuadores";
  http.begin(serverPath.c_str());
  test_response(http.POST(serializado));
}

void putOneActuador(String serializado){
  String serverPath = serverName + "api/actuadores";
  http.begin(serverPath.c_str());
  test_response(http.PUT(serializado));
}

// callback a ejecutar cuando se recibe un mensaje
// en este ejemplo, muestra por serial el mensaje recibido
void OnMqttReceived(char *topic, byte *payload, unsigned int length)
{
   Serial.print("Received on ");
  Serial.print(topic);
  Serial.print(": ");
  String content = "";
  for (size_t i = 0; i < length; i++) {
    content.concat((char)payload[i]);
  }
  Serial.print(content);
  Serial.println();

  // Parsear el mensaje recibido
  DynamicJsonDocument doc(2048);
  deserializeJson(doc, content);
  int idActuador = doc["idActuador"];
  int idGroup = doc["idGroup"];
  bool activo = doc["activo"];
  
  if (activo) {
    digitalWrite(relayPin, HIGH);
    putOneActuador(serializeActuatorStatusBody(idActuador,idGroup,activo)); //Actualizamos el valor en la base de datos con un put, para no inundar de registros
  } else {
    digitalWrite(relayPin, LOW);
    putOneActuador(serializeActuatorStatusBody(idActuador,idGroup,activo));
  }
}

// inicia la comunicacion MQTT
// inicia establece el servidor y el callback al recibir un mensaje
void InitMqtt()
{
  client.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);
  client.setCallback(OnMqttReceived);
}




// Setup
void setup()
{
  Serial.begin(9600);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(STASSID);

  /* Explicitly set the ESP32 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }

  InitMqtt();

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println("Setup!");

  pinMode(relayPin,OUTPUT); //Salida digital del relé
  pinMode(dht11Pin,INPUT);  //Entrada digital del dht11
  dht.begin();
  postOneSensor(serializeSensorValueBody(ID_SENSOR,ID_GROUP,55.0,25.0));
  postOneActuador(serializeActuatorStatusBody(ID_ACTUADOR,ID_GROUP,false));
}

// conecta o reconecta al MQTT
// consigue conectar -> suscribe a topic y publica un mensaje
// no -> espera 5 segundos
void ConnectMqtt()
{
  Serial.print("Starting MQTT connection...");
  if (client.connect(MQTT_CLIENT_NAME)) {
    client.subscribe("instrucciones/rele");
    client.publish("estado/esp32", "connected");
  } else {
    Serial.print("Failed MQTT connection, rc=");
    Serial.print(client.state());
    Serial.println(" try again in 5 seconds");
    delay(5000);
  }
}

// gestiona la comunicación MQTT
// comprueba que el cliente está conectado
// no -> intenta reconectar
// si -> llama al MQTT loop
void HandleMqtt()
{
  if (!client.connected())
  {
    ConnectMqtt();
  }
  client.loop();
}



// Run the tests!
void loop()
{
  // GET_tests();
  // sleep(10);
  // POST_tests();
  // sleep(10);
  // DELETE_tests();
  // sleep(10);
  // PUT_tests();
  // sleep(10);

  HandleMqtt();

  double humedad = dht.readHumidity();
  double temperatura = dht.readTemperature();

  if (isnan(humedad) || isnan(temperatura)) {
    Serial.println(F("Failed to read from DHT sensor!"));
    return;
  }

  // Publicar datos del sensor
  DynamicJsonDocument doc(2048);
  doc["idSensor"] = ID_SENSOR;
  doc["idGroup"] = ID_GROUP;
  doc["temperatura"] = temperatura;
  doc["humedad"] = humedad;

  String output;
  serializeJson(doc, output);
  client.publish("datos/sensor", output.c_str());
  postOneSensor(output); //Subo también los datos del sensor a la base de datos.

  delay(3000);  
}
