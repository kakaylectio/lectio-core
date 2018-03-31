package com.kakay.lectio.test.rest;

import java.util.Map;

import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.kakay.lectio.auth.IdentityAuthentication;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.User;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;

public abstract class TestRestResources {

	protected static final BasicCredentialAuthFilter<LectioPrincipal> BASIC_AUTH_HANDLER = new BasicCredentialAuthFilter.Builder<LectioPrincipal>()
				.setAuthenticator(new IdentityAuthentication())
				// .setAuthorizer(new ExampleAuthorizer())
				.setPrefix("Basic").setRealm("SUPER SECRET STUFF").buildAuthFilter();
	@Rule
	public ResourceTestRule resources = ResourceTestRule.builder()
				.addResource(getResource())
				.addProvider(new AuthDynamicFeature(BASIC_AUTH_HANDLER))
				// .addProvider(new LectioAuthorizer())
				.build();
	protected Notebook notebook;
	protected User teacher;
	protected Client teacherClient;
	protected SeedData seedData;

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
		HttpAuthenticationFeature teacherAuthFeature = HttpAuthenticationFeature.basic(
				teacher.getEmail(),
				teacherPassword);
		teacherClient = resources.client();
		teacherClient.register(teacherAuthFeature);
	}

	@After
	public void tearDown() throws Exception {
		LectioPersistence.clearData("secret");
	}

}