package com.opentill.models;

import com.opentill.main.Utils;

public class UserAuthModel {
	public String email = null;
	public String password = null;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHashedPassword() {
		// TODO Auto-generated method stub
		return Utils.hashPassword(this.password, "");
	}
	public boolean isValid() {
		if (Utils.isNull(this.email) || Utils.isNull(this.password)) {
			return false;
		}
		if (this.email.isEmpty() || this.password.isEmpty()) {
			return false;
		}
		if (!Utils.isValidEmail(this.email)) {
			return false;
		}
		return true;
	}
}
