package tw.org.sevenflanks.sa.base.data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class GenericEntity implements Persistable<Long> {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@Column
	private LocalDateTime createdTime;

	@Column
	private LocalDateTime updatedTime;

	@PrePersist
	protected void onCreate() {
		this.createdTime = LocalDateTime.now();
		this.updatedTime = this.createdTime;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedTime = LocalDateTime.now();
	}

	@Override
	public boolean isNew() {
		return id == null;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}
}
