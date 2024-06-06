package mqtt;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8084/api";
    private HttpClient client;
    private Gson gson;

    public ApiService() {
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    public DHT11 getSensorById(int sensorId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sensores/" + sensorId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body().replace("[", "").replace("]", ""), DHT11.class);
        } else {
            throw new RuntimeException("Error al obtener el sensor: " + response.body());
        }
    }
    
    public DHT11 getSensorByIdGroup(int groupId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sensores/grupo/" + groupId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body().replace("[", "").replace("]", ""), DHT11.class);
        } else {
            throw new RuntimeException("Error al obtener el sensor: " + response.body());
        }
    }

    public Rele getActuadorById(int actuadorId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/actuadores/" + actuadorId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body().replace("[", "").replace("]", ""), Rele.class);
        } else {
            throw new RuntimeException("Error al obtener el actuador: " + response.body());
        }
    }
    
    public Rele getActuadorByIdGroup(int groupId) throws Exception {
    	Rele res = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/actuadores/grupo/" + groupId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            res = gson.fromJson(response.body().replace("[", "").replace("]", ""), Rele.class); 
//            como devuelve una lista de json, pero solo hay un elemento por la condición de la petición que estoy haciendo
//            hago los replaces para quedarme con el objeto y así gson pueda formatearlo correctamente
            
        }else {
        	throw new RuntimeException("Error al obtener el actuador: " + response.body());
		}
        return res;
    }

    public void postSensor(DHT11 sensor) throws Exception {
        String json = gson.toJson(sensor);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sensores"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Error al agregar el sensor: " + response.body());
        }
    }

    public void postActuador(Rele actuador) throws Exception {
        String json = gson.toJson(actuador);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/actuadores"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Error al agregar el actuador: " + response.body());
        }
    }

    public void updateSensor(DHT11 sensor) throws Exception {
        String json = gson.toJson(sensor);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sensores"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Error al actualizar el sensor: " + response.body());
        }
    }

    public void updateActuador(Rele actuador) throws Exception {
        String json = gson.toJson(actuador);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/actuadores"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Error al actualizar el actuador: " + response.body());
        }
    }

    public void deleteSensor(int sensorId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sensores/" + sensorId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al eliminar el sensor: " + response.body());
        }
    }

    public void deleteActuador(int actuadorId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/actuadores/" + actuadorId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al eliminar el actuador: " + response.body());
        }
    }
}

