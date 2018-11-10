package com.opentill.models;

import com.opentill.models.BaseModels.BaseModelWithComments;

public class DepartmentModel extends BaseModelWithComments  {
	public String name;
	public String shortHand;
	public String colour;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortHand() {
		return shortHand;
	}
	public void setShortHand(String shortHand) {
		this.shortHand = shortHand;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	
}
