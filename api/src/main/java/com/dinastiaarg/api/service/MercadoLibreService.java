package com.dinastiaarg.api.service;

import com.dinastiaarg.api.model.Producto;
import com.dinastiaarg.api.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class MercadoLibreService {

    @Autowired
    private ProductoRepository productoRepository;

    @Value("${ML_CLIENT_ID}")
    private String clientId;

    @Value("${ML_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${ML_REDIRECT_URI}")
    private String redirectUri;

    // Este es el token que me pasaste recién
    private String currentToken = "APP_USR-3616307332149511-031614-1239c11e12a3e409f03790faf7d193eb-43712155";

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 3600000)
    public void sincronizacionAutomatica() {
        System.out.println("Iniciando sync para Dinastía Arg...");
        importarProductos("1297120798");
    }

    public void handleCallback(String code) {
        String url = "https://api.mercadolibre.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", code);
        map.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() != null) {
            this.currentToken = (String) response.getBody().get("access_token");
            System.out.println("Token actualizado con éxito");
        }
    }

    public void importarProductos(String sellerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + currentToken);
        headers.set("User-Agent", "Mozilla/5.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Endpoint de búsqueda de items del vendedor
            String url = "https://api.mercadolibre.com/users/" + sellerId + "/items/search";
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            List<String> ids = (List<String>) resp.getBody().get("results");

            if (ids == null || ids.isEmpty()) return;

            // Pedimos detalle de los productos
            String idsQuery = String.join(",", ids.subList(0, Math.min(ids.size(), 20)));
            ResponseEntity<List> details = restTemplate.exchange(
                    "https://api.mercadolibre.com/items?ids=" + idsQuery,
                    HttpMethod.GET, entity, List.class);

            List<Map<String, Object>> items = (List<Map<String, Object>>) details.getBody();
            for (Map<String, Object> item : items) {
                Map<String, Object> body = (Map<String, Object>) item.get("body");
                if (body != null) guardar(body);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void importarProductoPorId(String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + currentToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> resp = restTemplate.exchange(
                "https://api.mercadolibre.com/items/" + id,
                HttpMethod.GET, entity, Map.class);

        if (resp.getBody() != null) guardar(resp.getBody());
    }

    private void guardar(Map<String, Object> body) {
        String mlId = (String) body.get("id");
        Producto p = productoRepository.findByMercadoLibreId(mlId);
        if (p == null) {
            p = new Producto();
            p.setMercadoLibreId(mlId);
        }
        p.setNombre((String) body.get("title"));
        p.setPrecio(new BigDecimal(body.get("price").toString()));

        List<Map<String, Object>> pics = (List<Map<String, Object>>) body.get("pictures");
        p.setImagenUrl(pics != null && !pics.isEmpty() ? (String) pics.get(0).get("url") : (String) body.get("thumbnail"));

        p.setActivo(true);
        p.setCategoria("joyas");
        productoRepository.save(p);
    }
}