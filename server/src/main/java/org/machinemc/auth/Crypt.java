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
package org.machinemc.auth;

import com.google.common.base.Preconditions;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities related to the cryptography part of
 * Minecraft's auth system.
 * <p>
 * More info about how Minecraft encryption works can be seen on
 * <a href="https://wiki.vg/Protocol#Encryption_Request">wiki.vg</a>.
 */
public final class Crypt {

    /**
     * Algorithm used by the server to generate the keypair on startup.
     */
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
     * Generates new keypair for the server using {@link #ASYMMETRIC_ALGORITHM}.
     *
     * @return newly generated key
     */
    public static KeyPair generateKeyPair() {
        final KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ASYMMETRIC_ALGORITHM);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
        keyPairGenerator.initialize(ASYMMETRIC_BITS);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Generates verify token used for encryption requests.
     * <p>
     * Its length is always 4 for Notchian servers.
     *
     * @param length length of the token
     * @return sequence of random bytes used for verification
     */
    public static byte[] nextVerifyToken(final int length) {
        final byte[] verifyToken = new byte[length];
        ThreadLocalRandom.current().nextBytes(verifyToken);
        return verifyToken;
    }

    /**
     * Decrypts shared Secret value, encrypted with the server's public key.
     * <p>
     * This is used for handling encryption response sent by client.
     *
     * @param privateKey server's private key
     * @param encryptedSecretKey encrypted secret key
     * @return secret key
     * @throws InvalidKeyException if the given key is inappropriate for initializing this cipher
     */
    public static SecretKey decryptSecretKey(final PrivateKey privateKey, final byte[] encryptedSecretKey) throws InvalidKeyException {
        Preconditions.checkNotNull(privateKey, "Private key can not be null");
        Preconditions.checkNotNull(encryptedSecretKey, "Encrypted secret key can not be null");
        final Cipher cipher;
        try {
            cipher = Cipher.getInstance(ASYMMETRIC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new SecretKeySpec(cipher.doFinal(encryptedSecretKey), SYMMETRIC_ALGORITHM);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Returns hash server needs to use to make a request to Mojang servers if it operates is in online-mode.
     *
     * @param serverName name of the server, appears to be empty for Notchian servers
     * @param publicKey server's public key
     * @param secretKey secret key shared between the server and client
     * @return server hash
     */
    public static String getServerHash(final String serverName, final PublicKey publicKey, final SecretKey secretKey) {
        Preconditions.checkNotNull(serverName, "Server name can not be null");
        Preconditions.checkArgument(serverName.length() <= 20, "Length of server name can not exceed 20 characters");
        Preconditions.checkNotNull(publicKey, "Public key can not be null");
        Preconditions.checkNotNull(secretKey, "Secret key can not be null");
        final byte[] bytes;
        try {
            bytes = digestData(serverName.getBytes(BYTE_ENCODING), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException(exception);
        }
        return new BigInteger(bytes).toString(16);
    }

    private static byte[] digestData(final byte[]... bytes) {
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
        for (final byte[] bs : bytes) messageDigest.update(bs);
        return messageDigest.digest();
    }

    /**
     * Creates encryption cipher for given key.
     *
     * @param key secret key shared between server and client
     * @return cipher
     * @throws InvalidKeyException if the provided secret key is invalid
     */
    public static Cipher createEncryptionCipher(final SecretKey key) throws InvalidKeyException {
        return getCipher(Cipher.ENCRYPT_MODE, key);
    }


    /**
     * Creates decryption cipher for given key.
     *
     * @param key secret key shared between server and client
     * @return cipher
     * @throws InvalidKeyException if the provided secret key is invalid
     */
    public static Cipher createDecryptionCipher(final SecretKey key) throws InvalidKeyException {
        return getCipher(Cipher.DECRYPT_MODE, key);
    }

    private static Cipher getCipher(final int mode, final Key key) throws InvalidKeyException {
        try {
            final Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(mode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Creates public key from player session data provided by client.
     * <p>
     * Is used for chat message encryption.
     *
     * @param data encoded public key
     * @return decoded public key
     * @throws InvalidKeySpecException if the provided data are invalid
     */
    public static PublicKey createPlayerSessionPublicKey(final byte[] data) throws InvalidKeySpecException {
        Preconditions.checkNotNull(data, "Encoded public key can not be null");
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

}
