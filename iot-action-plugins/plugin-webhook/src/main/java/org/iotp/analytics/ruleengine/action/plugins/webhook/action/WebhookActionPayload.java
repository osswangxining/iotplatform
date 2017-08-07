package org.iotp.analytics.ruleengine.action.plugins.webhook.action;

import java.io.Serializable;

import org.iotp.analytics.ruleengine.common.msg.session.MsgType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class WebhookActionPayload implements Serializable {
  /**
  * 
  */
  private static final long serialVersionUID = 2984659227161425769L;
  private final String actionPath;
  private final String contentType;
  private final String username;
  private final String password;
  private final String msgBody;
  private final HttpMethod httpMethod;
  private final HttpStatus expectedResultCode;
  private final boolean sync;

  private final Integer requestId;
  private final MsgType msgType;

  public static WebhookActionPayloadBuilder builder() {
    return new WebhookActionPayloadBuilder();
  }
  
  public static class WebhookActionPayloadBuilder {
    private String actionPath;
    private String contentType;
    private String username;
    private String password;
    private String msgBody;
    private HttpMethod httpMethod;
    private HttpStatus expectedResultCode;
    private boolean sync;

    private Integer requestId;
    private MsgType msgType;

    public WebhookActionPayloadBuilder() {

    }

    public WebhookActionPayloadBuilder actionPath(String actionPath) {
      this.actionPath = actionPath;
      return this;
    }
    
    public WebhookActionPayloadBuilder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }
    
    public WebhookActionPayloadBuilder username(String username) {
      this.username = username;
      return this;
    }
    
    public WebhookActionPayloadBuilder password(String password) {
      this.password = password;
      return this;
    }

    public WebhookActionPayloadBuilder msgBody(String msgBody) {
      this.msgBody = msgBody;
      return this;
    }

    public WebhookActionPayloadBuilder httpMethod(HttpMethod httpMethod) {
      this.httpMethod = httpMethod;
      return this;
    }

    public WebhookActionPayloadBuilder expectedResultCode(HttpStatus expectedResultCode) {
      this.expectedResultCode = expectedResultCode;
      return this;
    }

    public WebhookActionPayloadBuilder sync(boolean sync) {
      this.sync = sync;
      return this;
    }

    public WebhookActionPayloadBuilder requestId(Integer requestId) {
      this.requestId = requestId;
      return this;
    }

    public WebhookActionPayloadBuilder msgType(MsgType msgType) {
      this.msgType = msgType;
      return this;
    }

    public WebhookActionPayload build() {
      return new WebhookActionPayload(this.actionPath, this.contentType, this.username, this.password, this.msgBody, this.httpMethod, this.expectedResultCode,
          this.sync, this.requestId, this.msgType);
    }
  }

}
