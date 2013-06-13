package com.norteksoft.product.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {
	
	private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	public static String encode(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(password.getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            return getFormattedText(digest);
        } catch (final NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedText(byte[] bytes) {
        final StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (int j = 0; j < bytes.length; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
}
