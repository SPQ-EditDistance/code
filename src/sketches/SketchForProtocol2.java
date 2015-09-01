package sketches;

import java.security.SecureRandom;
import java.util.Arrays;

public class SketchForProtocol2 {
	public int m,l;
	public int[][] bs;
	public long[][]sks;
	public int numberOfInsert;
	SecureRandom rnd = new SecureRandom();

	public SketchForProtocol2(int m, int l) {
		this.m = m;
		this.l = l;
		bs  = new int[l][m];
		sks = new long[l][m/64+1];
		for(int i = 0; i < l; ++i)
			for(int j = 0; j < sks[0].length; ++j)
				sks[i][j] = rnd.nextInt();
	}

	public void insert(long s) {
		numberOfInsert++;
		for(int i = 0; i < l; ++i){
			for(int j = 0; j < m; ) {
				long h = Hasher.fasthash64(s, sks[i][j/64]);
				for(int t = 0; t < 64 && j < m; ++t) {
					bs[i][j++] += (h&1);
					h >>=1;
				}
			}
		}
	}
	
	public void finalizeSketch() {
		for(int j = 0; j < l; ++j)
			for(int i = 0; i < m; ++i)
				bs[j][i] = bs[j][i]*2-numberOfInsert; 
	}

	public double size() {
		double[] res = new double[l];
		for(int i = 0; i < l; ++i) {
			for(int j = 0; j < m; ++j) {
				res[i] += Math.abs(bs[i][j]);
			}
			res[i]/=m;
		}
		Arrays.sort(res);
		return res[l/2]*Math.PI/2*res[l/2];
	}

	public void set_diff(SketchForProtocol2 a) {
		for(int j = 0; j < l; ++j)
			for(int i = 0; i < m; ++i) 
				bs[j][i]-=a.bs[j][i];
	}
	
	public int MaxInt() {
		int res = -1;
		for(int i = 0; i < bs.length; ++i)
			for(int j = 0; j < bs[0].length; ++j)
				res = Math.max(res, Math.abs(bs[i][j]));
		return (int)(Math.ceil( Math.log(res)/Math.log(2)));
	}
}

