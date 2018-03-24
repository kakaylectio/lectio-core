package com.kakay.lectio.rest;

import com.kakay.lectio.rest.health.BrandNameHealthCheck;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsWithLessonsResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class LectioRestApplication extends Application<LectioRestConfiguration> {
    public static void main(String[] args) throws Exception {
        new LectioRestApplication().run(args);
    }

	@Override
	public void run(LectioRestConfiguration configuration, Environment environment) throws Exception {
		
		LectioRestControl control = new LectioRestControl();
		
		
		
		BrandNameHealthCheck brandNameHealthCheck = new BrandNameHealthCheck();
		environment.healthChecks().register("brandName", brandNameHealthCheck);
		
        environment.jersey().register(new NotebookActiveTopicsResource(control));
        environment.jersey().register(new NotebookActiveTopicsWithLessonsResource(control));

	}

}
