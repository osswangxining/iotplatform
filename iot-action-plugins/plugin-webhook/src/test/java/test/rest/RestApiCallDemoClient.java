package test.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RestApiCallDemoClient {

  private static final String DEMO_REST_BASIC_AUTH = "/demo-rest-basic-auth";
  private static final String DEMO_REST_NO_AUTH = "/demo-rest-no-auth";
  private static final String USERNAME = "demo";
  private static final String PASSWORD = "demo";
  private static final int HTTP_SERVER_PORT = 8888;

  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(HTTP_SERVER_PORT), 0);

    HttpContext secureContext = server.createContext(DEMO_REST_BASIC_AUTH, new RestDemoHandler());
    secureContext.setAuthenticator(new BasicAuthenticator("demo-auth") {
      @Override
      public boolean checkCredentials(String user, String pwd) {
        return user.equals(USERNAME) && pwd.equals(PASSWORD);
      }
    });

    server.createContext(DEMO_REST_NO_AUTH, new RestDemoHandler());
    server.setExecutor(null);
    System.out.println("[*] Waiting for messages.");
    server.start();
  }

  private static class RestDemoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String requestBody;
      try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8"))) {
        requestBody = br.lines().collect(Collectors.joining(System.lineSeparator()));
      }
      System.out.println("[x] Received body: \n" + requestBody);

      String response = "Hello from demo client!";
      exchange.sendResponseHeaders(200, response.length());
      System.out.println("[x] Sending response: \n" + response);

      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }
}