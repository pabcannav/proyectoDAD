package es.us.lsi.dad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DHT11ListWrapper {
	private List<DHT11> sensoresList;

	public DHT11ListWrapper(Collection<DHT11> sensoresList) {
		super();
		this.sensoresList = new ArrayList<>(sensoresList);
	}
	
	public DHT11ListWrapper(List<DHT11> sensoresList) {
		super();
		this.sensoresList = new ArrayList<>(sensoresList);
	}

	public List<DHT11> getSensoresList() {
		return sensoresList;
	}

	public void setSensoresList(List<DHT11> sensoresList) {
		this.sensoresList = sensoresList;
	}
	
	public void addSensor(DHT11 e) {
		this.sensoresList.add(e);
	}

	@Override
	public String toString() {
		return "DHT11ListWrapper [sensoresList=" + sensoresList + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(sensoresList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DHT11ListWrapper other = (DHT11ListWrapper) obj;
		return Objects.equals(sensoresList, other.sensoresList);
	}
	
	
}
