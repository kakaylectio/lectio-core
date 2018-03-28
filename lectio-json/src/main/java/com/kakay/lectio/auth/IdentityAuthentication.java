package com.kakay.lectio.auth;

import javax.persistence.EntityManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.kktam.lectio.model.User;

public class IdentityAuthentication {

	private IdentityAuthentication() {
	}
	
	public static void setupNewIdentity(EntityManager em, User user, String password) {
		UserIdentity userIdentity = new UserIdentity();
		changeIdentityPassword(em, userIdentity, user, password);
		
	}
	
	public static void changeIdentity(EntityManager em, User user, String newpassword) {
		UserIdentity userIdentity = em.find(UserIdentity.class,  user);
		changeIdentityPassword(em, userIdentity, user, newpassword);
	}
	
	private static void changeIdentityPassword(EntityManager em, UserIdentity userIdentity, User user, String password) {
		String saltString = RandomStringUtils.random(32);
		String hashFodder = password + saltString;
		byte[] hashStore = DigestUtils.sha256(hashFodder);
		userIdentity.setUser(user);
		userIdentity.setPassword(hashStore);
		userIdentity.setSalt(saltString);
		em.persist(userIdentity);
	}		
	
	public static boolean checkIdentity(EntityManager em, int userId, String password) {
		UserIdentity identity = em.find(UserIdentity.class,  userId);
		if (identity == null) {
			return false;
		}
		if (identity.getPassword() == null || identity.getSalt() == null 
				|| identity.getPassword().length != 32 || identity.getSalt().length() != 32) 
		{
			return false;
		}
		byte[] hashStoreFromIdentity = identity.getPassword();
		 
		String hashFodderFromPassword = password = identity.getSalt();
		byte[] hashStoreFromPassword = DigestUtils.sha256(hashFodderFromPassword);
		
		return java.util.Arrays.equals(hashStoreFromPassword, hashStoreFromIdentity);
				
	}
}
