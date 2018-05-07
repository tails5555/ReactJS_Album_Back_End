package net.kang.util;

import java.security.MessageDigest;

public class Encryption {

    // 암호화 알고리즘을 SHA-256, MD5를 이용해서 진행할 수 있도록 한다.
    public static final String SHA256 = "SHA-256";
    public static final String MD5 = "MD5";

    // 암호화 알고리즘의 종류를 받아서 파일 이름에 대해서 암호화를 진행한다.
    public static String encrypt(String s, String messageDigest) {
        try {
            MessageDigest md = MessageDigest.getInstance(messageDigest);
            byte[] passBytes = s.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<digested.length;i++)
                sb.append(Integer.toHexString(0xff & digested[i]));
            return sb.toString();
        }
        catch (Exception e) {
            return s;
        }
    }
}
