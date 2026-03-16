package com.dinastiaarg.api.controller;

import com.dinastiaarg.api.model.Producto;
import com.dinastiaarg.api.repository.ProductoRepository;
import com.dinastiaarg.api.service.MercadoLibreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

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

    @Autowired
    private MercadoLibreService mercadoLibreService;

    @GetMapping("/importar/{sellerId}") // Cambiado a GetMapping
    public String importar(@PathVariable String sellerId) {
        try {
            mercadoLibreService.importarProductos(sellerId);
            return "Sincronización exitosa";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

