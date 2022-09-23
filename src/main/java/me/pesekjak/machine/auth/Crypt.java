package me.pesekjak.machine.auth;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ThreadLocalRandom;

public class Crypt {

    public static final String ASYMMETRIC_ALGORITHM = "RSA";
    public static final int ASYMMETRIC_BITS = 1024;

    public static final String SYMMETRIC_ALGORITHM = "AES";

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final String BYTE_ENCODING = "ISO_8859_1";

    public static final String ENCRYPTION = "AES/CFB8/NoPadding";

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ASYMMETRIC_ALGORITHM);
            keyPairGenerator.initialize(ASYMMETRIC_BITS);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static byte[] nextVerifyToken() {
        byte[] verifyToken = new byte[4];
        ThreadLocalRandom.current().nextBytes(verifyToken);
        return verifyToken;
    }

    public static SecretKey decryptByteToSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) {
        try {
            Cipher cipher = Cipher.getInstance(ASYMMETRIC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new SecretKeySpec(cipher.doFinal(encryptedSecretKey), SYMMETRIC_ALGORITHM);
        } catch (Exception ignored) { }
        return null;
    }

    public static byte[] digestData(String baseServerId, PublicKey publicKey, SecretKey secretKey) {
        try {
            return digestData(baseServerId.getBytes(BYTE_ENCODING), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static PublicKey pubicKeyFrom(byte[] bytes) {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM);
            return keyFactory.generatePublic(spec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static Cipher getCipher(int mode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(mode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] digestData(byte[]... bytes) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        for(byte[] bs : bytes) {
            messageDigest.update(bs);
        }
        return messageDigest.digest();
    }

}
