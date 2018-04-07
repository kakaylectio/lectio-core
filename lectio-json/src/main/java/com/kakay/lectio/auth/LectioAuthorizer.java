package com.kakay.lectio.auth;

import io.dropwizard.auth.Authorizer;

/**
 * Class used in HTTP request filtering process to check if user
 * is authorized to use an API.
 *
 */
public class LectioAuthorizer implements Authorizer<LectioPrincipal> {

	public LectioAuthorizer() {
	}

	@Override
	public boolean authorize(LectioPrincipal principal, String privilege) {
		return true;
	}

}
