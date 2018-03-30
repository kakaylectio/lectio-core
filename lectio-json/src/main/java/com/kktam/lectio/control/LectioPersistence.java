package com.kktam.lectio.control;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class LectioPersistence {
	
	EntityManager em;
	protected static final String PERSISTENCE_UNIT_NAME = "lectio-tests";
	LectioControl lectioControl;

	public LectioPersistence() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();

		lectioControl = new LectioControl(em);
	}
	
	public void close() {
		em.close();
	}
	
	public LectioControl getLectioControlById() {
		return lectioControl;
	}
	

	public static void clearData(String secret) {
		// Check secret here
		LectioClearData clearDataObj = new LectioClearData();
		clearDataObj.clearData();
	}

	public EntityManager getEm() {
		return em;
	}
	

}
