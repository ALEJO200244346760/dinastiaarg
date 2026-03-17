package com.dinastiaarg.api.controller;

import com.dinastiaarg.api.service.MercadoLibreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private MercadoLibreService mercadoLibreService;

    @GetMapping("/login")
    public RedirectView login() {
        String clientId = "3616307332149511";
        String redirectUri = "https://dinastiaarg-production.up.railway.app/api/auth/callback";
        String url = "https://auth.mercadolibre.com.ar/authorization?response_type=code&client_id="
                + clientId + "&redirect_uri=" + redirectUri;
        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) {
        try {
            mercadoLibreService.intercambiarCodePorToken(code);
            return "¡Autorización exitosa para Dinastía Arg! Ya podés cerrar esta pestaña y usar el botón de la web.";
        } catch (Exception e) {
            return "Error al autorizar: " + e.getMessage();
        }
    }
}