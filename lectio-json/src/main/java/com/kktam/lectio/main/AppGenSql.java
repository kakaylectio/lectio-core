// Generates the SQL scripts for Lectio.
// Scripts are generated into the file specified with property javax.persistence.schema-generation.scripts.create-target
// in persistence.xml

package com.kktam.lectio.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;

import org.hibernate.cfg.Environment;

public class AppGenSql {

	private static final String PERSISTENCE_UNIT_NAME_SCRIPT = "script-gen";
	private static final String PERSISTENCE_UNIT_NAME_TABLES = "tables";
	private static final String PERSISTENCE_DROP_TARGET = "scripts/lectio-drop.ddl";
	private static final String PERSISTENCE_CREATE_TARGET = "scripts/lectio-create.ddl";

	public static void main(String[] args) {
		try {
			System.out.println("Dropping tables...");
			dropTable(args);

			System.out.println("Removing old file...");
			removeOldSchema(args);

			System.out.println("Generating schema...");
			generateSchema(args);
			
			System.out.println("Creating tables...");
			createTable(args);
			System.out.println("Done.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	static void dropTable(String[] args) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.schema-database.action",  "drop");
//		Persistence.generateSchema(PERSISTENCE_UNIT_NAME_TABLES, properties);
		Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_TABLES);

	}
	static void createTable(String[] args) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.schema-database.action",  "create");
		Persistence.generateSchema(PERSISTENCE_UNIT_NAME_TABLES, properties);
		
	}
	
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
