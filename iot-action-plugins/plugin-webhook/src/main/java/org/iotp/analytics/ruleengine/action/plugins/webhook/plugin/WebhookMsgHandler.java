package org.iotp.analytics.ruleengine.action.plugins.webhook.plugin;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.config.SocketConfig.Builder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.iotp.analytics.ruleengine.action.plugins.webhook.action.WebhookActionMsg;
import org.iotp.analytics.ruleengine.action.plugins.webhook.action.WebhookActionPayload;
import org.iotp.analytics.ruleengine.api.plugins.PluginContext;
import org.iotp.analytics.ruleengine.api.plugins.handlers.RuleMsgHandler;
import org.iotp.analytics.ruleengine.api.rules.RuleException;
import org.iotp.analytics.ruleengine.common.msg.core.BasicStatusCodeResponse;
import org.iotp.analytics.ruleengine.plugins.msg.ResponsePluginToRuleMsg;
import org.iotp.analytics.ruleengine.plugins.msg.RuleToPluginMsg;
import org.iotp.infomgt.data.id.RuleId;
import org.iotp.infomgt.data.id.TenantId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WebhookMsgHandler implements RuleMsgHandler {

  private final String baseUrl;
  private final HttpHeaders headers;

  @Override
  public void process(PluginContext ctx, TenantId tenantId, RuleId ruleId, RuleToPluginMsg<?> msg)
      throws RuleException {
    if (!(msg instanceof WebhookActionMsg)) {
      throw new RuleException("Unsupported message type " + msg.getClass().getName() + "!");
    }
    WebhookActionPayload payload = ((WebhookActionMsg) msg).getPayload();
    try {
      HttpMethod httpMethod = payload.getHttpMethod();

      log.info("baseUrl:{},payload.getActionPath():{}", baseUrl, payload.getActionPath());
      log.info("payload.getHttpMethod():{}", httpMethod);
      log.info("headers:{}", headers);
      String url = baseUrl + payload.getActionPath();
      String contentType = payload.getContentType();
      String username = payload.getUsername();
      String password = payload.getPassword();
      ResponseEntity<String> responseEntity = null;
      if (httpMethod.matches(HttpMethod.GET.name())) {
        responseEntity = get(url, contentType, username, password);
      } else if (httpMethod.matches(HttpMethod.POST.name())) {
        String body = payload.getMsgBody();
        responseEntity = post(url, contentType, body, username, password);
      } else if (httpMethod.matches(HttpMethod.PUT.name())) {
        String body = payload.getMsgBody();
        responseEntity = put(url, contentType, body, username, password);
      } else if (httpMethod.matches(HttpMethod.DELETE.name())) {
        responseEntity = delete(url, contentType, username, password);
      } else if (httpMethod.matches(HttpMethod.HEAD.name())) {
        responseEntity = head(url, contentType, username, password);
      } else if (httpMethod.matches(HttpMethod.PATCH.name())) {
        String body = payload.getMsgBody();
        responseEntity = patch(url, contentType, body, username, password);
      }
      // ResponseEntity<String> exchangeResponse = new
      // RestTemplate().exchange(baseUrl + payload.getActionPath(),
      // payload.getHttpMethod(), new HttpEntity<>(payload.getMsgBody(),
      // headers), String.class);
      if (responseEntity != null) {
        if (responseEntity.getStatusCode().equals(payload.getExpectedResultCode()) && payload.isSync()) {
          ctx.reply(new ResponsePluginToRuleMsg(msg.getUid(), tenantId, ruleId,
              BasicStatusCodeResponse.onSuccess(payload.getMsgType(), payload.getRequestId())));
        } else if (!responseEntity.getStatusCode().equals(payload.getExpectedResultCode())) {
          throw new RuntimeException("Response Status Code '" + responseEntity.getStatusCode()
              + "' doesn't equals to Expected Status Code '" + payload.getExpectedResultCode() + "'");
        }
      }

    } catch (RestClientException e) {
      throw new RuleException(e.getMessage(), e);
    }
  }

  private static ResponseEntity<String> get(String url, String contentType, String username, String password) {
    HttpGet request = new HttpGet(url);
    request.addHeader("Content-Type", contentType);
    request.addHeader("Accept", contentType);
    request.setHeader("Connection", "close");

    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
      String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      request.setHeader("Authorization", "Basic " + encoding);
    }

    HttpClient httpClient = createHttpOrHttpsClient(url);

    HttpResponse response = null;
    HttpEntity entity = null;
    ResponseEntity<String> res = null;
    try {
      response = httpClient.execute(request);
      entity = response.getEntity();
      int statusCode = response.getStatusLine().getStatusCode();
      if (entity != null) {
        String string = IOUtils.toString(entity.getContent(), "UTF-8");
        res = ResponseEntity.status(statusCode).body(string);
      } else {
        res = ResponseEntity.status(statusCode).body("");
      }
    } catch (Exception e) {
      e.printStackTrace();
      res = ResponseEntity.status(500).body(e.getMessage());
    } finally {
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }

  private static ResponseEntity<String> head(String url, String contentType, String username, String password) {
    HttpHead request = new HttpHead(url);
    request.addHeader("Content-Type", contentType);
    request.addHeader("Accept", contentType);
    request.setHeader("Connection", "close");

    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
      String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      request.setHeader("Authorization", "Basic " + encoding);
    }

    HttpClient httpClient = createHttpOrHttpsClient(url);

    HttpResponse response = null;
    HttpEntity entity = null;
    ResponseEntity<String> res = null;
    try {
      response = httpClient.execute(request);
      entity = response.getEntity();
      int statusCode = response.getStatusLine().getStatusCode();
      if (entity != null) {
        String string = IOUtils.toString(entity.getContent(), "UTF-8");
        res = ResponseEntity.status(statusCode).body(string);
      } else {
        res = ResponseEntity.status(statusCode).body("");
      }
    } catch (Exception e) {
      e.printStackTrace();
      res = ResponseEntity.status(500).body(e.getMessage());
    } finally {
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }

  private static ResponseEntity<String> post(String url, String contentType, String body, String username,
      String password) {
    HttpPost request = new HttpPost(url);
    request.addHeader("Content-Type", contentType);
    request.addHeader("Accept", contentType);
    request.setHeader("Connection", "close");

    StringEntity inputEntity = new StringEntity(body, "UTF-8");
    request.setEntity(inputEntity);

    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
      String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      request.setHeader("Authorization", "Basic " + encoding);
    }

    HttpClient httpClient = createHttpOrHttpsClient(url);

    HttpResponse response = null;
    HttpEntity entity = null;
    ResponseEntity<String> res = null;
    try {
      response = httpClient.execute(request);
      entity = response.getEntity();
      int statusCode = response.getStatusLine().getStatusCode();
      if (entity != null) {
        String string = IOUtils.toString(entity.getContent(), "UTF-8");
        res = ResponseEntity.status(statusCode).body(string);
      } else {
        res = ResponseEntity.status(statusCode).body("");
      }
    } catch (Exception e) {
      e.printStackTrace();
      res = ResponseEntity.status(500).body(e.getMessage());
    } finally {
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }

  private static ResponseEntity<String> put(String url, String contentType, String body, String username,
      String password) {
    HttpPut request = new HttpPut(url);
    request.addHeader("Content-Type", contentType);
    request.addHeader("Accept", contentType);
    request.setHeader("Connection", "close");

    StringEntity inputEntity = new StringEntity(body, "UTF-8");
    request.setEntity(inputEntity);

    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
      String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      request.setHeader("Authorization", "Basic " + encoding);
    }

    HttpClient httpClient = createHttpOrHttpsClient(url);

    HttpResponse response = null;
    HttpEntity entity = null;
    ResponseEntity<String> res = null;
    try {
      response = httpClient.execute(request);
      entity = response.getEntity();
      int statusCode = response.getStatusLine().getStatusCode();
      if (entity != null) {
        String string = IOUtils.toString(entity.getContent(), "UTF-8");
        res = ResponseEntity.status(statusCode).body(string);
      } else {
        res = ResponseEntity.status(statusCode).body("");
      }
    } catch (Exception e) {
      e.printStackTrace();
      res = ResponseEntity.status(500).body(e.getMessage());
    } finally {
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }

  private static ResponseEntity<String> patch(String url, String contentType, String body, String username,
      String password) {
    HttpPatch request = new HttpPatch(url);
    request.addHeader("Content-Type", contentType);
    request.addHeader("Accept", contentType);
    request.setHeader("Connection", "close");

    StringEntity inputEntity = new StringEntity(body, "UTF-8");
    request.setEntity(inputEntity);

    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
      String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      request.setHeader("Authorization", "Basic " + encoding);
    }

    HttpClient httpClient = createHttpOrHttpsClient(url);

    HttpResponse response = null;
    HttpEntity entity = null;
    ResponseEntity<String> res = null;
    try {
      response = httpClient.execute(request);
      entity = response.getEntity();
      int statusCode = response.getStatusLine().getStatusCode();
      if (entity != null) {
        String string = IOUtils.toString(entity.getContent(), "UTF-8");
        res = ResponseEntity.status(statusCode).body(string);
      } else {
        res = ResponseEntity.status(statusCode).body("");
      }
    } catch (Exception e) {
      e.printStackTrace();
      res = ResponseEntity.status(500).body(e.getMessage());
    } finally {
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }

  private static ResponseEntity<String> delete(String url, String contentType, String username, String password) {
    HttpDelete request = new HttpDelete(url);
    request.addHeader("Content-Type", contentType);
    request.addHeader("Accept", contentType);
    request.setHeader("Connection", "close");

    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
      String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      request.setHeader("Authorization", "Basic " + encoding);
    }

    HttpClient httpClient = createHttpOrHttpsClient(url);

    HttpResponse response = null;
    HttpEntity entity = null;
    ResponseEntity<String> res = null;
    try {
      response = httpClient.execute(request);
      entity = response.getEntity();
      int statusCode = response.getStatusLine().getStatusCode();
      if (entity != null) {
        String string = IOUtils.toString(entity.getContent(), "UTF-8");
        res = ResponseEntity.status(statusCode).body(string);
      } else {
        res = ResponseEntity.status(statusCode).body("");
      }
    } catch (Exception e) {
      e.printStackTrace();
      res = ResponseEntity.status(500).body(e.getMessage());
    } finally {
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }

  private static ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
      while (it.hasNext()) {
        HeaderElement he = it.nextElement();
        String param = he.getName();
        String value = he.getValue();
        if (value != null && param.equalsIgnoreCase("timeout")) {
          long timeout = Long.parseLong(value) * 1000;
          if (timeout > 20 * 1000) {
            return 20 * 1000;
          } else {
            return timeout;
          }
        }
      }
      return 5 * 1000;
    }
  };

  /**
   * Create HttpClient with SSL
   */
  private static CloseableHttpClient createHttpOrHttpsClient(String url) {
    PoolingHttpClientConnectionManager cm = null;
    if (url != null && url.trim().toLowerCase().startsWith("https")) {
      Registry<ConnectionSocketFactory> sslSocketFactoryRegistry = createAcceptAllSSLSocketFactoryRegistry();
      cm = new PoolingHttpClientConnectionManager(sslSocketFactoryRegistry);
    } else {
      cm = new PoolingHttpClientConnectionManager();
    }

    cm.setMaxTotal(200);
    cm.setDefaultMaxPerRoute(20);
    Builder scBuilder = SocketConfig.copy(SocketConfig.DEFAULT);
    scBuilder.setSoTimeout(10000);
    cm.setDefaultSocketConfig(scBuilder.build());
    return HttpClients.custom().setKeepAliveStrategy(myStrategy).setConnectionManager(cm).build();
  }

  private static Registry<ConnectionSocketFactory> createAcceptAllSSLSocketFactoryRegistry() {
    SSLConnectionSocketFactory sslConnSockFactory = createAcceptAllSSLSocketFactory();

    // Create the registry
    return RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslConnSockFactory).build();
  }

  private static SSLConnectionSocketFactory createAcceptAllSSLSocketFactory() {
    SSLConnectionSocketFactory sslConnSockFactory = null;

    try {
      // Create a trust strategy that accepts all certificates
      SSLContext sslContext = createAcceptsAllCertsSSLContext();

      // Create a host name verifier that accepts all host names
      HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

      // Create the SSL connections socket factory
      sslConnSockFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2" }, null,
          hostnameVerifier);
    } catch (Exception e) {
      // Do nothing
    }

    return sslConnSockFactory;
  }

  private static SSLContext createAcceptsAllCertsSSLContext()
      throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
    return (new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
      public boolean isTrusted(X509Certificate[] certificate, String authType) throws CertificateException {
        return true;
      }
    }).build());
  }

//  public static void main(String[] args) {
//    ResponseEntity<String> responseEntity = get("http://127.0.0.1:8080/api/noauth/test/now",
//        "application/json", null, null);
//    System.out.println(responseEntity);
//  }
}
