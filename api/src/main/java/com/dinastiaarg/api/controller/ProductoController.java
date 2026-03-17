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

    @GetMapping("/sincronizar-todo")
    public ResponseEntity<String> sincronizarTodo() {
        try {
            mercadoLibreService.importarTodoElStock("1297120798");
            return ResponseEntity.ok("Sincronización exitosa.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

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
            return ResponseEntity.ok("Guardado.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}