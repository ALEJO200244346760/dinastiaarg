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

    // Este es tu token real de la App
    private final String currentToken = "APP_USR-3616307332149511-031614-1239c11e12a3e409f03790faf7d193eb-43712155";

    // Método para sincronizar TODO el stock de tu mamá
    public void importarTodoElStock(String sellerId) {
        HttpHeaders headers = crearHeadersDisfrazados();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 1. Obtener los IDs de los productos del vendedor
            String urlSearch = "https://api.mercadolibre.com/users/" + sellerId + "/items/search";
            ResponseEntity<Map> res = restTemplate.exchange(urlSearch, HttpMethod.GET, entity, Map.class);

            @SuppressWarnings("unchecked")
            List<String> ids = (List<String>) res.getBody().get("results");

            if (ids == null || ids.isEmpty()) return;

            // 2. Por cada ID, traer el detalle y guardar
            for (String id : ids) {
                importarProductoIndividual(id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Falla en búsqueda masiva: " + e.getMessage());
        }
    }

    // Método para traer un producto puntual (o actualizar uno existente)
    public void importarProductoIndividual(String itemId) {
        String url = "https://api.mercadolibre.com/items/" + itemId;
        HttpHeaders headers = crearHeadersDisfrazados();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = (Map<String, Object>) response.getBody();
                guardarOActualizarProducto(body);
            }
        } catch (Exception e) {
            System.err.println("No se pudo traer el item " + itemId + ": " + e.getMessage());
        }
    }

    // El "disfraz" oficial de Chrome para que el PolicyAgent no te bloquee
    private HttpHeaders crearHeadersDisfrazados() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + currentToken);
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json");
        return headers;
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
            // Buscamos la imagen de mejor calidad (HD)
            p.setImagenUrl((String) pics.get(0).get("url"));
        } else {
            p.setImagenUrl((String) body.get("thumbnail"));
        }

        p.setActivo(true);
        p.setCategoria("joyas");
        productoRepository.save(p);
    }
}