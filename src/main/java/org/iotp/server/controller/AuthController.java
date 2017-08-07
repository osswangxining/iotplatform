package org.iotp.server.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.iotp.infomgt.dao.user.UserService;
import org.iotp.infomgt.data.User;
import org.iotp.infomgt.data.security.UserCredentials;
import org.iotp.server.exception.IoTPErrorCode;
import org.iotp.server.exception.IoTPException;
import org.iotp.server.service.mail.MailService;
import org.iotp.server.service.security.auth.jwt.RefreshTokenRepository;
import org.iotp.server.service.security.model.SecurityUser;
import org.iotp.server.service.security.model.UserPrincipal;
import org.iotp.server.service.security.model.token.JwtToken;
import org.iotp.server.service.security.model.token.JwtTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController extends BaseController {



    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenFactory tokenFactory;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/auth/user", method = RequestMethod.GET)
    public @ResponseBody User getUser() throws IoTPException {
        try {
            SecurityUser securityUser = getCurrentUser();
            return userService.findUserById(securityUser.getId());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/auth/changePassword", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void changePassword (
            @RequestParam(value = "currentPassword") String currentPassword,
            @RequestParam(value = "newPassword") String newPassword) throws IoTPException {
        try {
            SecurityUser securityUser = getCurrentUser();
            UserCredentials userCredentials = userService.findUserCredentialsByUserId(securityUser.getId());
            if (!passwordEncoder.matches(currentPassword, userCredentials.getPassword())) {
                throw new IoTPException("Current password doesn't match!", IoTPErrorCode.BAD_REQUEST_PARAMS);
            }
            userCredentials.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUserCredentials(userCredentials);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    @RequestMapping(value = "/noauth/activate", params = { "activateToken" }, method = RequestMethod.GET)
    public ResponseEntity<String> checkActivateToken(
            @RequestParam(value = "activateToken") String activateToken) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus;
        UserCredentials userCredentials = userService.findUserCredentialsByActivateToken(activateToken);
        if (userCredentials != null) {
            String createPasswordURI = "/login/createPassword";
            try {
                URI location = new URI(createPasswordURI + "?activateToken=" + activateToken);
                headers.setLocation(location);
                responseStatus = HttpStatus.SEE_OTHER;
            } catch (URISyntaxException e) {
                log.error("Unable to create URI with address [{}]", createPasswordURI);
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } else {
            responseStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(headers, responseStatus);
    }
    
    @RequestMapping(value = "/noauth/resetPasswordByEmail", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void requestResetPasswordByEmail (
            @RequestParam(value = "email") String email,
            HttpServletRequest request) throws IoTPException {
        try {
            UserCredentials userCredentials = userService.requestPasswordReset(email);
            String baseUrl = constructBaseUrl(request);
            String resetPasswordUrl = String.format("%s/api/noauth/resetPassword?resetToken=%s", baseUrl,
                    userCredentials.getResetToken());
            
            mailService.sendResetPasswordEmail(resetPasswordUrl, email);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    @RequestMapping(value = "/noauth/resetPassword", params = { "resetToken" }, method = RequestMethod.GET)
    public ResponseEntity<String> checkResetToken(
            @RequestParam(value = "resetToken") String resetToken) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus;
        String resetPasswordURI = "/login/resetPassword";
        UserCredentials userCredentials = userService.findUserCredentialsByResetToken(resetToken);
        if (userCredentials != null) {
            try {
                URI location = new URI(resetPasswordURI + "?resetToken=" + resetToken);
                headers.setLocation(location);
                responseStatus = HttpStatus.SEE_OTHER;
            } catch (URISyntaxException e) {
                log.error("Unable to create URI with address [{}]", resetPasswordURI);
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } else {
            responseStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(headers, responseStatus);
    }
    
    @RequestMapping(value = "/noauth/activate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JsonNode activateUser(
            @RequestParam(value = "activateToken") String activateToken,
            @RequestParam(value = "password") String password,
            HttpServletRequest request) throws IoTPException {
        try {
            String encodedPassword = passwordEncoder.encode(password);
            UserCredentials credentials = userService.activateUserCredentials(activateToken, encodedPassword);
            User user = userService.findUserById(credentials.getUserId());
            UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.USER_NAME, user.getEmail());
            SecurityUser securityUser = new SecurityUser(user, credentials.isEnabled(), principal);
            String baseUrl = constructBaseUrl(request);
            String loginUrl = String.format("%s/login", baseUrl);
            String email = user.getEmail();

            try {
                mailService.sendAccountActivatedEmail(loginUrl, email);
            } catch (Exception e) {
                log.info("Unable to send account activation email [{}]", e.getMessage());
            }

            JwtToken accessToken = tokenFactory.createAccessJwtToken(securityUser);
            JwtToken refreshToken = refreshTokenRepository.requestRefreshToken(securityUser);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode tokenObject = objectMapper.createObjectNode();
            tokenObject.put("token", accessToken.getToken());
            tokenObject.put("refreshToken", refreshToken.getToken());
            return tokenObject;
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    @RequestMapping(value = "/noauth/resetPassword", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JsonNode resetPassword(
            @RequestParam(value = "resetToken") String resetToken,
            @RequestParam(value = "password") String password,
            HttpServletRequest request) throws IoTPException {
        try {
            UserCredentials userCredentials = userService.findUserCredentialsByResetToken(resetToken);
            if (userCredentials != null) {
                String encodedPassword = passwordEncoder.encode(password);
                userCredentials.setPassword(encodedPassword);
                userCredentials.setResetToken(null);
                userCredentials = userService.saveUserCredentials(userCredentials);
                User user = userService.findUserById(userCredentials.getUserId());
                UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.USER_NAME, user.getEmail());
                SecurityUser securityUser = new SecurityUser(user, userCredentials.isEnabled(), principal);
                String baseUrl = constructBaseUrl(request);
                String loginUrl = String.format("%s/login", baseUrl);
                String email = user.getEmail();
                mailService.sendPasswordWasResetEmail(loginUrl, email);

                JwtToken accessToken = tokenFactory.createAccessJwtToken(securityUser);
                JwtToken refreshToken = refreshTokenRepository.requestRefreshToken(securityUser);

                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode tokenObject = objectMapper.createObjectNode();
                tokenObject.put("token", accessToken.getToken());
                tokenObject.put("refreshToken", refreshToken.getToken());
                return tokenObject;
            } else {
                throw new IoTPException("Invalid reset token!", IoTPErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
