package utils;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Hasher {
	
	private Hasher(){
		
	}
	
	public static int calculateMd5(String strongToHash) {
		HashFunction hf = Hashing.md5();
		HashCode hc = hf.newHasher().putString(strongToHash, Charsets.UTF_8).hash();
		return hc.asInt();
	}

}
