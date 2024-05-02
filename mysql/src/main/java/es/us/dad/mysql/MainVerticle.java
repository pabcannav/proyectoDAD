package es.us.dad.mysql;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class MainVerticle extends AbstractVerticle {

	MySQLPool mySqlClient;

	@Override
	public void start(Promise<Void> startFuture) {
//		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
//				.setDatabase("ejemplodad").setUser("root").setPassword("rootroot");
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad").setUser("dadUser").setPassword("dadUser");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		getAll();

		getAllWithConnection();

		for (int i = 1; i < 4; i++) {
			getByIdSensor(i);;
		}

	}
	
	private void getAll() {
		mySqlClient.query("SELECT * FROM dad.sensores;", res -> {
			if (res.succeeded()) {
				// Get the result set
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size());
				JsonArray result = new JsonArray();
				for (Row elem : resultSet) {
					result.add(JsonObject.mapFrom(new DHT11(elem.getInteger("idSensor"), elem.getInteger("idGroup"),
							elem.getDouble("temperatura"), elem.getDouble("humedad"))));
				}
				System.out.println(result.toString());
			} else {
				System.out.println("Error: " + res.cause().getLocalizedMessage());
			}
		});
//		mySqlClient.query("SELECT * FROM ejemplodad.users;", res -> {
//			if (res.succeeded()) {
//				// Get the result set
//				RowSet<Row> resultSet = res.result();
//				System.out.println(resultSet.size());
//				JsonArray result = new JsonArray();
//				for (Row elem : resultSet) {
//					result.add(JsonObject.mapFrom(new UsuarioImpl(elem.getInteger("idusers"), elem.getString("name"),
//							elem.getString("surname"), localDateToDate(elem.getLocalDate("birthdate")),
//							elem.getString("username"), elem.getString("password"))));
//				}
//				System.out.println(result.toString());
//			} else {
//				System.out.println("Error: " + res.cause().getLocalizedMessage());
//			}
//		});
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
				connection.result().preparedQuery("SELECT * FROM dad.sensores WHERE idSensor = ?",
						Tuple.of(idSensor), res -> {
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
//	private void getByName(String username) {
//		mySqlClient.getConnection(connection -> {
//			if (connection.succeeded()) {
//				connection.result().preparedQuery("SELECT * FROM ejemplodad.users WHERE username = ?",
//						Tuple.of(username), res -> {
//							if (res.succeeded()) {
//								// Get the result set
//								RowSet<Row> resultSet = res.result();
//								System.out.println(resultSet.size());
//								JsonArray result = new JsonArray();
//								for (Row elem : resultSet) {
//									result.add(JsonObject.mapFrom(new UsuarioImpl(elem.getInteger("idusers"),
//											elem.getString("name"), elem.getString("surname"),
//											localDateToDate(elem.getLocalDate("birthdate")), elem.getString("username"),
//											elem.getString("password"))));
//								}
//								System.out.println(result.toString());
//							} else {
//								System.out.println("Error: " + res.cause().getLocalizedMessage());
//							}
//							connection.result().close();
//						});
//			} else {
//				System.out.println(connection.cause().toString());
//			}
//		});
//	}

	private Date localDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
