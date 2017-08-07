package org.iotp.server.service.security.auth.rest;

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
import org.iotp.server.service.security.model.SecurityUser;
import org.iotp.server.service.security.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RestAuthenticationProvider implements AuthenticationProvider {

  private final BCryptPasswordEncoder encoder;
  private final UserService userService;
  private final CustomerService customerService;

  @Autowired
  public RestAuthenticationProvider(final UserService userService, final CustomerService customerService,
      final BCryptPasswordEncoder encoder) {
    this.userService = userService;
    this.customerService = customerService;
    this.encoder = encoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Assert.notNull(authentication, "No authentication data provided");

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof UserPrincipal)) {
      throw new BadCredentialsException("Authentication Failed. Bad user principal.");
    }

    UserPrincipal userPrincipal = (UserPrincipal) principal;
    if (userPrincipal.getType() == UserPrincipal.Type.USER_NAME) {
      String username = userPrincipal.getValue();
      String password = (String) authentication.getCredentials();
      return authenticateByUsernameAndPassword(userPrincipal, username, password);
    } else {
      String publicId = userPrincipal.getValue();
      return authenticateByPublicId(userPrincipal, publicId);
    }
  }

  private Authentication authenticateByUsernameAndPassword(UserPrincipal userPrincipal, String username,
      String password) {
    User user = userService.findUserByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found: " + username);
    }

    UserCredentials userCredentials = userService.findUserCredentialsByUserId(user.getId());
    if (userCredentials == null) {
      throw new UsernameNotFoundException("User credentials not found");
    }

    if (!userCredentials.isEnabled()) {
      throw new DisabledException("User is not active");
    }

    if (!encoder.matches(password, userCredentials.getPassword())) {
      throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
    }

    if (user.getAuthority() == null)
      throw new InsufficientAuthenticationException("User has no authority assigned");

    SecurityUser securityUser = new SecurityUser(user, userCredentials.isEnabled(), userPrincipal);

    return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
  }

  private Authentication authenticateByPublicId(UserPrincipal userPrincipal, String publicId) {
    CustomerId customerId;
    try {
      customerId = new CustomerId(UUID.fromString(publicId));
    } catch (Exception e) {
      throw new BadCredentialsException("Authentication Failed. Public Id is not valid.");
    }
    Customer publicCustomer = customerService.findCustomerById(customerId);
    if (publicCustomer == null) {
      throw new UsernameNotFoundException("Public entity not found: " + publicId);
    }
    boolean isPublic = false;
    if (publicCustomer.getAdditionalInfo() != null && publicCustomer.getAdditionalInfo().has("isPublic")) {
      isPublic = publicCustomer.getAdditionalInfo().get("isPublic").asBoolean();
    }
    if (!isPublic) {
      throw new BadCredentialsException("Authentication Failed. Public Id is not valid.");
    }
    User user = new User(new UserId(UUIDBased.EMPTY));
    user.setTenantId(publicCustomer.getTenantId());
    user.setCustomerId(publicCustomer.getId());
    user.setEmail(publicId);
    user.setAuthority(Authority.CUSTOMER_USER);
    user.setFirstName("Public");
    user.setLastName("Public");

    SecurityUser securityUser = new SecurityUser(user, true, userPrincipal);

    return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
