package org.iotp.iothub.server.security;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.iotp.infomgt.dao.EncryptionUtil;
import org.springframework.util.Base64Utils;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class SslUtil {

  private SslUtil() {
  }

  public static String getX509CertificateString(X509Certificate cert) throws CertificateEncodingException, IOException {
    return EncryptionUtil.trimNewLines(Base64Utils.encodeToString(cert.getEncoded()));
  }

  public static String getX509CertificateString(javax.security.cert.X509Certificate cert)
      throws javax.security.cert.CertificateEncodingException, IOException {
    return EncryptionUtil.trimNewLines(Base64Utils.encodeToString(cert.getEncoded()));
  }
}
