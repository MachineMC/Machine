/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.api.auth;

import org.intellij.lang.annotations.MagicConstant;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class handling cryptography part of
 * Minecraft's auth system.
 */
public final class Crypt {

    public static final String ASYMMETRIC_ALGORITHM = "RSA";
    public static final int ASYMMETRIC_BITS = 1024;

    public static final String SYMMETRIC_ALGORITHM = "AES";

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final String BYTE_ENCODING = "ISO_8859_1";

    public static final String ENCRYPTION = "AES/CFB8/NoPadding";

    private Crypt() {
        throw new UnsupportedOperationException();
    }

    /**
     * Generates new key for the server.
     * @return newly generated key
     */
    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ASYMMETRIC_ALGORITHM);
            keyPairGenerator.initialize(ASYMMETRIC_BITS);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * @return sequence of random 4 bytes used for verification
     */
    public static byte[] nextVerifyToken() {
        final byte[] verifyToken = new byte[4];
        ThreadLocalRandom.current().nextBytes(verifyToken);
        return verifyToken;
    }

    /**
     * Creates secret key from server's private key and encrypted secret key.
     * @param privateKey server's private key
     * @param encryptedSecretKey encrypted secret key
     * @return secret key
     */
    public static SecretKey decryptByteToSecretKey(final PrivateKey privateKey, final byte[] encryptedSecretKey) {
        Objects.requireNonNull(privateKey);
        Objects.requireNonNull(encryptedSecretKey);
        try {
            final Cipher cipher = Cipher.getInstance(ASYMMETRIC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new SecretKeySpec(cipher.doFinal(encryptedSecretKey), SYMMETRIC_ALGORITHM);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Digest authentication process.
     * @param baseServerID server id - always empty string
     * @param publicKey server's public key
     * @param secretKey secret key shared between server and client
     * @return digested data
     */
    public static byte[] digestData(final String baseServerID, final PublicKey publicKey, final SecretKey secretKey) {
        Objects.requireNonNull(baseServerID);
        Objects.requireNonNull(publicKey);
        Objects.requireNonNull(secretKey);
        try {
            return digestData(baseServerID.getBytes(BYTE_ENCODING), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static byte[] digestData(final byte[]... bytes) {
        Objects.requireNonNull(bytes);
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
        for (final byte[] bs : bytes)
            messageDigest.update(bs);
        return messageDigest.digest();
    }

    /**
     * Creates public key from encoded key according to the X.509 standard.
     * @param bytes encoded key
     * @return public key
     */
    public static PublicKey pubicKeyFrom(final byte[] bytes) {
        Objects.requireNonNull(bytes);
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (InvalidKeySpecException exception) {
            throw new RuntimeException(exception);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Creates cipher for Minecraft's encryption algorithm.
     * @param mode op mode
     * @param key server's secret key
     * @return created cipher
     */
    public static Cipher getCipher(final @MagicConstant(intValues = {Cipher.ENCRYPT_MODE,
                                                                     Cipher.DECRYPT_MODE,
                                                                     Cipher.WRAP_MODE,
                                                                     Cipher.UNWRAP_MODE}) int mode,
                                   final Key key) {
        try {
            final Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(mode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (GeneralSecurityException exception) {
            throw new RuntimeException(exception);
        }
    }

}
