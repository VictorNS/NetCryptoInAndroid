package ru.victorns.netcryptoinandroid.Utils;

import android.util.Base64;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * copied from .NET System.Web.Helpers.Crypto
 * public static bool VerifyHashedPassword(string hashedPassword, string password);
 */
public class Crypto {
	private static final String TAG = "_" + Crypto.class.getSimpleName();

	public static boolean VerifyHashedPassword(String hashedPassword, String password) {
		int PBKDF2IterCount = 1000; // default for Rfc2898DeriveBytes
		int PBKDF2SubkeyLength = 256 / 8; // 256 bits
		int SaltSize = 128 / 8; // 128 bits
		if (hashedPassword == null) {
			throw new IllegalArgumentException("hashedPassword");
		}
		if (password == null) {
			throw new IllegalArgumentException("password");
		}

		byte[] hashedPasswordBytes = Base64.decode(hashedPassword, Base64.DEFAULT);
		Log.d(TAG, "hashedPasswordBytes=" + BytesToString(hashedPasswordBytes));

		// Verify a version 0 (see comment above) password hash.

		if (hashedPasswordBytes.length != (1 + SaltSize + PBKDF2SubkeyLength) || hashedPasswordBytes[0] != 0x00) {
			// Wrong length or version header.
			return false;
		}

		byte[] salt = new byte[SaltSize];

		System.arraycopy(hashedPasswordBytes, 1, salt, 0, SaltSize);
		Log.d(TAG, "salt=" + BytesToString(salt));
		byte[] storedSubkey = new byte[PBKDF2SubkeyLength];
		System.arraycopy(hashedPasswordBytes, 1 + SaltSize, storedSubkey, 0, PBKDF2SubkeyLength);
		Log.d(TAG, "storedSubkey=" + BytesToString(storedSubkey));

		byte[] generatedSubkey;


		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e2) {
			Log.e(TAG, e2.toString());
			return false;
		}
		Log.d(TAG, "Call PBKDF2WithHmacSHA1 with password=" + password + " salt... PBKDF2IterCount=" + PBKDF2IterCount);
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2IterCount, 256);
		SecretKey tmp = null;
		try {
			tmp = factory.generateSecret(spec);
		} catch (InvalidKeySpecException e2) {
			Log.e(TAG, e2.toString());
			return false;
		}
		SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		generatedSubkey = secret.getEncoded();
		Log.d(TAG, "generatedSubkey=" + BytesToString(generatedSubkey));

		Log.d(TAG, "");
		Log.d(TAG, "Totaly compare:");
		Log.d(TAG, "   storedSubkey=" + BytesToString(storedSubkey));
		Log.d(TAG, "generatedSubkey=" + BytesToString(generatedSubkey));
		return ByteArraysEqual(storedSubkey, generatedSubkey);
	}

	private static boolean ByteArraysEqual(byte[] a, byte[] b) {
		return Arrays.equals(a, b);
	}

	private static String BytesToString(byte[] source) {
		StringBuilder sb = new StringBuilder();
		for (byte b : source) {
			sb.append(Integer.valueOf(b & 0xff).toString());
			sb.append(' ');
		}
		return sb.toString();
	}
}
