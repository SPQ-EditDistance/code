package sketches;

public class Hasher {
	public static long mix(long h) {
		h ^= h >> 23;
		h *= 0x2127599bf4325c37L;
		h ^= h >> 47;
		return h;
	}

	//modified from existing hash functions...
	public static long fasthash64(long data, long seed) {
		seed ^= mix(data);
		seed *= 0x880355f21e6d1965L;
		seed ^= mix(data&0xFF);
		seed ^= mix(seed);
		seed *= 0x880355f21e6d1965L;
		return seed;
	}
}
