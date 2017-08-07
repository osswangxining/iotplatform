package org.iotp.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.iotp.infomgt.data.User;
import org.iotp.infomgt.data.id.CustomerId;
import org.iotp.infomgt.data.id.TenantId;
import org.iotp.infomgt.data.id.UserId;
import org.iotp.infomgt.data.page.TextPageData;
import org.iotp.infomgt.data.page.TextPageLink;
import org.iotp.infomgt.data.security.Authority;
import org.iotp.infomgt.data.security.UserCredentials;
import org.iotp.server.exception.IoTPErrorCode;
import org.iotp.server.exception.IoTPException;
import org.iotp.server.service.mail.MailService;
import org.iotp.server.service.security.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController extends BaseController {

    @Autowired
    private MailService mailService;

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public User getUserById(@PathVariable("userId") String strUserId) throws IoTPException {
        checkParameter("userId", strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            SecurityUser authUser = getCurrentUser();
            if (authUser.getAuthority() == Authority.CUSTOMER_USER && !authUser.getId().equals(userId)) {
                throw new IoTPException("You don't have permission to perform this operation!",
                        IoTPErrorCode.PERMISSION_DENIED);
            }
            return checkUserId(userId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody 
    public User saveUser(@RequestBody User user,
                         @RequestParam(required = false, defaultValue = "true") boolean sendActivationMail,
            HttpServletRequest request) throws IoTPException {
        try {
            SecurityUser authUser = getCurrentUser();
            if (authUser.getAuthority() == Authority.CUSTOMER_USER && !authUser.getId().equals(user.getId())) {
                throw new IoTPException("You don't have permission to perform this operation!",
                        IoTPErrorCode.PERMISSION_DENIED);
            }
            boolean sendEmail = user.getId() == null && sendActivationMail;
            if (getCurrentUser().getAuthority() == Authority.TENANT_ADMIN) {
                user.setTenantId(getCurrentUser().getTenantId());
            }
            User savedUser = checkNotNull(userService.saveUser(user));
            if (sendEmail) {
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(savedUser.getId());
                String baseUrl = constructBaseUrl(request);
                String activateUrl = String.format("%s/api/noauth/activate?activateToken=%s", baseUrl,
                        userCredentials.getActivateToken());
                String email = savedUser.getEmail();
                try {
                    mailService.sendActivationEmail(activateUrl, email);
                } catch (IoTPException e) {
                    userService.deleteUser(savedUser.getId());
                    throw e;
                }
            }
            return savedUser;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/sendActivationMail", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void sendActivationEmail(
            @RequestParam(value = "email") String email,
            HttpServletRequest request) throws IoTPException {
        try {
            User user = checkNotNull(userService.findUserByEmail(email));
            UserCredentials userCredentials = userService.findUserCredentialsByUserId(user.getId());
            if (!userCredentials.isEnabled()) {
                String baseUrl = constructBaseUrl(request);
                String activateUrl = String.format("%s/api/noauth/activate?activateToken=%s", baseUrl,
                        userCredentials.getActivateToken());
                mailService.sendActivationEmail(activateUrl, email);
            } else {
                throw new IoTPException("User is already active!", IoTPErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}/activationLink", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getActivationLink(
            @PathVariable("userId") String strUserId,
            HttpServletRequest request) throws IoTPException {
        checkParameter("userId", strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            SecurityUser authUser = getCurrentUser();
            if (authUser.getAuthority() == Authority.CUSTOMER_USER && !authUser.getId().equals(userId)) {
                throw new IoTPException("You don't have permission to perform this operation!",
                        IoTPErrorCode.PERMISSION_DENIED);
            }
            User user = checkUserId(userId);
            UserCredentials userCredentials = userService.findUserCredentialsByUserId(user.getId());
            if (!userCredentials.isEnabled()) {
                String baseUrl = constructBaseUrl(request);
                String activateUrl = String.format("%s/api/noauth/activate?activateToken=%s", baseUrl,
                        userCredentials.getActivateToken());
                return activateUrl;
            } else {
                throw new IoTPException("User is already active!", IoTPErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(@PathVariable("userId") String strUserId) throws IoTPException {
        checkParameter("userId", strUserId);
        try {
            UserId userId = new UserId(toUUID(strUserId));
            checkUserId(userId);
            userService.deleteUser(userId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/tenant/{tenantId}/users", params = { "limit" }, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<User> getTenantAdmins(
            @PathVariable("tenantId") String strTenantId,
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        checkParameter("tenantId", strTenantId);
        try {
            TenantId tenantId = new TenantId(toUUID(strTenantId));
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            return checkNotNull(userService.findTenantAdmins(tenantId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/users", params = { "limit" }, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<User> getCustomerUsers(
            @PathVariable("customerId") String strCustomerId,
            @RequestParam int limit,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws IoTPException {
        checkParameter("customerId", strCustomerId);
        try {
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId);
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(userService.findCustomerUsers(tenantId, customerId, pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
}
