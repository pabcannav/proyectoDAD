package es.us.lsi.dad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ReleListWrapper {
	private List<Rele> actuadoresList;

	
	public ReleListWrapper(Collection<Rele> actuadoresList) {
		super();
		this.actuadoresList = new ArrayList<>(actuadoresList);
	}
	
	public ReleListWrapper(List<Rele> actuadoresList) {
		super();
		this.actuadoresList = new ArrayList<>(actuadoresList);
	}

	public List<Rele> getActuadoresList() {
		return actuadoresList;
	}

	public void setActuadoresList(List<Rele> actuadoresList) {
		this.actuadoresList = actuadoresList;
	}

	@Override
	public String toString() {
		return "ReleListWrapper [actuadoresList=" + actuadoresList + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(actuadoresList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReleListWrapper other = (ReleListWrapper) obj;
		return Objects.equals(actuadoresList, other.actuadoresList);
	}
	
	
}
