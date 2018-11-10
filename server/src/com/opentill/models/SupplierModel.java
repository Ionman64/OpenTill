package com.opentill.models;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.opentill.main.Config;
import com.opentill.models.BaseModels.BaseModelWithComments;

@Entity	(name = "Supplier")
@Table( name = Config.DATABASE_TABLE_PREFIX + "tblsuppliers")
public class SupplierModel extends BaseModelWithComments {
	public String name;
	public String telephone;
	public String website;
	public String email;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
