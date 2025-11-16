package com.iwacu250.landplots.util;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        
        System.out.println("=".repeat(70));
        System.out.println("JWT SECRET KEY GENERATED");
        System.out.println("=".repeat(70));
        System.out.println("\nAdd this to your application.properties:\n");
        System.out.println("jwt.secret=" + base64Key);
        System.out.println("\nKey length: " + key.getEncoded().length + " bytes");
        System.out.println("=".repeat(70));
    }
}
