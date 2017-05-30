package io.storj.android.crypto;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static byte[] generateKey(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1000, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            AlgorithmParameters params = cipher.getParameters();
            final byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            final byte[] encrypted = cipher.doFinal(data);

            final byte[] fullEncrypted = new byte[iv.length + encrypted.length];

            System.arraycopy(iv, 0, fullEncrypted, 0, iv.length);
            System.arraycopy(encrypted, 0, fullEncrypted, iv.length, fullEncrypted.length - iv.length);

            return fullEncrypted;
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] key) {
        try {
            byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
            encryptedData = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);

            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);

            return cipher.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
