package org.iotp.server.config;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.List;

import org.iotp.infomgt.data.security.Authority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

      @Bean
      public Docket thingsboardApi() {
          TypeResolver typeResolver = new TypeResolver();
          final ResolvedType jsonNodeType =
                  typeResolver.resolve(
                          JsonNode.class);
          final ResolvedType stringType =
                  typeResolver.resolve(
                          String.class);

            return new Docket(DocumentationType.SWAGGER_2)
                    .groupName("iotp")
                    .apiInfo(apiInfo())
                    .alternateTypeRules(
                        new AlternateTypeRule(
                                jsonNodeType,
                                stringType))
                    .select()
                    .paths(apiPaths())
                    .build()
                    .securitySchemes(newArrayList(jwtTokenKey()))
                    .securityContexts(newArrayList(securityContext()));
      }

      private ApiKey jwtTokenKey() {
            return new ApiKey("X-Authorization", "JWT token", "header");
      }

      private SecurityContext securityContext() {
            return SecurityContext.builder()
                    .securityReferences(defaultAuth())
                    .forPaths(securityPaths())
                    .build();
      }

      private Predicate<String> apiPaths() {
           return regex("/api.*");
      }

      private Predicate<String> securityPaths() {
           return and(
                    regex("/api.*"),
                    not(regex("/api/noauth.*"))
           );
      }

      List<SecurityReference> defaultAuth() {
            AuthorizationScope[] authorizationScopes = new AuthorizationScope[3];
            authorizationScopes[0] = new AuthorizationScope(Authority.SYS_ADMIN.name(), "System administrator");
            authorizationScopes[1] = new AuthorizationScope(Authority.TENANT_ADMIN.name(), "Tenant administrator");
            authorizationScopes[2] = new AuthorizationScope(Authority.CUSTOMER_USER.name(), "Customer");
            return newArrayList(
                    new SecurityReference("X-Authorization", authorizationScopes));
      }

      private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                .title("IoT Platform REST API")
                .description("For instructions how to authorize requests please visit <a href='http://osswangxining.github.io/iotp/docs/rest-api/'>REST API documentation page</a>.")
                .contact(new Contact("IoT Platform", "http://osswangxining.github.io", "osswangxining@163.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/osswangxining/iotp/blob/master/LICENSE")
                .version("2.0")
                .build();
      }

}
