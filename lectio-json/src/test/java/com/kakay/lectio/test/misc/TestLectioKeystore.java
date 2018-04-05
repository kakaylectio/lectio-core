package com.kakay.lectio.test.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kakay.lectio.auth.LectioKeystore;

public class TestLectioKeystore {

	@Test
	public void testKeystore() throws Exception{
		LectioKeystore keystore = LectioKeystore.getLectioKeystoreInstance();
		assertNotNull("LectioKeystore creation failed.", keystore);
		
		String testString = "Here is a string that I'd like to sign.";
		String signedString = keystore.signWithWebtoken(testString);
			
		assertNotNull("Signed string from keystore should not be null.", 
				signedString);
		
		
		String verifiedString = keystore.verifySignatureWithWebtoken(signedString);
		assertNotNull("Verify signature failure.", verifiedString);
		
		assertEquals("Output of verified string is not correct", testString, verifiedString);
		
		String modifiedString = new String(signedString);
		modifiedString = signedString.substring(0, modifiedString.length()-5) + "abcde";
		String extractedModifiedString = keystore.verifySignatureWithWebtoken(modifiedString);
		assertNull("Verification should fail when string is modified.", 
				extractedModifiedString);
	}

}
