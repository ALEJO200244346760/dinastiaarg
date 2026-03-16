package com.dinastiaarg.api.service;

import com.dinastiaarg.api.model.Producto;
import com.dinastiaarg.api.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void importarProductos(String sellerId) {
        // 1. Buscamos los IDs de las publicaciones del vendedor
        String urlBusqueda = "https://api.mercadolibre.com/sites/MLA/search?seller_id=" + sellerId;
        Map<String, Object> response = restTemplate.getForObject(urlBusqueda, Map.class);

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        for (Map<String, Object> item : results) {
            String idMeLi = (String) item.get("id");

            // Si el producto no existe en nuestra DB, lo creamos
            if (productoRepository.findByMercadoLibreId(idMeLi) == null) {
                Producto p = new Producto();
                p.setMercadoLibreId(idMeLi);
                p.setNombre((String) item.get("title"));

                // El precio viene como Double, lo pasamos a BigDecimal
                Double price = Double.valueOf(item.get("price").toString());
                p.setPrecio(BigDecimal.valueOf(price));

                p.setImagenUrl((String) item.get("thumbnail"));
                p.setActivo(true);

                // Por defecto los ponemos en una categoría, después se puede editar
                p.setCategoria("general");

                productoRepository.save(p);
            }
        }
    }
}