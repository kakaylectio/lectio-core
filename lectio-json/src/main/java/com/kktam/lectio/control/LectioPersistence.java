package com.kktam.lectio.control;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class LectioPersistence {
	
	EntityManager em;
	protected static final String PERSISTENCE_UNIT_NAME = "lectio-tests";
	LectioControlById lectioControl;

	public LectioPersistence() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();

		lectioControl = new LectioControlById(em);
	}
	
	public void close() {
		em.close();
	}
	
	public LectioControlById getLectioControlById() {
		return lectioControl;
	}
	

	public static void clearData(String secret) {
		// Check secret here
		LectioClearData clearDataObj = new LectioClearData();
		clearDataObj.clearData();
	}
	

}
