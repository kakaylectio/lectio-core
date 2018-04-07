package com.kakay.lectio.rest;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.kakay.lectio.auth.IdentityAuthenticator;
import com.kakay.lectio.auth.LectioAuthorizer;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.auth.TokenAuthenticator;
import com.kakay.lectio.auth.WebTokenFilter;
import com.kakay.lectio.rest.exceptions.LectioExceptionMappers;
import com.kakay.lectio.rest.health.BrandNameHealthCheck;
import com.kakay.lectio.rest.resources.LoginResource;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.rest.resources.UserResource;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
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
        environment.jersey().register(new UserResource(lectioControl));

        IdentityAuthenticator idAuthenticator = new IdentityAuthenticator(lectioPersistence.getEm());
        TokenAuthenticator tokenAuthenticator = new TokenAuthenticator();
        LoginResource loginResource = new LoginResource(idAuthenticator, tokenAuthenticator);
        environment.jersey().register(loginResource);
//        environment.jersey().register(new AuthDynamicFeature(
//        		new BasicCredentialAuthFilter.Builder<LectioPrincipal>()
//        			.setAuthenticator(new IdentityAuthenticator())
//        			.setAuthorizer(new LectioAuthorizer())
//        			.setRealm(ORDINARY_REALM)
//        			.buildAuthFilter()));

        
        environment.jersey().register(new AuthDynamicFeature(
        		new WebTokenFilter.Builder()
        			.setAuthenticator(tokenAuthenticator)
        			.setAuthorizer(new LectioAuthorizer())
        			.setRealm(ORDINARY_REALM)
        			.buildAuthFilter()));

        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(LectioPrincipal.class));

        // Register the exception mappers.
        environment.jersey().register(new LectioExceptionMappers.LectioAuthorizationExceptionMapper());
        environment.jersey().register(new LectioExceptionMappers.LectioConstraintExceptionMapper());
        environment.jersey().register(new LectioExceptionMappers.LectioObjectNotFoundExceptionMapper());

        
        
 
        // CORS Settings to allow cross origin requests
        final FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, configuration.getAllowedOrigins());
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");

	
	}

}
