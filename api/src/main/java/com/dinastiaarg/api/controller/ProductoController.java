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

    @GetMapping("/sincronizar-todo")
    public ResponseEntity<String> sincronizarTodo() {
        try {
            // Llamamos al método nuevo del service
            mercadoLibreService.importarTodoElStock("1297120798");
            return ResponseEntity.ok("Stock de Dinastía Arg actualizado correctamente.");
        } catch (Exception e) {
            // Si sale 403, acá lo vamos a ver en el alert del front
            return ResponseEntity.status(500).body("Error de sincronización: " + e.getMessage());
        }
    }
}