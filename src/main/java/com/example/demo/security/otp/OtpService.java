package com.example.demo.security.otp;

import static com.eatthepath.otp.TimeBasedOneTimePasswordGenerator.DEFAULT_TIME_STEP;
import static com.eatthepath.otp.TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA1;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public final class OtpService {
  private final TimeBasedOneTimePasswordGenerator totp =
      new TimeBasedOneTimePasswordGenerator(
          DEFAULT_TIME_STEP,
          6,
          TOTP_ALGORITHM_HMAC_SHA1);

  public String generateOtpSecret() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
      int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
      keyGenerator.init(macLengthInBytes * 8);
      return encodeKey(keyGenerator.generateKey());
    } catch(NoSuchAlgorithmException e) {
      throw new NoSuchElementException(e);
    }
  }

  public boolean isOtpValid(String secret, String otp) {
    Key secretKey = decodeKey(secret);
    System.out.println(">>secretKey " + secretKey.getEncoded());
    try {
      String validOtp = totp.generateOneTimePasswordString(secretKey, Instant.now());
      System.out.println(">>validOtp " + validOtp);
      return validOtp.equals(otp);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }

  private String encodeKey(Key key) {
    if(Objects.isNull(key)) {
      throw new RuntimeException("Key is required");
    }
//    return Base64.getEncoder().encodeToString(key.getEncoded());
    Base32 base32 = new Base32();
    return base32.encodeToString(key.getEncoded());
  }

  private Key decodeKey(String stringKey) {
    if(Objects.isNull(stringKey)) {
      throw new RuntimeException("StringKey is required");
    }
//    byte[] keyAsBytes = Base64.getDecoder().decode(stringKey);
    Base32 base32 = new Base32();
    byte[] keyAsBytes = base32.decode(stringKey);
    return new SecretKeySpec(keyAsBytes, TOTP_ALGORITHM_HMAC_SHA1);
  }
}
