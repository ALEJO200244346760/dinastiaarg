package com.dinastiaarg.api.service;

import com.dinastiaarg.api.model.Producto;
import com.dinastiaarg.api.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap; // IMPORTANTE
import org.springframework.util.MultiValueMap;       // IMPORTANTE
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class MercadoLibreService {

    @Autowired
    private ProductoRepository productoRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // Credenciales de tu App
    private final String clientId = "3616307332149511";
    private final String clientSecret = "6Q2aMPBwjDKCu4B0lrgizD2Cb26QpHp1";
    private final String redirectUri = "https://dinastiaarg-production.up.railway.app/api/auth/callback";

    private String accessToken = "";
    private String refreshToken = "";

    // 1. Intercambio de CODE por TOKEN (Solo se hace una vez al autorizar)
    public String intercambiarCodePorToken(String code) {
        return solicitarToken("authorization_code", "code", code);
    }

    // 2. RENOVACIÓN AUTOMÁTICA (Se llama si el token falla o vence)
    public void renovarToken() {
        if (refreshToken.isEmpty()) throw new RuntimeException("No hay refresh token guardado");
        solicitarToken("refresh_token", "refresh_token", refreshToken);
    }

    private String solicitarToken(String grantType, String paramName, String value) {
        String url = "https://api.mercadolibre.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grantType);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add(paramName, value);
        map.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> body = response.getBody();
            this.accessToken = (String) body.get("access_token");
            this.refreshToken = (String) body.get("refresh_token");
            return this.accessToken;
        } catch (Exception e) {
            throw new RuntimeException("Error en OAuth: " + e.getMessage());
        }
    }

    // 3. IMPORTACIÓN CON RE-INTENTO (Si da 401/403, renueva el token solo)
    public void importarTodoElStock(String sellerId) {
        try {
            ejecutarImportacion(sellerId);
        } catch (Exception e) {
            if (e.getMessage().contains("401") || e.getMessage().contains("403")) {
                renovarToken();
                ejecutarImportacion(sellerId);
            } else {
                throw e;
            }
        }
    }

    private void ejecutarImportacion(String sellerId) {
        String url = "https://api.mercadolibre.com/users/" + sellerId + "/items/search";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        @SuppressWarnings("unchecked")
        List<String> ids = (List<String>) res.getBody().get("results");
        if (ids != null) {
            for (String id : ids) {
                importarProductoIndividual(id);
            }
        }
    }

    public void importarProductoIndividual(String itemId) {
        String url = "https://api.mercadolibre.com/items/" + itemId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = (Map<String, Object>) response.getBody();
                guardarOActualizarProducto(body);
            }
        } catch (Exception e) {
            System.err.println("Error item " + itemId + ": " + e.getMessage());
        }
    }

    private void guardarOActualizarProducto(Map<String, Object> body) {
        String mlId = (String) body.get("id");
        Producto p = productoRepository.findByMercadoLibreId(mlId);
        if (p == null) {
            p = new Producto();
            p.setMercadoLibreId(mlId);
        }
        p.setNombre((String) body.get("title"));
        p.setPrecio(new BigDecimal(body.get("price").toString()));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> pics = (List<Map<String, Object>>) body.get("pictures");
        p.setImagenUrl(pics != null && !pics.isEmpty() ? (String) pics.get(0).get("url") : (String) body.get("thumbnail"));

        p.setActivo(true);
        p.setCategoria("joyas");
        productoRepository.save(p);
    }
}