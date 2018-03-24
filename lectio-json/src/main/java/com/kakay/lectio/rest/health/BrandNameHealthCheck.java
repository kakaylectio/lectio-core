package com.kakay.lectio.rest.health;

import com.codahale.metrics.health.HealthCheck;

public class BrandNameHealthCheck extends HealthCheck {

	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}

}
