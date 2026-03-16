package com.dinastiaarg.api.service;

import com.dinastiaarg.api.model.Producto;
import com.dinastiaarg.api.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    public void importarProductoPorId(String itemMeliId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlDetalle = "https://api.mercadolibre.com/items/" + itemMeliId;

        try {
            ResponseEntity<Map> response = restTemplate.exchange(urlDetalle, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null) {
                String idMeLi = (String) body.get("id");

                // Buscamos si ya existe para no duplicar
                Producto p = productoRepository.findByMercadoLibreId(idMeLi);
                if (p == null) p = new Producto();

                p.setMercadoLibreId(idMeLi);
                p.setNombre((String) body.get("title"));
                p.setPrecio(new BigDecimal(body.get("price").toString()));

                // Imagen HD
                List<Map<String, Object>> pictures = (List<Map<String, Object>>) body.get("pictures");
                if (pictures != null && !pictures.isEmpty()) {
                    p.setImagenUrl((String) pictures.get(0).get("url"));
                }

                p.setActivo(true);
                p.setCategoria("joyas");
                productoRepository.save(p);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al traer item: " + e.getMessage());
        }
    }
}