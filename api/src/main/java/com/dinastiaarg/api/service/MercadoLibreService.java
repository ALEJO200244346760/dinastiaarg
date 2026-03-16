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

    public void importarProductos(String sellerId) {
        // 1. Configuramos los Headers con el Token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. Buscamos los productos del vendedor
        String urlBusqueda = "https://api.mercadolibre.com/sites/MLA/search?seller_id=" + sellerId;

        // Usamos exchange para poder mandar los headers
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                urlBusqueda,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> response = responseEntity.getBody();
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        for (Map<String, Object> item : results) {
            String idMeLi = (String) item.get("id");

            if (productoRepository.findByMercadoLibreId(idMeLi) == null) {
                Producto p = new Producto();
                p.setMercadoLibreId(idMeLi);
                p.setNombre((String) item.get("title"));

                Double price = Double.valueOf(item.get("price").toString());
                p.setPrecio(BigDecimal.valueOf(price));

                // Imagen HD
                String img = (String) item.get("thumbnail");
                p.setImagenUrl(img.replace("-I.jpg", "-O.jpg"));

                p.setActivo(true);
                p.setCategoria("joyas"); // Categoría por defecto

                productoRepository.save(p);
            }
        }
    }
}