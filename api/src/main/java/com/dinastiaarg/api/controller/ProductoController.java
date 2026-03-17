package com.dinastiaarg.api.controller;

import com.dinastiaarg.api.model.Producto;
import com.dinastiaarg.api.repository.ProductoRepository;
import com.dinastiaarg.api.service.MercadoLibreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
// Usamos originPatterns para evitar el conflicto con allowCredentials
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MercadoLibreService mercadoLibreService;

    @GetMapping
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @GetMapping("/categoria/{cat}")
    public List<Producto> listarPorCategoria(@PathVariable String cat) {
        return productoRepository.findByCategoria(cat);
    }

    @PostMapping
    public Producto guardar(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    @GetMapping("/importar-item/{itemId}")
    public String importarItem(@PathVariable String itemId) {
        try {
            mercadoLibreService.importarProductoPorId(itemId);
            return "Producto " + itemId + " importado con éxito.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/guardar-desde-meli")
    public ResponseEntity<String> guardarDesdeMeli(@RequestBody Map<String, Object> data) {
        try {
            // Verificamos si ya existe para no duplicar
            String mlId = (String) data.get("id");
            Producto p = productoRepository.findByMercadoLibreId(mlId);

            if (p == null) p = new Producto();

            p.setMercadoLibreId(mlId);
            p.setNombre((String) data.get("title"));
            p.setPrecio(new BigDecimal(data.get("price").toString()));
            p.setImagenUrl((String) data.get("urlImagen"));
            p.setActivo(true);
            p.setCategoria("joyas");

            productoRepository.save(p);
            return ResponseEntity.ok("Producto guardado en la base de datos");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar: " + e.getMessage());
        }
    }

    @PostMapping("/nuevo")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        producto.setActivo(true);
        if (producto.getMercadoLibreId() == null) {
            producto.setMercadoLibreId("MANUAL-" + System.currentTimeMillis());
        }
        return ResponseEntity.ok(productoRepository.save(producto));
    }
    @GetMapping("/sincronizar-todo")
    public String sincronizarTodo() {
        try {
            // El ID de vendedor de tu mamá
            mercadoLibreService.importarProductos("1297120798");
            return "Sincronización masiva iniciada correctamente.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}