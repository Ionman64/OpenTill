package com.opentill.models.BaseModels;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@MappedSuperclass
public abstract class BaseModel {
	
	@Id
	public String id;

	@Column(name = "created")
	//@Temporal(TemporalType.TIMESTAMP)
	public int created;
	
	@Column(name = "updated")
	//@Temporal(TemporalType.TIMESTAMP)
	public int updated;
	
	@Version
    private Long version;
	
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public boolean deleted;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
	public int getUpdated() {
		return updated;
	}
	public void setUpdated(int updated) {
		this.updated = updated;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
