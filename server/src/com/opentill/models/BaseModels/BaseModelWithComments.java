package com.opentill.models.BaseModels;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseModelWithComments extends BaseModel {
	public String comments;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
