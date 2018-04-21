// Generates the SQL scripts for Lectio.
// Scripts are generated into the file specified with property javax.persistence.schema-generation.scripts.create-target
// in persistence.xml

package com.kakay.lectio.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.Environment;



/**
 * 
 * This class generates the SQL files for creating and dropping the database.
 * The scripts are generated into the working directory at the location
 * specified by PERSISTENCE_DROP_TARGET and PERSISTENCE_CREATE_TARGET.
 *
 */
public class AppGenSql {

	/**
	 * The persistence unit name in persistence.xml specifically set up
	 * just to generate the scripts.
	 */
	private static final String PERSISTENCE_UNIT_NAME_SCRIPT = "script-gen";
	
	
	/**
	 * The persistence unit name in persistence.xml set up for dropping and
	 * creating tables.  The dropping and creating tables is used for 
	 * development purposes to quickly recreate tables in new schema.  
	 * Not working right now. 
	 */
	private static final String PERSISTENCE_UNIT_NAME_TABLES = "tables";
	
	
	/**
	 * Location of the drop-table sql files relative to working directory
	 */
	private static final String PERSISTENCE_DROP_TARGET = "scripts/lectio-drop.ddl";

	/**
	 * Location of create-table sql files relative to working directory.
	 */
	private static final String PERSISTENCE_CREATE_TARGET = "scripts/lectio-create.ddl";


	/**
	 * Main entry point if you want to delete the old schema and create new ones.
	 * 
	 * @param args  Not used - required for main
	 */
	public static void main(String[] args) {
		try {
//			System.out.println("Dropping tables...");
//			dropTable(args);

			System.out.println("Removing old file...");
			removeOldSchema(args);

			System.out.println("Generating schema...");
			generateSchema(args);
			
//			System.out.println("Creating tables...");
//			createTable(args);
			System.out.println("Done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * Connect to the database and drop the table.  This is not actually
	 * working right now.
	 * 
	 * @param args  Not used.  Needs to be removed.
	 */
	static void dropTable(String[] args) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.schema-database.action",  "drop");
//		Persistence.generateSchema(PERSISTENCE_UNIT_NAME_TABLES, properties);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_TABLES);
		EntityManager em = emf.createEntityManager();
		em.close();
	}
	
	/**
	 * Generates the SQL file for creating tables in the database.  This is not working
	 * right now.
	 * 
	 * @param args  Not used.  Needs to be removed.
	 */
	static void createTable(String[] args) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.schema-database.action",  "create");
		Persistence.generateSchema(PERSISTENCE_UNIT_NAME_TABLES, properties);
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_TABLES);
		EntityManager em = emf.createEntityManager();
		em.close();
		
	}
	
	/**
	 * Delete the old SQL files.  Used for development.
	 * @param args Not used.
	 */
	static void removeOldSchema(String[] args) {
		File dropSchema = new File(PERSISTENCE_DROP_TARGET);
		if (dropSchema.exists()) {
			dropSchema.delete();
		}
		
		File createSchema = new File(PERSISTENCE_CREATE_TARGET);
		if (createSchema.exists()) {
			createSchema.delete();
		}
	}
	
	/**
	 * Create the new schema based on JPA
	 * 
	 * @param args Not used.
	 */
	static void generateSchema(String[] args) {
		try {
			Map<String, Object> properties = new HashMap<>();
			properties.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
            properties.put("javax.persistence.schema-generation.scripts.create-target", PERSISTENCE_CREATE_TARGET);
            properties.put("javax.persistence.schema-generation.scripts.drop-target", PERSISTENCE_DROP_TARGET);

            properties.put(Environment.HBM2DDL_AUTO, "validate");
			properties.put(Environment.HBM2DDL_IMPORT_FILES, "data.sql");
			Persistence.generateSchema(PERSISTENCE_UNIT_NAME_SCRIPT, properties);

			System.out.println("SQL create and drop scripts generated.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
