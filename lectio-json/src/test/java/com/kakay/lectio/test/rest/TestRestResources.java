package com.kakay.lectio.test.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.uri.UriComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.kakay.lectio.auth.EmailPassword;
import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.User;
import com.kakay.lectio.rest.LectioRestApplication;
import com.kakay.lectio.rest.LectioRestConfiguration;
import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.SeedData;

import io.dropwizard.jersey.jackson.JacksonBinder;
import io.dropwizard.testing.junit.DropwizardAppRule;

/**
 * Tests that use Rest Resources must be called
 * with a client keystore.
 * Export the certificate from the server httpsrest keystore.
 * keytool -export -alias server -keystore client/server/server_keystore.jks -storepass secret -file client/server/server.cer
 * Import the certificate into the unittest client keystore.
 * keytool -importcert -v -trustcacerts -alias server -keystore client/client_cacerts.jks -storepass secret -keypass secret -file client/server/server.cer
 * 
 * Then define the JVM variables:
 * -Djavax.net.ssl.trustStore=/path/to/client/client_cacerts.jks 
 * -Djavax.net.ssl.trustStorePassword=secret
 */
public abstract class TestRestResources {

	private static final int DEBUG_CONNECT_TIMEOUT_MS = 400000;
	private static final int DEBUG_READ_TIMEOUT_MS = 400000;
	protected static String appUri = "https://localhost:8889";
	@Rule
	public DropwizardAppRule<LectioRestConfiguration> appRule = 
	   new DropwizardAppRule<LectioRestConfiguration>(LectioRestApplication.class, "lectio-rest.yml");
	protected Notebook notebook;
	protected User teacher;
	protected SeedData seedData;

	protected String savedTokenString;
	protected Client client;

	public TestRestResources() {
		super();
	}

	abstract protected SeedData getSeedData();

//	abstract protected Object getResource();

	@Before
	public void setUp() throws Exception {
		

		appUri = "https://localhost:" + appRule.getPort(0);

		seedData = getSeedData();

		teacher = seedData.getTeacher();
		notebook = seedData.getNotebook();

		// Set up the rest client authentication
		Map<String, String> emailToPasswordMap = seedData.getEmailToPasswordMap();
		String teacherPassword = emailToPasswordMap.get(teacher.getEmail());

		client = new JerseyClientBuilder()
		        .register(new JacksonBinder(appRule.getObjectMapper()))
		        .property(ClientProperties.CONNECT_TIMEOUT, DEBUG_CONNECT_TIMEOUT_MS)
		        .property(ClientProperties.READ_TIMEOUT, DEBUG_READ_TIMEOUT_MS)
		        .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).build();

		LoginResponse loginResponse = getLoginResponse(teacher.getEmail(), teacherPassword, "/login");
		savedTokenString = loginResponse.getToken();
	}

	@After
	public void tearDown() throws Exception {
		client.close();
		ClearData.main(new String[] {});
	}

	protected String hitEndpoint(String targetString) {
		try {
			return hitEndpoint(targetString, new HashMap<String, Object>());
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	protected String hitEndpoint(String targetString, Map<String, Object> queryParams) throws JsonProcessingException {
		
		
		WebTarget target = client.target(appUri + targetString);
		for (String queryParamKey : queryParams.keySet()) {
			Object obj = queryParams.get(queryParamKey);
					
			target = target.queryParam(queryParamKey, UriComponent.encode(obj.toString(), UriComponent.Type.QUERY_PARAM_SPACE_ENCODED));
		}
		String resp = target.request()
				.header(HttpHeaders.AUTHORIZATION, "Token " + savedTokenString).get(String.class);

		return resp;
	}

	protected LoginResponse getLoginResponse(String teacherEmail, String teacherPassword, String targetString) {

		EmailPassword emailPassword = new EmailPassword();
		emailPassword.setEmail(teacherEmail);
		emailPassword.setPassword(teacherPassword);

		// Hit the endpoint and get the raw json string
		Entity entity = Entity.json(emailPassword);
		Response resp = client.target(appUri + targetString).request(MediaType.APPLICATION_JSON).post(entity);
		if (resp.getStatus() >= 400) {
			throw new WebApplicationException(resp);
		}
		LoginResponse loginResponse = resp.readEntity(LoginResponse.class);
		return loginResponse;
	}
	
	protected Response postEndpoint(String targetString, Object bodyContent) {
		Entity entity = Entity.json(bodyContent);
		Response resp = client.target(appUri + targetString)
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Token " + savedTokenString)
				.post(entity);
		return resp;
		
	}
	
	

}