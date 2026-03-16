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

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 3600000)
    public void sincronizacionAutomatica() {
        System.out.println("Iniciando sincronización automática de Dinastía Arg...");
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

        // Acá recibís el access_token y el refresh_token
        // IMPORTANTE: Deberías guardarlos en la base de datos para usarlos siempre
        String accessToken = (String) response.getBody().get("access_token");
        System.out.println("Nuevo Token: " + accessToken);
    }

    public void importarProductos(String sellerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // CAMBIO CLAVE: Usamos el endpoint de items por vendedor
        // Este endpoint devuelve solo una lista de IDs (ej: ["MLA1","MLA2"])
        String urlIds = "https://api.mercadolibre.com/users/" + sellerId + "/items/search";

        try {
            ResponseEntity<Map> responseIds = restTemplate.exchange(urlIds, HttpMethod.GET, entity, Map.class);
            List<String> resultsIds = (List<String>) responseIds.getBody().get("results");

            if (resultsIds == null || resultsIds.isEmpty()) return;

            // Ahora pedimos el detalle de cada ID (limitamos a los primeros 20 para no saturar)
            String idsJoined = String.join(",", resultsIds.subList(0, Math.min(resultsIds.size(), 20)));
            String urlDetalle = "https://api.mercadolibre.com/items?ids=" + idsJoined;

            ResponseEntity<List> responseDetails = restTemplate.exchange(urlDetalle, HttpMethod.GET, entity, List.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) responseDetails.getBody();

            for (Map<String, Object> container : items) {
                // El resultado viene envuelto en un objeto con "code" y "body"
                Map<String, Object> body = (Map<String, Object>) container.get("body");
                if (body == null) continue;

                String idMeLi = (String) body.get("id");

                if (productoRepository.findByMercadoLibreId(idMeLi) == null) {
                    Producto p = new Producto();
                    p.setMercadoLibreId(idMeLi);
                    p.setNombre((String) body.get("title"));
                    p.setPrecio(new BigDecimal(body.get("price").toString()));

                    // Imagen HD (buscamos la primera foto del array de pictures)
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
        } catch (Exception e) {
            throw new RuntimeException("Error en MeLi: " + e.getMessage());
        }
    }
}