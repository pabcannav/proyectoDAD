package mqtt;

import com.google.gson.Gson;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

public class MqttClientVerticle extends AbstractVerticle {
	Gson gson = new Gson();

	public void start(Promise<Void> startFuture) {
		//ApiService miGestorRest = new ApiService();

		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "localhost", s -> {

			mqttClient.subscribe("datos/sensor", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					System.out.println("Suscripción a datos/sensor");
				}
			});

			mqttClient.publishHandler(handler -> {
				System.out.println("Mensaje recibido:");
				System.out.println("    Topic: " + handler.topicName().toString());
				System.out.println("    Id del mensaje: " + handler.messageId());
				System.out.println("    Contenido: " + handler.payload().toString());

				// Parsear el JSON recibido
				String payload = handler.payload().toString();
				DHT11 sensorData = gson.fromJson(payload, DHT11.class);
				
				if (sensorData.getHumedad() < 50 || sensorData.getTemperatura() > 24) {
//					Rele estadoActuador = new Rele(null, null, null);
//					try {
//						estadoActuador = miGestorRest.getActuadorByIdGroup(sensorData.getIdGroup());
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if (!estadoActuador.getActivo()) { //Si ya está encendido, para que lo voy a estar encendiendo todo el rato
//						String response = gson.toJson(new Rele(estadoActuador.getIdActuador(), estadoActuador.getIdGroup(), true));
//						mqttClient.publish("instrucciones/rele", Buffer.buffer(response), MqttQoS.AT_LEAST_ONCE, false,
//								false);
//					}
						String response = gson.toJson(new Rele(sensorData.getIdGroup(), sensorData.getIdGroup(), true));
						mqttClient.publish("instrucciones/rele", Buffer.buffer(response), MqttQoS.AT_LEAST_ONCE, false,
								false);
				} else {
//					Rele estadoActuador = new Rele(null, null, null);
//					try {
//						estadoActuador = miGestorRest.getActuadorByIdGroup(sensorData.getIdGroup());
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if (estadoActuador.getActivo()) { //Si no esta encendido para que voy a estar apagandolo todo el rato
//						String response = gson.toJson(new Rele(estadoActuador.getIdActuador(), estadoActuador.getIdGroup(), true));
//						mqttClient.publish("instrucciones/rele", Buffer.buffer(response), MqttQoS.AT_LEAST_ONCE, false,
//								false);
//					}
					String response = gson.toJson(new Rele(sensorData.getIdGroup(), sensorData.getIdGroup(), false));
					mqttClient.publish("instrucciones/rele", Buffer.buffer(response), MqttQoS.AT_LEAST_ONCE, false,
							false);
				}
			});

			mqttClient.publish("estado/esp32", Buffer.buffer("Ejemplo"), MqttQoS.AT_LEAST_ONCE, false, false);
		});
	}


}
