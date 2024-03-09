#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

const int trigPin = 2;
const int echoPin = 15;
const int id = 1;
struct outputdata{
int id;
int distance;
int timestamp;
};
// defines variables
long duration;
int distance;
void setup() {
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPin, INPUT); // Sets the echoPin as an Input
    Serial.begin(115200);
  
  // Connect to Wi-Fi
  WiFi.begin("alex", "alexs007");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
}

struct outputdata dataCollection()
{
  // Clears the trigPin
  digitalWrite(trigPin, LOW);
  delayMicroseconds(10);
  
  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  // Reads the echoPin, returns the sound wave travel time in microseconds
  duration = pulseIn(echoPin, HIGH);
  // Calculating the distance
  distance = (duration * 0.0343) / 2;
  // Prints the distance on the Serial Monitor
  struct outputdata outputvar;
  outputvar.id = id;
  outputvar.distance = distance;
  outputvar.timestamp = millis();
  return outputvar;
}
void httpPost(struct outputdata x)
{
  
  StaticJsonDocument<200> jsonDocument; // Adjust size as needed
  jsonDocument["id"] = x.id;
  jsonDocument["distance"] = x.distance;
  jsonDocument["timestamp"] = x.timestamp;

  // Serialize JSON to string
  String jsonString;
  serializeJson(jsonDocument, jsonString);
  
  // Construct HTTP POST request
  HTTPClient http;
  http.begin("http://192.168.222.153:5000/data");
  http.addHeader("Content-Type", "application/json");

  int httpResponseCode = http.POST(jsonString);

  if (httpResponseCode > 0) {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
  } else {
    Serial.print("Error sending HTTP request: ");
    Serial.println(httpResponseCode);
  }

  http.end();

}
void loop() {
  httpPost(dataCollection());
  delay(2000);
}