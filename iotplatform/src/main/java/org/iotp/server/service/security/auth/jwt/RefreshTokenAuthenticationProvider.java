package org.iotp.server.service.security.auth.jwt;

import java.util.UUID;

import org.iotp.infomgt.dao.customer.CustomerService;
import org.iotp.infomgt.dao.user.UserService;
import org.iotp.infomgt.data.Customer;
import org.iotp.infomgt.data.User;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.UUIDBased;
import org.iotp.infomgt.data.id.UserId;
import org.iotp.infomgt.data.security.Authority;
import org.iotp.infomgt.data.security.UserCredentials;
import org.iotp.server.service.security.auth.RefreshAuthenticationToken;
import org.iotp.server.service.security.model.SecurityUser;
import org.iotp.server.service.security.model.UserPrincipal;
import org.iotp.server.service.security.model.token.JwtTokenFactory;
import org.iotp.server.service.security.model.token.RawAccessJwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

  private final JwtTokenFactory tokenFactory;
  private final UserService userService;
  private final CustomerService customerService;

  @Autowired
  public RefreshTokenAuthenticationProvider(final UserService userService, final CustomerService customerService,
      final JwtTokenFactory tokenFactory) {
    this.userService = userService;
    this.customerService = customerService;
    this.tokenFactory = tokenFactory;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Assert.notNull(authentication, "No authentication data provided");
    RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
    SecurityUser unsafeUser = tokenFactory.parseRefreshToken(rawAccessToken);
    UserPrincipal principal = unsafeUser.getUserPrincipal();
    SecurityUser securityUser;
    if (principal.getType() == UserPrincipal.Type.USER_NAME) {
      securityUser = authenticateByUserId(unsafeUser.getId());
    } else {
      securityUser = authenticateByPublicId(principal.getValue());
    }
    return new RefreshAuthenticationToken(securityUser);
  }

  private SecurityUser authenticateByUserId(UserId userId) {
    User user = userService.findUserById(userId);
    if (user == null) {
      throw new UsernameNotFoundException("User not found by refresh token");
    }

    UserCredentials userCredentials = userService.findUserCredentialsByUserId(user.getId());
    if (userCredentials == null) {
      throw new UsernameNotFoundException("User credentials not found");
    }

    if (!userCredentials.isEnabled()) {
      throw new DisabledException("User is not active");
    }

    if (user.getAuthority() == null)
      throw new InsufficientAuthenticationException("User has no authority assigned");

    UserPrincipal userPrincipal = new UserPrincipal(UserPrincipal.Type.USER_NAME, user.getEmail());

    SecurityUser securityUser = new SecurityUser(user, userCredentials.isEnabled(), userPrincipal);

    return securityUser;
  }

  private SecurityUser authenticateByPublicId(String publicId) {
    CustomerId customerId;
    try {
      customerId = new CustomerId(UUID.fromString(publicId));
    } catch (Exception e) {
      throw new BadCredentialsException("Refresh token is not valid");
    }
    Customer publicCustomer = customerService.findCustomerById(customerId);
    if (publicCustomer == null) {
      throw new UsernameNotFoundException("Public entity not found by refresh token");
    }
    boolean isPublic = false;
    if (publicCustomer.getAdditionalInfo() != null && publicCustomer.getAdditionalInfo().has("isPublic")) {
      isPublic = publicCustomer.getAdditionalInfo().get("isPublic").asBoolean();
    }
    if (!isPublic) {
      throw new BadCredentialsException("Refresh token is not valid");
    }
    User user = new User(new UserId(UUIDBased.EMPTY));
    user.setTenantId(publicCustomer.getTenantId());
    user.setCustomerId(publicCustomer.getId());
    user.setEmail(publicId);
    user.setAuthority(Authority.CUSTOMER_USER);
    user.setFirstName("Public");
    user.setLastName("Public");

    UserPrincipal userPrincipal = new UserPrincipal(UserPrincipal.Type.PUBLIC_ID, publicId);

    SecurityUser securityUser = new SecurityUser(user, true, userPrincipal);

    return securityUser;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (RefreshAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
