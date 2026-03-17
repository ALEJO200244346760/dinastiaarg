package com.dinastiaarg.api.controller;


import com.dinastiaarg.api.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/crear-preferencia")
    public String crearPreferencia(@RequestBody Map<String, Object> datos) {
        // Extraemos los datos que vienen del frontend
        String titulo = (String) datos.get("titulo");
        Double precio = Double.parseDouble(datos.get("precio").toString());
        Integer cantidad = (Integer) datos.get("cantidad");

        return pagoService.crearPreferencia(titulo, precio, cantidad);
    }
}