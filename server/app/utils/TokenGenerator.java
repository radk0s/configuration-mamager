package utils;

import java.util.UUID;

public class TokenGenerator {
	private TokenGenerator() {

	}

	public static String generate() {
		return UUID.randomUUID().toString();
	}
}
