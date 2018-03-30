package com.kakay.lectio.auth;

import io.dropwizard.auth.Authorizer;

public class LectioAuthorizer implements Authorizer<LectioPrincipal> {

	public LectioAuthorizer() {
	}

	@Override
	public boolean authorize(LectioPrincipal principal, String privilege) {
		return true;
	}

}
