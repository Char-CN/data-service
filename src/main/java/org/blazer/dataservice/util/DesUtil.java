package org.blazer.dataservice.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public final class DesUtil {

	private static final String DEFAULT_PASSWORD_CRYPT_KEY = "HelLohYy";
	private static final String DES = "DES";
	private static Cipher cipher = null;

	static {
		try {
			cipher = Cipher.getInstance(DES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DesUtil() {
	}

	private static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	private static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);
	}

	public static String decrypt(String data) {
		return decrypt(data, DEFAULT_PASSWORD_CRYPT_KEY);
	}

	public static String decrypt(final String data, final String key) {
		if (data == null || key == null) {
			return null;
		}
		String rst = data;
		try {
			for (int i = key.length() / 8 - 1; i >= 0; i--) {
				String tmpKey = key.substring(i * 8, i * 8 + 8);
				rst = new String(decrypt(hex2byte(rst.getBytes()), tmpKey.getBytes()));
			}
			return rst;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}

	public static String encrypt(String data) {
		return encrypt(data, DEFAULT_PASSWORD_CRYPT_KEY);
	}

	public static String encrypt(final String data, final String key) {
		if (data == null || key == null) {
			return null;
		}
		String rst = data;
		try {
			for (int i = 0; i < key.length() / 8; i++) {
				String tmpKey = key.substring(i * 8, i * 8 + 8);
				rst = byte2hex(encrypt(rst.getBytes(), tmpKey.getBytes()));
			}
			return rst;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}

	private static String byte2hex(byte[] b) {
		StringBuilder hs = new StringBuilder();
		for (int n = 0; n < b.length; n++) {
			String stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs.append("0");
			}
			hs.append(stmp);
		}
		return hs.toString();
	}

	private static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("长度不是偶数");
		}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	public static void main(String[] args) {
		String url = "http://baidu.com";
		String KEY = "URIW853F";
		System.out.println(KEY.length());
		System.out.println("1:" + DesUtil.encrypt("12347", "hellohyy"));
		System.out.println("1:" + DesUtil.decrypt(DesUtil.encrypt("http://baidu.com", "HelLOHyY"), "HelLOHyY"));
		TimeUtil time = TimeUtil.createAndPoint();
		TimeUtil time2 = TimeUtil.createAndPoint();
//		String k = "12345678";
//		String content = "fghjkzxcvbnm,2222222233333333888888fghjkzxcvbnm,222222223333333388888888999999991234";
//		System.out.println(content.length());
//		for (int i = 0; i < 1000000; i ++) {
//			if (i % 5000 == 0) {
//				time.printMs(""+i);
//			}
//			String encode = DesUtil.encrypt(content, k);
//			DesUtil.decrypt(encode, k);
//		}
		time.printMs("total");
		time2.printMs("total");
//		System.out.println("1:" + DesUtil.encrypt("http://baidu.com", "12345678"));
//		System.out.println("1:" + DesUtil.encrypt(DesUtil.encrypt("http://baidu.com", "12345678"), "12345678"));
//		System.out.println("2:" + DesUtil.encrypt("http://www.baidu.com", "URIW853FACSDQWEZ"));
//		System.out.println("2:" + DesUtil.encrypt("http://www.baidu.com", "URIW853FACSDQWEZ"));
//		System.out.println();
//		url = DesUtil.decrypt("AC11168CCE5F7CDF1A26708E9B8B4399845EA9A37066A2604EF77570F171DEC1F7DEA244AE5A9F9279D6F0039CDAA372FEB959B7D4642FCB", "12345678");
//		url = DesUtil.decrypt("49D7997FAA52E7F76BD7B9DFBEAD8203FEB959B7D4642FCB", "12345678");
		System.out.println(url);
	}

}
