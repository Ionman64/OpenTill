package com.opentill.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.opentill.idata.UserType;
import com.opentill.main.Config;
import com.opentill.models.BaseModels.BaseModelWithComments;

@Entity	(name = "User")
@Table( name = Config.DATABASE_TABLE_PREFIX + "operators")
public class UserModel extends BaseModelWithComments {
	public String name = null;
	public int type = UserType.UNKNOWN;
	public String email = null;
	public String telephone = null;
	public String passwordHash;
	public String code;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int i) {
		this.type = i;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
