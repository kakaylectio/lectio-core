package com.kakay.lectio.auth;

import java.util.HashSet;
import java.util.Optional;

import org.glassfish.jersey.internal.util.Base64;
import org.jboss.logging.Logger;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class TokenAuthenticator implements Authenticator<String, LectioPrincipal> {
	private final static Logger logger = Logger.getLogger(TokenAuthenticator.class);

	public TokenAuthenticator() {
	}
	
	public void loadKeystore() {
		LectioKeystore keystore = LectioKeystore.getLectioKeystoreInstance();
		if (keystore == null) {
			logger.debug("Keystore is null.");
			return;
		}
		logger.debug("Keystore alias = " + keystore.getWebtokenAlias());
	}

	@Override
	public Optional<LectioPrincipal> authenticate(String credentials) throws AuthenticationException {
		LectioToken token = deserializeToken(credentials);
		if (token != null)
		{
			LectioPrincipal lectioPrincipal = new LectioPrincipal("Name", token.id, new HashSet<Privilege>());
			return Optional.of(lectioPrincipal);
		}
		return Optional.empty();
		
	}
	
	public LectioToken principal2Token(LectioPrincipal principal) {
		LectioToken token = new LectioToken();
		token.id = principal.getId();
		return token;
	}
	
	public LectioPrincipal token2principal (LectioToken token) {
		LectioPrincipal lectioPrincipal = new LectioPrincipal("BogusName", token.id, new HashSet<Privilege>());
		return lectioPrincipal;
	}
	
	public String serializeToken(LectioToken token) {
		String tokenString = "" + token.id + ":obvious-password";
		// TODO replace this with more information and encryption.
		
		String encodedString = Base64.encodeAsString(tokenString);
		return encodedString;
	}
	
	public LectioToken deserializeToken(String tokenString) {
		String decodedString = Base64.decodeAsString(tokenString);
		
		
		String[] decodedStringSplit = decodedString.split(":", 2);
		if (decodedStringSplit.length < 2) {
            return null;
		}
		
		if (!decodedStringSplit[1].equals("obvious-password")) {
			return null;
		}
		
		LectioToken lectioToken = new LectioToken();
		try {
			lectioToken.id = Integer.parseInt(decodedStringSplit[0]);
		}
		catch(NumberFormatException e) {
            return null;
		}
		lectioToken.stupidSecurityHole  = decodedStringSplit[1];
		return lectioToken;
	}
	
	public LoginResponse principal2tokenContent(LectioPrincipal lectioPrincipal) {
		LectioToken token = principal2Token(lectioPrincipal);
		String tokenString = serializeToken(token);
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken(tokenString);
		loginResponse.setName(lectioPrincipal.getName());
		loginResponse.setExpiration("Nothing right now");
		return loginResponse;
	}
}
