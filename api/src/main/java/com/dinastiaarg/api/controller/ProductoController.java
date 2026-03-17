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
// Usamos originPatterns para que sea compatible con Vercel y Railway sin errores de CORS
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MercadoLibreService mercadoLibreService;

    // Listar todos los productos para el inicio de la web
    @GetMapping
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // Listar por categoría (ej: joyas, accesorios)
    @GetMapping("/categoria/{cat}")
    public List<Producto> listarPorCategoria(@PathVariable String cat) {
        return productoRepository.findByCategoria(cat);
    }

    // Este es el método que llama el botón de tu Footer
    @GetMapping("/sincronizar-todo")
    public ResponseEntity<String> sincronizarTodo() {
        try {
            // ID de vendedor de tu mamá
            mercadoLibreService.importarTodoElStock("1297120798");
            return ResponseEntity.ok("Stock de Dinastía Arg actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error de sincronización: " + e.getMessage());
        }
    }

    // Importar un solo producto manualmente si fuera necesario
    @GetMapping("/importar-item/{itemId}")
    public ResponseEntity<String> importarItem(@PathVariable String itemId) {
        try {
            // Reutilizamos la lógica del service
            mercadoLibreService.importarTodoElStock(itemId);
            return ResponseEntity.ok("Producto " + itemId + " procesado.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Endpoint para guardar datos que vengan directo desde el Front
    @PostMapping("/guardar-desde-meli")
    public ResponseEntity<String> guardarDesdeMeli(@RequestBody Map<String, Object> data) {
        try {
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
            return ResponseEntity.ok("Producto guardado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar: " + e.getMessage());
        }
    }

    // Crear un producto de forma manual (sin Mercado Libre)
    @PostMapping("/nuevo")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        producto.setActivo(true);
        if (producto.getMercadoLibreId() == null) {
            producto.setMercadoLibreId("MANUAL-" + System.currentTimeMillis());
        }
        return ResponseEntity.ok(productoRepository.save(producto));
    }
}