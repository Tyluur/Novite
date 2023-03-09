package novite.rs.api.database;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (byte element : data) {
			int halfbyte = (element >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				} else {
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = element & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String convertToMD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}

	public static String SHA(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	}
}
