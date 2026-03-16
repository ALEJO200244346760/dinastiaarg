package com.dinastiaarg.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${ML_CLIENT_ID}")
    private String clientId;

    @Value("${ML_REDIRECT_URI}")
    private String redirectUri;

    @GetMapping("/link")
    public String getAuthLink() {
        // Este link lo tiene que abrir tu mamá una vez
        return "https://auth.mercadolibre.com.ar/authorization?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
    }
}
