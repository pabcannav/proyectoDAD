package es.us.lsi.dad;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DHT11 {

	private int idSensor;
	private double temperatura;
	private double humedad;

	public int getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

	public double getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(double temperatura) {
		this.temperatura = temperatura;
	}

	public double getHumedad() {
		return humedad;
	}

	public void setHumedad(double humedad) {
		this.humedad = humedad;
	}

	public List<Double> getValores() {
		List<Double> res = new ArrayList<>();
		res.add(temperatura);
		res.add(humedad);
		return res;
	}

	public DHT11(int idSensor, double temperatura, double humedad) {
		super();
		this.idSensor = idSensor;
		this.temperatura = temperatura;
		this.humedad = humedad;
	}

	@Override
	public int hashCode() {
		return Objects.hash(humedad, idSensor, temperatura);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DHT11 other = (DHT11) obj;
		return Double.doubleToLongBits(humedad) == Double.doubleToLongBits(other.humedad) && idSensor == other.idSensor
				&& Double.doubleToLongBits(temperatura) == Double.doubleToLongBits(other.temperatura);
	}

	@Override
	public String toString() {
		return "DHT11 [idSensor=" + idSensor + ", temperatura=" + temperatura + ", humedad=" + humedad + "]";
	}

}
