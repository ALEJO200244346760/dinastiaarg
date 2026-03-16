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

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    // Sincronización automática cada 1 hora
    @Scheduled(fixedRate = 3600000)
    public void sincronizacionAutomatica() {
        System.out.println("Iniciando sincronización automática de Dinastía Arg...");
        try {
            importarProductos("1297120798");
        } catch (Exception e) {
            System.err.println("Error en sync automática: " + e.getMessage());
        }
    }

    // Método para el callback de OAuth2
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
            String nuevoAccessToken = (String) response.getBody().get("access_token");
            System.out.println("Nuevo Token obtenido: " + nuevoAccessToken);
            // TODO: Guardar en DB para que sea permanente
        }
    }

    // ESTE ES EL MÉTODO QUE FALTABA Y DABA ERROR DE COMPILACIÓN
    public void importarProductoPorId(String itemMeliId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        // User-Agent para evitar el bloqueo del PolicyAgent
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlDetalle = "https://api.mercadolibre.com/items/" + itemMeliId;

        try {
            ResponseEntity<Map> response = restTemplate.exchange(urlDetalle, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null) {
                guardarOActualizarProducto(body);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al traer item individual: " + e.getMessage());
        }
    }

    // Método para importar todos los productos de un vendedor
    public void importarProductos(String sellerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlIds = "https://api.mercadolibre.com/users/" + sellerId + "/items/search";

        try {
            ResponseEntity<Map> responseIds = restTemplate.exchange(urlIds, HttpMethod.GET, entity, Map.class);
            List<String> resultsIds = (List<String>) responseIds.getBody().get("results");

            if (resultsIds == null || resultsIds.isEmpty()) return;

            String idsJoined = String.join(",", resultsIds.subList(0, Math.min(resultsIds.size(), 20)));
            String urlDetalle = "https://api.mercadolibre.com/items?ids=" + idsJoined;

            ResponseEntity<List> responseDetails = restTemplate.exchange(urlDetalle, HttpMethod.GET, entity, List.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) responseDetails.getBody();

            for (Map<String, Object> container : items) {
                Map<String, Object> body = (Map<String, Object>) container.get("body");
                if (body != null && body.get("id") != null) {
                    guardarOActualizarProducto(body);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en importación masiva: " + e.getMessage());
        }
    }

    // Función auxiliar para evitar repetir código de guardado
    private void guardarOActualizarProducto(Map<String, Object> body) {
        String idMeLi = (String) body.get("id");
        Producto p = productoRepository.findByMercadoLibreId(idMeLi);

        if (p == null) {
            p = new Producto();
            p.setMercadoLibreId(idMeLi);
        }

        p.setNombre((String) body.get("title"));
        p.setPrecio(new BigDecimal(body.get("price").toString()));

        List<Map<String, Object>> pictures = (List<Map<String, Object>>) body.get("pictures");
        if (pictures != null && !pictures.isEmpty()) {
            p.setImagenUrl((String) pictures.get(0).get("url"));
        } else {
            p.setImagenUrl((String) body.get("thumbnail"));
        }

        p.setActivo(true);
        p.setCategoria("joyas");
        productoRepository.save(p);
    }
}