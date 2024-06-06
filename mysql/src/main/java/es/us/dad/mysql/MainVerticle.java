package es.us.dad.mysql;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class MainVerticle extends AbstractVerticle {

	MySQLPool mySqlClient;
	private Gson gson;

	@Override
	public void start(Promise<Void> startFuture) {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad").setUser("dadUser").setPassword("dadUser");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5000);

		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		// Defining the router object
		Router router = Router.router(vertx);

		// Handling any server startup result
		vertx.createHttpServer().requestHandler(router::handle).listen(8084, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		router.route("/api/sensores*").handler(BodyHandler.create());
		router.get("/api/sensores").handler(this::getAllSensores);
		router.get("/api/sensores/:sensorid").handler(this::getLastIdSensor);
		router.get("/api/sensores/grupo/:groupid").handler(this::getLastIdGroupSensores);
		router.post("/api/sensores").handler(this::postOneSensor);
		router.delete("/api/sensores/:sensorid").handler(this::deleteOneSensor);
		router.put("/api/sensores").handler(this::putOneSensor);

		router.route("/api/actuadores*").handler(BodyHandler.create());
		router.get("/api/actuadores").handler(this::getAllActuadores);
		router.get("/api/actuadores/:actuadorid").handler(this::getLastIdActuador);
		router.get("/api/actuadores/grupo/:groupid").handler(this::getLastIdGroupActuadores);
		router.post("/api/actuadores").handler(this::postOneActuador);
		router.delete("/api/actuadores/:actuadorid").handler(this::deleteOneActuador);
		router.put("/api/actuadores").handler(this::putOneActuador);

	}

	private void postOneSensor(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				final DHT11 sensor = gson.fromJson(routingContext.getBodyAsString(), DHT11.class);
				connection.result().preparedQuery(
						"INSERT INTO dad.sensores (idSensor, idGroup, temperatura, humedad) VALUES (?, ?, ?, ?)",
						Tuple.of(sensor.getIdSensor(), sensor.getIdGroup(), sensor.getTemperatura(),
								sensor.getHumedad()),
						res -> {
							if (res.succeeded()) {
								System.out.println("Sensor " + sensor.toString() + " añadido");
								routingContext.response().setStatusCode(201)
										.putHeader("content-type", "application/json; charset=utf-8")
										.end(gson.toJson(sensor));
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
						});
			} else {
				System.out.println("Error al obtener la conexión: " + connection.cause().getMessage());
				routingContext.response().setStatusCode(500).end();
			}
		});
	}
	private void postOneActuador(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				final Rele actuador = gson.fromJson(routingContext.getBodyAsString(), Rele.class);
				connection.result().preparedQuery(
						"INSERT INTO dad.actuadores (idActuador, idGroup, activo) VALUES (?, ?, ?)",
						Tuple.of(actuador.getIdActuador(),actuador.getIdGroup(),actuador.getActivo()),
						res -> {
							if (res.succeeded()) {
								System.out.println("Actuador " + actuador.toString() + " añadido");
								routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(gson.toJson(actuador));
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
						});
			} else {
				System.out.println("Error al obtener la conexión: " + connection.cause().getMessage());
				routingContext.response().setStatusCode(500).end();
			}
		});
	}

	private void putOneSensor(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				final DHT11 sensor = gson.fromJson(routingContext.getBodyAsString(), DHT11.class);
				connection.result().preparedQuery(
						"UPDATE sensores SET temperatura = ?, humedad = ? WHERE idSensor = ? AND idGroup = ?;",
						Tuple.of(sensor.getTemperatura(),sensor.getHumedad(),sensor.getIdSensor(),sensor.getIdGroup()),
						res -> {
							if (res.succeeded()) {
								System.out.println("Sensor " + sensor.toString() + " modificado");
								routingContext.response().setStatusCode(201)
										.putHeader("content-type", "application/json; charset=utf-8")
										.end(gson.toJson(sensor));
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
						});
			} else {
				System.out.println("Error al obtener la conexión: " + connection.cause().getMessage());
				routingContext.response().setStatusCode(500).end();
			}
		});
	}
	private void putOneActuador(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				final Rele actuador = gson.fromJson(routingContext.getBodyAsString(), Rele.class);
				connection.result().preparedQuery(
						"UPDATE dad.actuadores SET activo = ? WHERE idActuador = ? AND idGroup = ?;",
						Tuple.of(actuador.getActivo(),actuador.getIdActuador(),actuador.getIdGroup()),
						res -> {
							if (res.succeeded()) {
								System.out.println("Actuador " + actuador.toString() + " modificado");
								routingContext.response().setStatusCode(201)
								.putHeader("content-type", "application/json; charset=utf-8")
								.end(gson.toJson(actuador));
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
						});
			} else {
				System.out.println("Error al obtener la conexión: " + connection.cause().getMessage());
				routingContext.response().setStatusCode(500).end();
			}
		});
	}

	private void getAllSensores(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad.sensores;", res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new DHT11(elem.getInteger("idSensor"), elem.getInteger("idGroup"),
											elem.getDouble("temperatura"), elem.getDouble("humedad"))));
						}
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(result.encode());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});

	}
	private void getAllActuadores(RoutingContext routingContext) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad.actuadores;", res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new Rele(elem.getInteger("idActuador"), elem.getInteger("idGroup"), elem.getBoolean("activo"))));
						}
						System.out.println(result.toString());
						routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.setStatusCode(200).end(result.encode());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
		
	}

	private void getLastIdSensor(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("sensorid"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"SELECT * FROM dad.sensores WHERE idSensor = ? ORDER BY fecha_registro DESC LIMIT 1;",
						Tuple.of(id), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject
											.mapFrom(new DHT11(elem.getInteger("idSensor"), elem.getInteger("idGroup"),
													elem.getDouble("temperatura"), elem.getDouble("humedad"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(200).end(result.encode());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	private void getLastIdActuador(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("actuadorid"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"SELECT * FROM dad.actuadores WHERE idActuador = ? ORDER BY fecha_registro DESC LIMIT 1;",
						Tuple.of(id), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject
											.mapFrom(new Rele(elem.getInteger("idActuador"), elem.getInteger("idGroup"), elem.getBoolean("activo"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(result.encode());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	private void deleteOneSensor(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("sensorid"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"DELETE FROM sensores WHERE idSensor = ?;",
						Tuple.of(id), res -> {
							if (res.succeeded()) {
								// Get the result set
								String result = "Sensor "+id+" borrado con exito";
								System.out.println(result);
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(result);
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	private void deleteOneActuador(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("actuadorid"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"DELETE FROM dad.actuadores WHERE idActuador = ?;",
						Tuple.of(id), res -> {
							if (res.succeeded()) {
								// Get the result set
								String result = "Actuador "+id+" borrado con exito";
								System.out.println(result);
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(result);
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}

	private void getLastIdGroupSensores(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("groupid"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"SELECT * FROM dad.sensores WHERE idGroup = ? ORDER BY fecha_registro DESC LIMIT 1;",
						Tuple.of(id), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject
											.mapFrom(new DHT11(elem.getInteger("idSensor"), elem.getInteger("idGroup"),
													elem.getDouble("temperatura"), elem.getDouble("humedad"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
										.setStatusCode(200).end(result.encode());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}
	private void getLastIdGroupActuadores(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("groupid"));
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery(
						"SELECT * FROM dad.actuadores WHERE idGroup = ? ORDER BY fecha_registro DESC LIMIT 1;",
						Tuple.of(id), res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject
											.mapFrom(new Rele(elem.getInteger("idActuador"), elem.getInteger("idGroup"), elem.getBoolean("activo"))));
								}
								System.out.println(result.toString());
								routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
								.setStatusCode(200).end(result.encode());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
								routingContext.response().setStatusCode(500).end();
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}

	private void getAllWithConnection() {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query("SELECT * FROM dad.sensores;", res -> {
					if (res.succeeded()) {
						// Get the result set
						RowSet<Row> resultSet = res.result();
						System.out.println(resultSet.size());
						JsonArray result = new JsonArray();
						for (Row elem : resultSet) {
							result.add(JsonObject
									.mapFrom(new DHT11(elem.getInteger("idSensor"), elem.getInteger("idGroup"),
											elem.getDouble("temperatura"), elem.getDouble("humedad"))));
						}
						System.out.println(result.toString());
					} else {
						System.out.println("Error: " + res.cause().getLocalizedMessage());
					}
					connection.result().close();
				});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().query("SELECT * FROM sensores;", res -> {
//					if (res.succeeded()) {
//						// Get the result set
//						RowSet<Row> resultSet = res.result();
//						System.out.println(resultSet.size());
//						JsonArray result = new JsonArray();
//						for (Row elem : resultSet) {
//							result.add(JsonObject
//									.mapFrom(new UsuarioImpl(elem.getInteger("idusers"), elem.getString("name"),
//											elem.getString("surname"), localDateToDate(elem.getLocalDate("birthdate")),
//											elem.getString("username"), elem.getString("password"))));
//						}
//						System.out.println(result.toString());
//					} else {
//						System.out.println("Error: " + res.cause().getLocalizedMessage());
//					}
//					connection.result().close();
//				});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
	}

	private void getByIdSensor(Integer idSensor) {
		mySqlClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().preparedQuery("SELECT * FROM dad.sensores WHERE idSensor = ?", Tuple.of(idSensor),
						res -> {
							if (res.succeeded()) {
								// Get the result set
								RowSet<Row> resultSet = res.result();
								System.out.println(resultSet.size());
								JsonArray result = new JsonArray();
								for (Row elem : resultSet) {
									result.add(JsonObject
											.mapFrom(new DHT11(elem.getInteger("idSensor"), elem.getInteger("idGroup"),
													elem.getDouble("temperatura"), elem.getDouble("humedad"))));
								}
								System.out.println(result.toString());
							} else {
								System.out.println("Error: " + res.cause().getLocalizedMessage());
							}
							connection.result().close();
						});
			} else {
				System.out.println(connection.cause().toString());
			}
		});
	}

	private Date localDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}