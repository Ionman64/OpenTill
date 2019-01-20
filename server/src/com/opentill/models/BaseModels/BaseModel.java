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
    public int version;
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int deleted;
	
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
		return deleted == 1;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted ? 1 : 0;
	}
}
