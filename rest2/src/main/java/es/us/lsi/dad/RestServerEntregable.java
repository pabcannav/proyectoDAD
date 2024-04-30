package es.us.lsi.dad;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestServerEntregable extends AbstractVerticle {
	private Map<Integer,DHT11> sensores = new HashMap<>();
	private Map<Integer,Rele> actuadores = new HashMap<>();
	private Gson gson;
	
	public void start(Promise<Void> startFuture) {
		// Creating some synthetic data
		createSomeData(25);

		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		// Defining the router object
		Router router = Router.router(vertx);

		// Handling any server startup result
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		// Defining URI paths for each method in RESTful interface, including body
		// handling by /api/users* or /api/users/*
//		router.route("/api/users*").handler(BodyHandler.create());
//		router.get("/api/users").handler(this::getAllWithParams);
//		router.get("/api/users/:userid").handler(this::getOne);
//		router.post("/api/users").handler(this::addOne);
//		router.delete("/api/users/:userid").handler(this::deleteOne);
//		router.put("/api/users/:userid").handler(this::putOne);
		router.route("/api/sensores*").handler(BodyHandler.create());
		router.get("/api/sensores").handler(this::getAllSensores);
		router.post("/api/sensores").handler(this::addOneSensor);
		router.delete("/api/sensores/:sensorid").handler(this::deleteOneSensor);
		router.put("/api/sensores/:sensorid").handler(this::putOneSensor);
		
		router.route("/api/actuadores*").handler(BodyHandler.create());
		router.get("/api/actuadores").handler(this::getAllActuadores);
		router.post("/api/actuadores").handler(this::addOneActuador);
		router.delete("/api/actuadores/:actuadorid").handler(this::deleteOneActuador);
		router.put("/api/actuadores/:actuadorid").handler(this::putOneActuador);
	}

	private void getAllSensores(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(new DHT11ListWrapper(sensores.values())));
	}
	private void getAllActuadores(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(new ReleListWrapper(actuadores.values())));
	}

//	private void getAllWithParams(RoutingContext routingContext) {
//		final String name = routingContext.queryParams().contains("name") ? routingContext.queryParam("name").get(0) : null;
//		final String surname = routingContext.queryParams().contains("surname") ? routingContext.queryParam("surname").get(0) : null;
//		final String username = routingContext.queryParams().contains("username") ? routingContext.queryParam("username").get(0) : null;
//		
//		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
//				.end(gson.toJson(new UserEntityListWrapper(users.values().stream().filter(elem -> {
//					boolean res = true;
//					res = res && name != null ? elem.getName().equals(name) : true;
//					res = res && surname != null ? elem.getSurname().equals(surname) : true;
//					res = res && username != null ? elem.getUsername().equals(username) : true;
//					return res;
//				}).collect(Collectors.toList()))));
//	}
//
	private void getOneSensor(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("sensorid"));
		if (sensores.containsKey(id)) {
			DHT11 ds = sensores.get(id);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}
	}


	private void getOneActuador(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("actuadorid"));
		if (sensores.containsKey(id)) {
			Rele ds = actuadores.get(id);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(ds));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(204)
					.end();
		}
	}

	private void addOneSensor(RoutingContext routingContext) {
		final DHT11 sensor = gson.fromJson(routingContext.getBodyAsString(), DHT11.class);
		sensores.put(sensor.getIdSensor(), sensor);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(sensor));
	}
	
	private void addOneActuador(RoutingContext routingContext) {
		final Rele actuador = gson.fromJson(routingContext.getBodyAsString(), Rele.class);
		actuadores.put(actuador.getIdActuador(), actuador);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(actuador));
	}
//
	private void deleteOneSensor(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("sensorid"));
		if (sensores.containsKey(id)) {
			DHT11 sensor = sensores.get(id);
			sensores.remove(id);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(sensores));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}
	
	private void deleteOneActuador(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("actuadorid"));
		if (sensores.containsKey(id)) {
			Rele actuador = actuadores.get(id);
			actuadores.remove(id);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
					.end(gson.toJson(actuadores));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
					.end();
		}
	}

	private void putOneSensor(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("sensorid"));
		DHT11 ds = sensores.get(id);
		final DHT11 element = gson.fromJson(routingContext.getBodyAsString(), DHT11.class);
//		ds.setName(element.getName());
//		ds.setSurname(element.getSurname());
//		ds.setBirthdate(element.getBirthdate());
//		ds.setPassword(element.getPassword());
//		ds.setUsername(element.getUsername());
		ds.setHumedad(element.getHumedad());
		ds.setTemperatura(element.getTemperatura());
		sensores.put(ds.getIdSensor(), ds);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}
	
	private void putOneActuador(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("actuadorid"));
		Rele ds = actuadores.get(id);
		final Rele element = gson.fromJson(routingContext.getBodyAsString(), Rele.class);
//		ds.setName(element.getName());
//		ds.setSurname(element.getSurname());
//		ds.setBirthdate(element.getBirthdate());
//		ds.setPassword(element.getPassword());
//		ds.setUsername(element.getUsername());
		ds.setActivo(element.getActivo());
		actuadores.put(ds.getIdActuador(), ds);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(gson.toJson(element));
	}

	private void createSomeData(int number) {
		Random rnd = new Random();
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
//			users.put(id, new UserEntity(id, "Nombre_" + id, "Apellido_" + id,
//					new Date(Calendar.getInstance().getTimeInMillis() + id), "Username_" + id, "Password_" + id));
			sensores.put(id, new DHT11(id, id+1, id+10d, id+15d));
			actuadores.put(id, new Rele(id, id+1, id%2==0?true:false));
		});
	}
}
