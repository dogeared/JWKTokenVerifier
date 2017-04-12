package com.okta.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class AccessTokenVerifier {

    public static void main(String[] args) {

        String accessTokenString = args[0];
        final String modulusString = args[1];
        final String exponentString = args[2];

        SigningKeyResolver resolver = new SigningKeyResolverAdapter() {
            public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
                try {
                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusString));
                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentString));

                    return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        try {
            Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKeyResolver(resolver)
                .parseClaimsJws(accessTokenString);

            System.out.println("Verified Access Token");
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwsClaims));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
