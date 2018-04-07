package com.kakay.lectio.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.logging.Logger;

import com.kakay.lectio.rest.exceptions.LectioSystemException;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.User;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * Authenticator used during login process to authenticate an email and password.
 *
 */
public class IdentityAuthenticator implements Authenticator<BasicCredentials, LectioPrincipal> {
	private final static Logger logger = Logger.getLogger(IdentityAuthenticator.class);

	EntityManager entityManager;
	/**
	 * @param entityManager Entity manager for connection to the database storing
	 *     username, email, and passwords.
	 */
	public IdentityAuthenticator(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * This method is called after a User has been created and stored in the database.
	 * This method stores the username and password information.
	 * 
	 * @param em Entity manager for connection to the database storing
	 *     username, email, and passwords.
	 * @param user  New user 
	 * @param password  User's password
	 */
	public static void setupNewIdentity(EntityManager em, User user, String password) {
		UserIdentity userIdentity = new UserIdentity();
		changeIdentityPassword(em, userIdentity, user, password);

	}

	/**
	 * Set up new identity or change the password for a user.  
	 * 
	 * @param em Entity manager for connection to the database storing
	 *     username, email, and passwords.
	 * @param user  New user 
	 * @param password  User's password
	 */
	public static void changeIdentity(EntityManager em, User user, String newpassword) {
		UserIdentity userIdentity = em.find(UserIdentity.class, user);
		changeIdentityPassword(em, userIdentity, user, newpassword);
	}

	/**
	 * Set up new identity or change the password for a user.  
	 * 
	 * @param em Entity manager for connection to the database storing
	 *     username, email, and passwords.
	 * @param userIdentity UserIdentity entry in database 
	 * @param password  User's password
	 */
	private static void changeIdentityPassword(EntityManager em, UserIdentity userIdentity, User user,
			String password) {
		String saltString = RandomStringUtils.randomAscii(32);
		String hashFodder = password + saltString;
		byte[] hashStore = DigestUtils.sha256(hashFodder);
		userIdentity.setUser(user);
		userIdentity.setPassword(hashStore);
		userIdentity.setSalt(saltString);
		em.persist(userIdentity);
	}

	/**
	 * Called by authentication process to check if user's password matches
	 * that in the database.  This method should be called after user ID is
	 * known.
	 * 
	 * @param em   EntityManager with connection to database storing user information
	 * @param userId   User's ID  
	 * @param password  User's password to check
	 * @return  True if password matches.
	 */
	public static boolean checkIdentity(EntityManager em, int userId, String password)  {
		TypedQuery<UserIdentity> userIdentityQuery = em.createNamedQuery(UserIdentity.QUERY_USERIDENTITY_BYUSERID, UserIdentity.class);
		userIdentityQuery.setParameter(UserIdentity.QUERYPARAM_USERIDENTITY_USERID, userId);
		List<UserIdentity> userIdentityList = userIdentityQuery.getResultList();

		// See if user exists. 
		if (userIdentityList.size() > 1) {
			throw new LectioSystemException("Duplicate user " + userId + " in database.");
		} else if (userIdentityList.size() < 1) {
			return false;
		}

		// There's only one user in the list.  Get that user.
		UserIdentity identity = userIdentityList.get(0);

		if (identity == null) {
			return false;
		}

		// Check that user has password hash and salt, and that they are the right length.
		if (identity.getPassword() == null || identity.getSalt() == null || identity.getPassword().length != 32
				|| identity.getSalt().length() != 32) {
			return false;
		}
		byte[] hashStoreFromIdentity = identity.getPassword();

		// Calculate the has password, then make sure they match the one calculated
		// from the client's password.
		String hashFodderFromPassword = password + identity.getSalt();
		byte[] hashStoreFromPassword = DigestUtils.sha256(hashFodderFromPassword);

		return java.util.Arrays.equals(hashStoreFromPassword, hashStoreFromIdentity);

	}

	/* (non-Javadoc)
	 * @see io.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
	 */
	@Override
	public Optional<LectioPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
		String username = credentials.getUsername();
		String password = credentials.getPassword();

		LectioPrincipal principal = checkStringCredentials(username, password);
		if (principal == null) {
			return Optional.empty();
		}
		else 
			return Optional.of(principal);

	}

	/**
	 * Called by DropWizard authenticate method to check username and password.
	 * @param username  This is really the email address.
	 * @param password
	 * @return  A Principal object containing user information.  This Principal
	 *     is later used to generate a token.
	 */
	public LectioPrincipal checkStringCredentials(String username, String password) {
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl control = lectioPersistence.getLectioControlById();
		User user;
		try {
			user = control.findUserByExactName(0, username);
			if (user == null) {
				user = control.findUserByEmail(0, username);
			}
			if (user == null) {
				return null;
			}

			if (checkIdentity(lectioPersistence.getEm(), user.getId(), password)) {
				LectioPrincipal principal = new LectioPrincipal(user.getEmail(), user.getId(), 
						new HashSet<Privilege>());
				return principal;
			}
		} catch (LectioException e) {
			logger.error("LectioException while searching for user name.", e);
			user = null;
		}
		return null;
	}
}
