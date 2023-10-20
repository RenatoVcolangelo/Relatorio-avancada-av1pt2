package io.sim;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;

public class Criptografia {

 private static String IV = "AAAAAAAAAAAAAAAA";
 private static String textopuro = "teste texto 12345678\0\0\0";
 private static String chaveencriptacao = "0123456789abcdef";
 private static int tamNumBytes = 32;

 public static int getTamNumBytes(){
    return tamNumBytes;
 }

 public static byte[] encrypt(String textopuro) throws Exception {
   Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
   SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes("UTF-8"), "AES");
   encripta.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
   return encripta.doFinal(textopuro.getBytes("UTF-8"));
 }

 public static String decrypt(byte[] textoencriptado) throws Exception{
   Cipher decripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
   SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes("UTF-8"), "AES");
   decripta.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
   return new String(decripta.doFinal(textoencriptado),"UTF-8");
 }

}