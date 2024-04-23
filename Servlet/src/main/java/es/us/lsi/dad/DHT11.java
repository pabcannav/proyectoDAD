package es.us.lsi.dad;

import java.util.Objects;

public class DHT11 {
	private double temperatura;
	private double humedad;
	
	public DHT11(double temperatura, double humedad) {
		super();
		this.temperatura = temperatura;
		this.humedad = humedad;
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

	@Override
	public int hashCode() {
		return Objects.hash(humedad, temperatura);
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
		return Double.doubleToLongBits(humedad) == Double.doubleToLongBits(other.humedad)
				&& Double.doubleToLongBits(temperatura) == Double.doubleToLongBits(other.temperatura);
	}
	
	
	
}
