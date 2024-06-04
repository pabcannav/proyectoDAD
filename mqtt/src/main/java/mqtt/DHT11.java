package mqtt;

import java.util.Objects;

public class DHT11 {
	protected Integer idSensor;
	protected Integer idGroup;
	protected Double temperatura;
	protected Double humedad;

	public DHT11(Integer idSensor, Integer idGroup, Double temperatura, Double humedad) {
		super();
		this.idSensor = idSensor;
		this.idGroup = idGroup;
		this.temperatura = temperatura;
		this.humedad = humedad;
	}

	public Integer getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
	}

	public Integer getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(Integer idGroup) {
		this.idGroup = idGroup;
	}

	public Double getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(Double temperatura) {
		this.temperatura = temperatura;
	}

	public Double getHumedad() {
		return humedad;
	}

	public void setHumedad(Double humedad) {
		this.humedad = humedad;
	}

	@Override
	public String toString() {
		return "DHT11 [idSensor=" + idSensor + ", idGroup=" + idGroup + ", temperatura=" + temperatura + ", humedad="
				+ humedad + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(humedad, idGroup, idSensor, temperatura);
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
		return Objects.equals(humedad, other.humedad) && Objects.equals(idGroup, other.idGroup)
				&& Objects.equals(idSensor, other.idSensor) && Objects.equals(temperatura, other.temperatura);
	}
}
