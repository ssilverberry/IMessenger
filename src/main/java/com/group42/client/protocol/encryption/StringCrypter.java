package com.group42.client.protocol.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for encrypting and decrypting messages to exchange with the server
 */
public class StringCrypter {

    private final Logger logger = LogManager.getLogger(StringCrypter.class);

    /**
     * Simplified constructor. Creates a StringCrypter with a
     * DESSecretKey key with a default value (not recommended)
     */
    public StringCrypter() {
        this(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
    }

    /**
     * Simplified constructor. Creates a StringCrypter with a key
     * DESSecretKey (encryption algorithm DES) with the value key.
     * The key key must be 8 bytes in length
     */
    public StringCrypter(byte[] key) {
        try {
            updateSecretKey(new DESSecretKey(key));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            logger.error("Exception in constructor: StringCrypter(byte[] key): ", ex);
        }
    }

    public StringCrypter(SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        updateSecretKey(key);
    }

    private void updateSecretKey(SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException {
        ecipher = Cipher.getInstance(key.getAlgorithm());
        dcipher = Cipher.getInstance(key.getAlgorithm());
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public static class DESSecretKey implements SecretKey {

        private final byte[] key;

        /**
         * the key must be 8 bytes in length
         */
        DESSecretKey(byte[] key) {
            this.key = key;
        }

        @Override
        public String getAlgorithm() {
            return "DES";
        }

        @Override
        public String getFormat() {
            return "RAW";
        }

        @Override
        public byte[] getEncoded() {
            return key;
        }
    }

    private Cipher ecipher;
    private Cipher dcipher;

    /**
     * Encryption function
     *
     * @param str plain text string
     * @return an encrypted string in Base64 format
     */
    public String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            return Base64.encodeBase64String(enc);
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            logger.error("exception in method encrypt(): ", ex);
        }
        return null;
    }

    /**
     * Decryption function
     *
     * @param str encrypted string in Base64 format
     * @return decrypted string
     */
    public String decrypt(String str) {
        try {
            byte[] dec = Base64.decodeBase64(str);
            byte[] utf8 = dcipher.doFinal(dec);
            return new String(utf8, "UTF8");
        } catch (IllegalBlockSizeException | BadPaddingException | IOException ex) {
            logger.error("exception in method decrypt(): ", ex);
        }
        return null;
    }
}
