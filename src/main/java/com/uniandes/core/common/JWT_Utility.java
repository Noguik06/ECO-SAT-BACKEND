package com.uniandes.core.common;


import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by juannoguera on 3/07/16.
 */
public class JWT_Utility {


    public static JSONObject generarToken(String token) throws ParseException, JSONException {
    	SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key key = MacProvider.generateKey(SignatureAlgorithm.HS256);
        JSONObject entry = new JSONObject(token);
        JSONObject payLoad = new JSONObject();

        payLoad.put("user_id",entry.getString("cedula"));

        String userToken = Jwts.builder().setPayload(payLoad.toString()).signWith(SignatureAlgorithm.HS512, key).compact();

        JSONObject jsonObjectResponse = new JSONObject();
        jsonObjectResponse.put("token",userToken);
        String keyConverted = Base64.encodeBase64String(key.getEncoded());
        jsonObjectResponse.put("key",keyConverted);

        return jsonObjectResponse;
    }

    public static void validarToken(String tokenUser, String tokenMessage, String keyString){

        byte[] decodedKey = com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64.decodeBase64(keyString);
        Key key = new SecretKeySpec(decodedKey,0, decodedKey.length,"HS256");
        DefaultClaims defaultClaims = (DefaultClaims) Jwts.parser().setSigningKey(key).parse(tokenUser).getBody();
        String userIdUser = ((DefaultClaims) Jwts.parser().setSigningKey(key).parse(tokenUser).getBody()).get("user_id").toString();
        String userIdMessage = ((DefaultClaims) Jwts.parser().setSigningKey(key).parse(tokenMessage).getBody()).get("user_id").toString();
        assert userIdUser.equals(userIdMessage);
    }

    public void invalidarToken(){

    }
}
