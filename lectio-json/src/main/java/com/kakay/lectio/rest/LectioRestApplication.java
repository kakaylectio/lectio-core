package com.kakay.lectio.rest;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.kakay.lectio.auth.IdentityAuthenticator;
import com.kakay.lectio.auth.LectioAuthorizer;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.rest.exceptions.LectioExceptionMappers;
import com.kakay.lectio.rest.health.BrandNameHealthCheck;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;

public class LectioRestApplication extends Application<LectioRestConfiguration> {
	
	public final static String ORDINARY_REALM  = "Lectio Realm";
    public static void main(String[] args) throws Exception {
        new LectioRestApplication().run(args);
    }

	@Override
	public void run(LectioRestConfiguration configuration, Environment environment) throws Exception {
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		
		
		BrandNameHealthCheck brandNameHealthCheck = new BrandNameHealthCheck();
		environment.healthChecks().register("brandName", brandNameHealthCheck);
		
		NotebookActiveTopicsResource notebookActiveTopicResource = new NotebookActiveTopicsResource();
		notebookActiveTopicResource.setLectioControl(lectioControl);
        environment.jersey().register(notebookActiveTopicResource);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(LectioPrincipal.class));

        environment.jersey().register(new AuthDynamicFeature(
        		new BasicCredentialAuthFilter.Builder<LectioPrincipal>()
        			.setAuthenticator(new IdentityAuthenticator())
        			.setAuthorizer(new LectioAuthorizer())
        			.setRealm(ORDINARY_REALM)
        			.buildAuthFilter()));

        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(LectioPrincipal.class));

        // Register the exception mappers.
        environment.jersey().register(new LectioExceptionMappers.LectioAuthorizationExceptionMapper());
        environment.jersey().register(new LectioExceptionMappers.LectioConstraintExceptionMapper());
        environment.jersey().register(new LectioExceptionMappers.LectioObjectNotFoundExceptionMapper());
	}

}
