package com.group42.server.server.protocol;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for encrypting and decrypting
 * It uses Apache Codec http://commons.apache.org/codec/ lib
 */
public class StringCrypter {

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
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public StringCrypter(SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        updateSecretKey(key);
    }

    private void updateSecretKey(SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        ecipher = Cipher.getInstance(key.getAlgorithm());
        dcipher = Cipher.getInstance(key.getAlgorithm());
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public static class DESSecretKey implements SecretKey {

        private final byte[] key;

        /**
         * The key length is needed to be a 8 bit
         */
        public DESSecretKey(byte[] key) {
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
     * Encryption method
     *
     * @param str open string
     * @return encrypted string in Base64 key
     */
    public String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            return Base64.encodeBase64String(enc);
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            Logger.getLogger(com.group42.server.protocol.StringCrypter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Decryption method
     *
     * @param str encrypted string in Base64
     * @return decrypted string
     */
    public String decrypt(String str) {
        try {
            byte[] dec = Base64.decodeBase64(str);
            byte[] utf8 = dcipher.doFinal(dec);
            return new String(utf8, "UTF8");
        } catch (IllegalBlockSizeException | BadPaddingException | IOException ex) {
            Logger.getLogger(com.group42.server.protocol.StringCrypter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
