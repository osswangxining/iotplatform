package org.iotp.server.controller.plugin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PluginNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public PluginNotFoundException(String message){
        super(message);
    }

}
