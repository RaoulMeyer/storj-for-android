package io.storj.android.crypto;

import org.spongycastle.crypto.digests.RIPEMD160Digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.storj.android.util.Hexadecimal;

public class RIPEMD {

    public static String hash(String input) {
        try {
            final RIPEMD160Digest md = new RIPEMD160Digest();
            md.update(input.getBytes("UTF-8"), 0, input.getBytes("UTF-8").length);

            final int digestSize = md.getDigestSize();
            byte[] digest = new byte[digestSize];

            System.out.println(md.getDigestSize());

            md.doFinal(digest, 0);

            return Hexadecimal.byteArrayToHex(digest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String hashWithSha(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes("UTF-8"));

            byte[] digest = md.digest();

            final RIPEMD160Digest ripemd = new RIPEMD160Digest();
            ripemd.update(digest, 0, digest.length);

            final int digestSize = ripemd.getDigestSize();
            byte[] ripeDigest = new byte[digestSize];

            ripemd.doFinal(ripeDigest, 0);

            return Hexadecimal.byteArrayToHex(ripeDigest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
