package utils;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Hasher {
	
	private Hasher(){
		
	}
	
	public static int calculateMd5(String password) {
		HashFunction hf = Hashing.md5();
		HashCode hc = hf.newHasher().putString(password, Charsets.UTF_8).hash();
		return hc.asInt();
	}

}
