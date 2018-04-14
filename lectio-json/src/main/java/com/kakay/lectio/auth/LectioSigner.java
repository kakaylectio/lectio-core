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
import com.kktam.lectio.control.exception.LectioSystemException;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jersey.validation.Validators;

/**
 * Object used to sign and verify tokens
 * A keystore needs to be created using keytool.  A yaml file
 * with the sensitive keystore access information needs to be in the classpath.
 * 
 * Command to set up keystore:  (keytool is a command line utility provided with Oracle Java SE8)
 * keytool -genkeypair -alias  <choose-alias> -keystore <choose-filename> -keyalg RSA -storepass <choose-keystore-password> -keypass <choose-alias-password>
 *
 * Content of lectio-rest-secret.yml file to be placed in classpath:
 * 
 * keystoreFile: <Location of choose-filename>
 * keystorePassword: <value of choose-keystore-password>
 * webtokenAlias: <value of choose-alias>
 * webtokenPassword: <value of choose-alias-password>

 * 
 */
public class LectioSigner {
	/**
	 * Configuration class to read a YAML file containing the keystore information.
	 *
	 */
	public static class KeystoreConfiguration {
		public String keystoreFile;
		public String keystorePassword;
		public String webtokenAlias;
		public String webtokenPassword;

		public KeystoreConfiguration() {
		}
		@JsonProperty
		String getKeystoreFile() {
			return keystoreFile;
		}

		public void setKeystoreFile(String keystoreFile) {
			this.keystoreFile = keystoreFile;
		}

		@JsonProperty
		String getKeystorePassword() {
			return keystorePassword;
		}

		public void setKeystorePassword(String keystorePassword) {
			this.keystorePassword = keystorePassword;
		}

		@JsonProperty
		String getWebtokenAlias() {
			return webtokenAlias;
		}

		public void setWebtokenAlias(String webtokenAlias) {
			this.webtokenAlias = webtokenAlias;
		}

		@JsonProperty
		String getWebtokenPassword() {
			return this.webtokenPassword;
		}

		public void setWebtokenPassword(String webtokenPassword) {
			this.webtokenPassword = webtokenPassword;
		}
	}

	private final static Logger logger = Logger.getLogger(LectioSigner.class);

	private KeystoreConfiguration configuration = new KeystoreConfiguration();

	protected static LectioSigner lectioSigner;
	private static final String CONFIGURATION_FILENAME = "lectio-rest-secret.yml";
	private KeyStore keyStore;

	private Signature webtokenSignatureVerifier;
	private final static int SIGNATURE_MAX_BYTES = 256;

	protected LectioSigner() {
	}
	
	@JsonIgnore
	public static LectioSigner getInstance() {
		if (lectioSigner == null) {
			buildLectioKeystore();
		}
		return lectioSigner;
	}
	
	void init(KeystoreConfiguration newConfiguration) throws KeyStoreException, InvalidKeyException, NoSuchAlgorithmException {
		this.configuration = newConfiguration;
		keyStore = KeyStore.Builder.newInstance("JKS", null, new File(this.configuration.getKeystoreFile()), 
				new PasswordProtection(this.configuration.getKeystorePassword().toCharArray())).getKeyStore();
		Certificate cert;
		cert = keyStore.getCertificate(configuration.getWebtokenAlias());
		PublicKey webtokenPublicKey = cert.getPublicKey();
		webtokenSignatureVerifier = Signature.getInstance("SHA256withRSA");
		webtokenSignatureVerifier.initVerify(webtokenPublicKey);

	}
	private static void buildLectioKeystore() 
	{
		YamlConfigurationFactory<KeystoreConfiguration> configurationFactory = new YamlConfigurationFactory<KeystoreConfiguration>(KeystoreConfiguration.class, Validators.newValidator(),
				new ObjectMapper(), "");

		try {
			KeystoreConfiguration configuration = configurationFactory.build(new ResourceConfigurationSourceProvider(), CONFIGURATION_FILENAME);
			lectioSigner = new LectioSigner();
			lectioSigner.init(configuration);
		
		} catch (IOException e) {
			
			logger.error("IOException trying to find keystore configuration file.  ", e);
		} catch (ConfigurationException e) {
			logger.error("ConfigurationException trying to find keystore configuration file.  ", e);
		} catch(KeyStoreException e) {
			logger.error("KeyStoreException trying to initialize keystore.  ", e);
			lectioSigner = null;
		} catch(GeneralSecurityException e) {
			logger.error("SecurityException trying to set up signatures.", e);
			lectioSigner = null;
		}
			
	}
	


	

	public String signWithWebtoken(String payload) throws GeneralSecurityException, LectioSystemException {
		try {
			KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(configuration.getWebtokenPassword().toCharArray());
			KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(configuration.getWebtokenAlias(), param);
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
			int encodedPayloadIndex = 0;
			while(encodedPayloadIndex < encodedPayloadBytes.length) {
				byte[] subsetByteArray = ArrayUtils.subarray(encodedPayloadBytes,  encodedPayloadIndex,  
						encodedPayloadIndex +  Math.min(SIGNATURE_MAX_BYTES,  
								encodedPayloadBytes.length - encodedPayloadIndex));
				webtokenSignatureVerifier.update(subsetByteArray);
				encodedPayloadIndex = encodedPayloadIndex + subsetByteArray.length;
			}
			if (webtokenSignatureVerifier.verify(Base64.decodeBase64(encodedSignature))) {
				byte[] byteArray = Base64.decodeBase64(encodedPayloadBytes);
				return new String(byteArray, "UTF-8");
			}
		} catch (SignatureException e) {
			logger.error("Signature failure.", e);
			return null;
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException while verifying signature.", e);
			throw new LectioSystemException("Decoding not supported.", e);
		}
		return null;
	}
}
