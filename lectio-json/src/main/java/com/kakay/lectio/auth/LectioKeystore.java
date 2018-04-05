package com.kakay.lectio.auth;

/*
 * To use this keystore, set up the following:
 * 
 * In command prompt, enter
 * keytool -genkeypair -alias  <choose-alias> -keystore <choose-filename> -keyalg RSA -storepass <choose-keystore-password> -keypass <choose-alias-password>
 *
 * Then create a file called lectio-rest-secret.yml and put it in the classpath.  This file should look like this:
 * 
 * keystoreFile: <Location of choose-filename>
 * keystorePassword: <value of choose-keystore-password>
 * webtokenAlias: <value of choose-alias>
 * webtokenPassword: <value of choose-alias-password>

 */


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.rest.exceptions.LectioSystemException;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jersey.validation.Validators;

public class LectioKeystore {
	private final static Logger logger = Logger.getLogger(LectioKeystore.class);

	private String keystoreFile;
	private String keystorePassword;
	private String webtokenAlias;
	private String webtokenPassword;

	protected static LectioKeystore lectioKeystore;
	private static final String CONFIGURATION_FILENAME = "lectio-rest-secret.yml";
	private KeyStore keyStore;

	private Signature webtokenSignatureVerifier;
	private final static int SIGNATURE_MAX_BYTES = 256;

	protected LectioKeystore() {
	}
	
	@JsonIgnore
	public static LectioKeystore getLectioKeystoreInstance() {
		if (lectioKeystore == null) {
			buildLectioKeystore();
		}
		return lectioKeystore;
	}
	
	public void init() throws KeyStoreException, InvalidKeyException, NoSuchAlgorithmException {
		keyStore = KeyStore.Builder.newInstance("JKS", null, new File(this.keystoreFile), new PasswordProtection(this.keystorePassword.toCharArray())).getKeyStore();
		Certificate cert;
		cert = keyStore.getCertificate(webtokenAlias);
		PublicKey webtokenPublicKey = cert.getPublicKey();
		webtokenSignatureVerifier = Signature.getInstance("SHA256withRSA");
		webtokenSignatureVerifier.initVerify(webtokenPublicKey);

	}
	private static void buildLectioKeystore() 
	{
		YamlConfigurationFactory<LectioKeystore> configurationFactory = new YamlConfigurationFactory<LectioKeystore>(LectioKeystore.class, Validators.newValidator(),
				new ObjectMapper(), "");

		try {
			lectioKeystore = configurationFactory.build(new ResourceConfigurationSourceProvider(), CONFIGURATION_FILENAME);
			lectioKeystore.init();
		
		} catch (IOException e) {
			
			logger.error("IOException trying to find keystore configuration file.  ", e);
		} catch (ConfigurationException e) {
			logger.error("ConfigurationException trying to find keystore configuration file.  ", e);
		} catch(KeyStoreException e) {
			logger.error("KeyStoreException trying to initialize keystore.  ", e);
			lectioKeystore = null;
		} catch(GeneralSecurityException e) {
			logger.error("SecurityException trying to set up signatures.", e);
			lectioKeystore = null;
		}
			
	}
	

	@JsonProperty
	public String getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	@JsonProperty
	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	@JsonProperty
	public String getWebtokenAlias() {
		return webtokenAlias;
	}

	public void setWebtokenAlias(String webtokenAlias) {
		this.webtokenAlias = webtokenAlias;
	}

	@JsonProperty
	public String getWebtokenPassword() {
		return webtokenPassword;
	}

	public void setWebtokenPassword(String webtokenPassword) {
		this.webtokenPassword = webtokenPassword;
	}
	

	public String signWithWebtoken(String payload) throws GeneralSecurityException, LectioSystemException {
		try {
			KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(webtokenPassword.toCharArray());
			KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(webtokenAlias, param);
			PrivateKey privateKey = pkEntry.getPrivateKey();
			Signature signature  = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			byte[] byteArray = Base64.encodeBase64(payload.getBytes("UTF-8"));
			int byteArrayIndex = 0;
			while(byteArrayIndex < byteArray.length) {
				byte[] subsetByteArray = ArrayUtils.subarray(byteArray,  byteArrayIndex,  
						byteArrayIndex +  Math.min(SIGNATURE_MAX_BYTES,  
						byteArray.length - byteArrayIndex));
				signature.update(subsetByteArray);
				byteArrayIndex = byteArrayIndex + subsetByteArray.length;
			}
			byte[] signatureBytes = signature.sign();
				String signatureString =  Base64.encodeBase64String(signatureBytes);
				return new String(byteArray, "UTF-8") + "." + signatureString;
		}
		catch(UnsupportedEncodingException ex) {
			throw new LectioSystemException("Wrong encoding for signature string specified.", ex);
		}
	}
	
	public String verifySignatureWithWebtoken(String signedEntity) {
		int dotIndex = signedEntity.indexOf('.');
		if (dotIndex <= 0) {
			return null;
		}
		String encodedPayload = signedEntity.substring(0, dotIndex);
		String encodedSignature = signedEntity.substring(dotIndex+1);
		try {
			byte[] encodedPayloadBytes = encodedPayload.getBytes("UTF-8");
			byte[] byteArray = Base64.decodeBase64(encodedPayloadBytes);
			int encodedPayloadIndex = 0;
			while(encodedPayloadIndex < encodedPayloadBytes.length) {
				byte[] subsetByteArray = ArrayUtils.subarray(encodedPayloadBytes,  encodedPayloadIndex,  
						encodedPayloadIndex +  Math.min(SIGNATURE_MAX_BYTES,  
						byteArray.length - encodedPayloadIndex));
				webtokenSignatureVerifier.update(subsetByteArray);
				encodedPayloadIndex = encodedPayloadIndex + subsetByteArray.length;
			}
			if (webtokenSignatureVerifier.verify(Base64.decodeBase64(encodedSignature))) {
				return new String(byteArray, "UTF-8");
			}
		} catch (SignatureException e) {
			logger.error("Signature failure.", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("Signature failure.", e);
		}
		return null;
		

	}
}
