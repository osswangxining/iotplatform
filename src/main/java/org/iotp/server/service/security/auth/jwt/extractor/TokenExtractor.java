package org.iotp.server.service.security.auth.jwt.extractor;

import javax.servlet.http.HttpServletRequest;

public interface TokenExtractor {
  public String extract(HttpServletRequest request);
}