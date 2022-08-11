package com.example.clients;

import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashingUtils {


  /**
   * Use javax.xml.bind.DatatypeConverter class in JDK to convert byte array
   * to a hexadecimal string. Note that this generates hexadecimal in upper case.
   * @param hash
   * @return
   */
  private static String  bytesToHex(final byte[] hash) {
    return DatatypeConverter.printHexBinary(hash);
  }

  /**
   * Returns a hexadecimal encoded SHA-256 hash for the input String.
   * @param data
   * @return
   */
  public static String getSHA256Hash(final String data) {
    String result = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(data.getBytes("UTF-8"));
      result = bytesToHex(hash); // make it printable
    }catch(Exception ex) {
      log.error("Exception while hashing: {}", ex.getMessage());
      ex.printStackTrace();
    }
    return result;
  }

}
