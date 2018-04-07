package com.kakay.lectio.auth;

import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Optional;

import org.glassfish.jersey.internal.util.Base64;
import org.jboss.logging.Logger;

import com.kakay.lectio.rest.exceptions.LectioSystemException;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * Class to authenticate a token
 *
 */
/**
 * @author kktam
 *
 */
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
	
	
	/**
	 * Used during login process to create a token to be used by the principal
	 * @param principal  Created by IdentityAuthenticator which was called before this call.
	 * @return  A token.  All subsequent client requests to resources must include this
	 * 			token in its authorization header.
	 */
	public LectioToken principal2Token(LectioPrincipal principal) {
		LectioToken token = new LectioToken();
		token.id = principal.getId();
		return token;
	}
	
	/**
	 * Converts a deserialized token into the equivalent lectioPrincipal
	 * This is not used.
	 * 
	 * @param token 
	 * @return
	 */
	public LectioPrincipal token2principal (LectioToken token) {
		LectioPrincipal lectioPrincipal = new LectioPrincipal("BogusName", token.id, new HashSet<Privilege>());
		return lectioPrincipal;
	}
	
	
	/**
	 * Returns a signed token as a string.  This is the string sent to the 
	 * client for subsequent authentication and authorication processes.
	 * 
	 * @param token Unsigned unserialized token
	 * @return Signed token as a String
	 * @throws LectioSystemException  Thrown for some fatal unexpected internal error.
	 * @throws GeneralSecurityException Thrown if access to the keystores fail
	 */
	public String serializeToken(LectioToken token) throws LectioSystemException, GeneralSecurityException {
		String tokenString = "" + token.id;
		
		String signedString = signer.signWithWebtoken(tokenString);
		
		return signedString;
	}
	
	/**
	 * 
	 * @param signedString  This is the signed token from the client.  This token
	 * 		should match the one that was returned by serializeToken.
	 * @return  If authentication passes, an unsigned deserialized token containing
	 *     user information is returned.  If authentication fails, null is returned.
	 */
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
	
	/**
	 * Once a principal is returned from the authentication process, this
	 * method creates a login response for the login process.
	 * @param lectioPrincipal  
	 * @return The LoginResponse serialized as JSON string to be returned 
	 *     to the client.
	 */
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
