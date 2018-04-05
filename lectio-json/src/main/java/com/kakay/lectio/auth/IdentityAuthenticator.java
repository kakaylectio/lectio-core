package com.kakay.lectio.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.logging.Logger;

import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.User;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class IdentityAuthenticator implements Authenticator<BasicCredentials, LectioPrincipal> {
	private final static Logger logger = Logger.getLogger(IdentityAuthenticator.class);

	EntityManager entityManager;
	public IdentityAuthenticator(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public static void setupNewIdentity(EntityManager em, User user, String password) {
		UserIdentity userIdentity = new UserIdentity();
		changeIdentityPassword(em, userIdentity, user, password);

	}

	public static void changeIdentity(EntityManager em, User user, String newpassword) {
		UserIdentity userIdentity = em.find(UserIdentity.class, user);
		changeIdentityPassword(em, userIdentity, user, newpassword);
	}

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

	public static boolean checkIdentity(EntityManager em, int userId, String password) throws LectioException {
		TypedQuery<UserIdentity> userIdentityQuery = em.createNamedQuery(UserIdentity.QUERY_USERIDENTITY_BYUSERID, UserIdentity.class);
		userIdentityQuery.setParameter(UserIdentity.QUERYPARAM_USERIDENTITY_USERID, userId);
		List<UserIdentity> userIdentityList = userIdentityQuery.getResultList();
		if (userIdentityList.size() > 1) {
			throw new LectioException("Duplicate user " + userId + " in database.");
		} else if (userIdentityList.size() < 1) {
			return false;
		}

		UserIdentity identity = userIdentityList.get(0);

		if (identity == null) {
			return false;
		}
		if (identity.getPassword() == null || identity.getSalt() == null || identity.getPassword().length != 32
				|| identity.getSalt().length() != 32) {
			return false;
		}
		byte[] hashStoreFromIdentity = identity.getPassword();

		String hashFodderFromPassword = password + identity.getSalt();
		byte[] hashStoreFromPassword = DigestUtils.sha256(hashFodderFromPassword);

		return java.util.Arrays.equals(hashStoreFromPassword, hashStoreFromIdentity);

	}

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
