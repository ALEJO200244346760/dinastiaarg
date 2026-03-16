package com.dinastiaarg.api.controller;

import com.dinastiaarg.api.service.MercadoLibreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para que tu React pueda pegarle sin líos
public class AuthController {

    @Autowired
    private MercadoLibreService mercadoLibreService;

    @Value("${ML_CLIENT_ID}")
    private String clientId;

    @Value("${ML_REDIRECT_URI}")
    private String redirectUri;

    // 1. Este genera el link de "Permitir"
    @GetMapping("/link")
    public String getLink() {
        return "https://auth.mercadolibre.com.ar/authorization?response_type=code&client_id="
                + clientId + "&redirect_uri=" + redirectUri;
    }

    // 2. Este es el que recibe el permiso de MeLi (el callback)
    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) {
        try {
            mercadoLibreService.handleCallback(code);
            return "¡Conexión exitosa con Mercado Libre! Ya podés cerrar esta pestaña.";
        } catch (Exception e) {
            return "Error al conectar: " + e.getMessage();
        }
    }
}