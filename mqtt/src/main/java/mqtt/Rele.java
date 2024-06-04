package mqtt;

import java.util.Objects;

public class Rele {
	protected Integer idActuador;
	protected Integer idGroup;
	protected Boolean activo; // Si el relé está activo true, si no false
	
	public Rele(Integer idActuador, Integer idGroup, Boolean activo) {
		super();
		this.idActuador = idActuador;
		this.idGroup = idGroup;
		this.activo = activo;
	}

	public Integer getIdActuador() {
		return idActuador;
	}

	public void setIdActuador(Integer idActuador) {
		this.idActuador = idActuador;
	}

	public Integer getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(Integer idGroup) {
		this.idGroup = idGroup;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@Override
	public String toString() {
		return "Rele [idActuador=" + idActuador + ", idGroup=" + idGroup + ", activo=" + activo + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(activo, idActuador, idGroup);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rele other = (Rele) obj;
		return Objects.equals(activo, other.activo) && Objects.equals(idActuador, other.idActuador)
				&& Objects.equals(idGroup, other.idGroup);
	}
}
