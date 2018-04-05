package com.kakay.lectio.test.rest;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.google.common.net.HttpHeaders;
import com.kakay.lectio.auth.EmailPassword;
import com.kakay.lectio.auth.IdentityAuthenticator;
import com.kakay.lectio.auth.LectioAuthorizer;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.auth.TokenAuthenticator;
import com.kakay.lectio.auth.WebTokenFilter;
import com.kakay.lectio.rest.LectioRestApplication;
import com.kakay.lectio.rest.resources.LoginResource;
import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.User;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;

public abstract class TestRestResources {
	
	@Rule
	public ResourceTestRule loginResourceTestRule = ResourceTestRule.builder()
			.addResource(new LoginResource(new IdentityAuthenticator(new LectioPersistence().getEm()), new TokenAuthenticator()))
			.addProvider(new AuthDynamicFeature(WEB_AUTH_HANDLER))
			.addProvider(new LectioAuthorizer())
	        .addProvider(RolesAllowedDynamicFeature.class)
	        .addProvider(new AuthValueFactoryProvider.Binder<>(LectioPrincipal.class))
			.build();

	protected static final WebTokenFilter WEB_AUTH_HANDLER = new WebTokenFilter.Builder()
			.setAuthenticator(new TokenAuthenticator())
			.setAuthorizer(new LectioAuthorizer())
			.setPrefix("Token").setRealm(LectioRestApplication.ORDINARY_REALM).buildAuthFilter();
			
	
	protected static final BasicCredentialAuthFilter<LectioPrincipal> BASIC_AUTH_HANDLER = new BasicCredentialAuthFilter.Builder<LectioPrincipal>()
				.setAuthenticator(new IdentityAuthenticator(new LectioPersistence().getEm()))
    			.setAuthorizer(new LectioAuthorizer())
				.setPrefix("Basic").setRealm(LectioRestApplication.ORDINARY_REALM).buildAuthFilter();
	@Rule
	public ResourceTestRule resources = ResourceTestRule.builder()
				.addResource(getResource())
				.addProvider(new AuthDynamicFeature(WEB_AUTH_HANDLER))
				.addProvider(new LectioAuthorizer())
		        .addProvider(RolesAllowedDynamicFeature.class)
		        .addProvider(new AuthValueFactoryProvider.Binder<>(LectioPrincipal.class))
				.build();
	protected Notebook notebook;
	protected User teacher;
	protected SeedData seedData;

	protected String teacherTokenString;
	
	public TestRestResources() {
		super();
	}
	abstract protected SeedData getSeedData();

	abstract protected Object getResource();
	
	@Before
	public void setUp() throws Exception {
	
		seedData = getSeedData();
		
		teacher = seedData.getTeacher();
		notebook = seedData.getNotebook();
	
		// Set up the rest client authentication
		Map<String, String> emailToPasswordMap = seedData.getEmailToPasswordMap();
		String teacherPassword = emailToPasswordMap.get(teacher.getEmail());
//		teacherClient = resources.client();
		

		Response response = getLoginResponse(teacher.getEmail(), teacherPassword, "/login");
		NewCookie newCookie = response.getCookies().get(LoginResource.LOGIN_COOKIE_NAME);
		teacherTokenString = newCookie.getValue();
	}

	@After
	public void tearDown() throws Exception {
		ClearData.main(new String[]{});
	}
	protected String hitEndpoint(String targetString)  {
		// Hit the endpoint and get the raw json string
	    String resp = resources.client().target(targetString)
	            .request()
	            .header(HttpHeaders.AUTHORIZATION, "Token " + teacherTokenString)
	            .get(String.class);
	    
		return resp;
	}
	protected Response getLoginResponse(String teacherEmail, String teacherPassword, String targetString) {
	
	
		EmailPassword emailPassword = new EmailPassword();
		emailPassword.setEmail(teacherEmail);
		emailPassword.setPassword(teacherPassword);
		
		// Hit the endpoint and get the raw json string
		Client client = loginResourceTestRule.client();
		Entity entity = Entity.json(emailPassword);
		Response resp = client.target(targetString).request().post(entity);
		return resp;
	}

}