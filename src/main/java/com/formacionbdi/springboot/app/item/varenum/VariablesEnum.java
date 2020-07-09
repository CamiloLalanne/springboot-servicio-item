package com.formacionbdi.springboot.app.item.varenum;

public enum VariablesEnum {
	
	NOMBREAPI("application.name"),
	TITULO("API ITEMS");
	
	private String value;

	public String getValue() {
		return value;
	}

	VariablesEnum(String var){
		this.value = var;
	}
	
	

}
