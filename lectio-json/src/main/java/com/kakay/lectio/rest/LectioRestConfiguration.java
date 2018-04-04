package com.kakay.lectio.rest;

import org.hibernate.validator.constraints.NotEmpty;

import io.dropwizard.Configuration;

public class LectioRestConfiguration extends Configuration {

    @NotEmpty
    private String studio;

    @NotEmpty
    private String brandName = "Gangqinke";
    
    @NotEmpty
    private String allowedOrigins;

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}
    
    

}
