package com.torkirion.eroam.microservice.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class HotelBedsUtil {
    public static String getXSignature(String apiKey, String secret)  {
        Long millis = Calendar.getInstance().getTimeInMillis();
        Double second = millis * 1.0 / 1000;
        Long utcDate = (Long)Math.round(second - 0.5);
        String assemble = apiKey + secret + utcDate;
        String encryption = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            encryption = toHexString(md.digest(assemble.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryption;
    }

    private static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
