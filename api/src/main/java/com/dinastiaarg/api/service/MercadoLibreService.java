package com.dinastiaarg.api.service;

import com.dinastiaarg.api.model.Producto;
import com.dinastiaarg.api.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class MercadoLibreService {

    @Autowired
    private ProductoRepository productoRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String currentToken = "APP_USR-3616307332149511-031614-1239c11e12a3e409f03790faf7d193eb-43712155";

    public void importarTodoElStock(String sellerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + currentToken);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            String urlIds = "https://api.mercadolibre.com/users/" + sellerId + "/items/search";
            ResponseEntity<Map> respIds = restTemplate.exchange(urlIds, HttpMethod.GET, entity, Map.class);

            @SuppressWarnings("unchecked")
            List<String> ids = (List<String>) respIds.getBody().get("results");

            if (ids == null || ids.isEmpty()) return;

            for (String id : ids) {
                try {
                    String urlDetalle = "https://api.mercadolibre.com/items/" + id;
                    ResponseEntity<Map> respDetalle = restTemplate.exchange(urlDetalle, HttpMethod.GET, entity, Map.class);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> body = (Map<String, Object>) respDetalle.getBody();

                    if (body != null) {
                        guardarOActualizarProducto(body);
                    }
                } catch (Exception e) {
                    System.err.println("Error con el item " + id + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error MeLi: " + e.getMessage());
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

        if (pics != null && !pics.isEmpty()) {
            p.setImagenUrl((String) pics.get(0).get("url"));
        } else {
            p.setImagenUrl((String) body.get("thumbnail"));
        }

        p.setActivo(true);
        p.setCategoria("joyas");
        productoRepository.save(p);
    }
}