package com.kakay.lectio.rest;

import com.kakay.lectio.rest.health.BrandNameHealthCheck;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsWithLessonsResource;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class LectioRestApplication extends Application<LectioRestConfiguration> {
    public static void main(String[] args) throws Exception {
        new LectioRestApplication().run(args);
    }

	@Override
	public void run(LectioRestConfiguration configuration, Environment environment) throws Exception {
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		
		
		BrandNameHealthCheck brandNameHealthCheck = new BrandNameHealthCheck();
		environment.healthChecks().register("brandName", brandNameHealthCheck);
		
        environment.jersey().register(new NotebookActiveTopicsResource(lectioControl));
        environment.jersey().register(new NotebookActiveTopicsWithLessonsResource(lectioControl));

	}

}
