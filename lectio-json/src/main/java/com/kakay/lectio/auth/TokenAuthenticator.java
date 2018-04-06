package com.kakay.lectio.auth;

import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Optional;

import org.glassfish.jersey.internal.util.Base64;
import org.jboss.logging.Logger;

import com.kakay.lectio.rest.exceptions.LectioSystemException;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class TokenAuthenticator implements Authenticator<String, LectioPrincipal> {
	private final static Logger logger = Logger.getLogger(TokenAuthenticator.class);

	private LectioSigner signer;
	public TokenAuthenticator() {
		signer = LectioSigner.getInstance();
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
	
	/* Used during login process */
	public LectioToken principal2Token(LectioPrincipal principal) {
		LectioToken token = new LectioToken();
		token.id = principal.getId();
		return token;
	}
	
	public LectioPrincipal token2principal (LectioToken token) {
		LectioPrincipal lectioPrincipal = new LectioPrincipal("BogusName", token.id, new HashSet<Privilege>());
		return lectioPrincipal;
	}
	
	public String serializeToken(LectioToken token) throws LectioSystemException, GeneralSecurityException {
		String tokenString = "" + token.id;
		
		String signedString = signer.signWithWebtoken(tokenString);
		
		return signedString;
	}
	
	public LectioToken deserializeToken(String signedString) {
		
		String tokenString = signer.verifySignatureWithWebtoken(signedString);
		
		LectioToken lectioToken = new LectioToken();
		try {
			lectioToken.id = Integer.parseInt(tokenString);
		}
		catch(NumberFormatException e) {
            return null;
		}
		return lectioToken;
	}
	
	public LoginResponse principal2loginResponse(LectioPrincipal lectioPrincipal) {
		LectioToken token = principal2Token(lectioPrincipal);
		try {
			String tokenString = serializeToken(token);
			LoginResponse loginResponse = new LoginResponse();
			loginResponse.setToken(tokenString);
			loginResponse.setName(lectioPrincipal.getName());
			loginResponse.setExpiration("Nothing right now");
			loginResponse.setUserId(lectioPrincipal.getId());
			return loginResponse;
		} catch (LectioSystemException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
