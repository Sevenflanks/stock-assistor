package tw.org.sevenflanks.sa.stock.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import tw.org.sevenflanks.sa.base.data.GenericEntity;

@MappedSuperclass
public abstract class SyncDateEntity extends GenericEntity {

	@Column
	private LocalDate syncDate;

	public LocalDate getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(LocalDate syncDate) {
		this.syncDate = syncDate;
	}
}
