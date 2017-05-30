package io.storj.android.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.storj.android.util.Hexadecimal;

public class SHA256 {

    public static String hash(String input) {
        return SHA256.hash(input, "SHA-256");
    }

    public static String hash(String input, String method) {
        try {
            MessageDigest md = MessageDigest.getInstance(method);
            md.update(input.getBytes("UTF-8"));

            byte[] digest = md.digest();

            return Hexadecimal.byteArrayToHex(digest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
