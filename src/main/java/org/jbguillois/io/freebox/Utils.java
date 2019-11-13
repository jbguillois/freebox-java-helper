package org.jbguillois.io.freebox;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class Utils {
	public static String computeHMAC_SHA1(String appToken, String challenge) {
		try {
			byte[] keyBytes = appToken.getBytes("UTF-8");           
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal(challenge.getBytes("UTF-8"));

			// TODO Consider replacing with a simple hex encoder so we don't need commons-codec
			byte[] hexBytes = new Hex().encode(rawHmac);

			return new String(hexBytes, "UTF-8");

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
