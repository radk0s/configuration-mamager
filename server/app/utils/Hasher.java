package utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Hasher {

	private Hasher() {

	}

	public static int calculateMd5(String stringToHash) {
		HashFunction hf = Hashing.md5();
		HashCode hc = hf.newHasher().putString(stringToHash, Charsets.UTF_8).hash();
		return hc.asInt();
	}

	public static String calculateHmacMd5(String key, String message) throws Exception {
		 Mac sha256_HMAC = calculateHmac(key);

		  return String.valueOf(sha256_HMAC.doFinal(message.getBytes()));
	}
	public static String calculateHexHmacMd5(String key, String message) throws Exception {
		 Mac sha256_HMAC = calculateHmac(key);

		  return Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes()));
	}

	private static Mac calculateHmac(String key) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		  SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		  sha256_HMAC.init(secret_key);
		return sha256_HMAC;
	}

	public static String calculateSha256(String stringToHash) {
		HashFunction hf = Hashing.sha256();
		HashCode hc = hf.newHasher().putString(stringToHash, Charsets.UTF_8).hash();
		return Hex.encodeHexString(hc.asBytes());
	}
}
