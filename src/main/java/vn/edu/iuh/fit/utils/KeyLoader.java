package vn.edu.iuh.fit.utils;

import io.jsonwebtoken.security.Keys;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    private static final String KEY_PATH = "certs/";

    public static Key loadRefreshSecretKey(String filename) throws IOException {
        InputStream inputStream = KeyLoader.class.getClassLoader().getResourceAsStream(KEY_PATH + filename);
        if (inputStream == null) {
            throw new IOException("Không tìm thấy file khóa Refresh Token trong resources: " + KEY_PATH + filename);
        }

        // Đọc nội dung file
        String keyContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
        byte[] decodedKey = Base64.getDecoder().decode(keyContent);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        return loadKey(filename, true);
    }

    public static PublicKey loadPublicKey(String filename) throws Exception {
        return loadKey(filename, false);
    }

    private static <T> T loadKey(String filename, boolean isPrivateKey) throws Exception {
        InputStream inputStream = KeyLoader.class.getClassLoader().getResourceAsStream(KEY_PATH + filename);
        if (inputStream == null) {
            throw new FileNotFoundException("Không tìm thấy file: " + KEY_PATH + filename);
        }

        String keyPEM = new String(inputStream.readAllBytes())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");  // Xóa khoảng trắng và dòng trống

        byte[] keyBytes = Base64.getDecoder().decode(keyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        if (isPrivateKey) {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return (T) keyFactory.generatePrivate(keySpec);
        } else {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return (T) keyFactory.generatePublic(keySpec);
        }
    }


}
