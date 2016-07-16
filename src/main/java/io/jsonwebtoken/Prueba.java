package io.jsonwebtoken;

import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by juannoguera on 23/06/16.
 */
public class Prueba {

    public void pruebota(){
    }

    public static void main(String[] args) throws JSONException, ParseException {
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("dd/mm/yy");
        calendar.setTime(format.parse("07/07/2016"));
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());

        JSONObject payLoad = new JSONObject();
        payLoad.put("iss","http://galaxies.com");
        payLoad.put("exp",1700819380);
        String[] o = new String[]{"explorer", "solar-harvester", "seller"};
        payLoad.put("scopes", o);
        payLoad.put("sub","tom@andromeda.com");
        payLoad.put("iss","http://galaxies.com");
        payLoad.put("xsrfToken","d9b9714c-7ac0-42e0-8696-2dae95dbc33e");
//
        byte[] decodedKey = Base64.decodeBase64("CI5xTEszEA5NLVgCvHDJHSVFtdjpj1UTvZfEwJ6ys1U");
        Key key = new SecretKeySpec(decodedKey,0, decodedKey.length,"HS256");
        String s = Jwts.builder().setPayload(payLoad.toString()).signWith(SignatureAlgorithm.HS512, key).compact();
        System.out.println(s);
        System.out.println(Jwts.parser().setSigningKey(key).parse(s).getBody());
    }
}
