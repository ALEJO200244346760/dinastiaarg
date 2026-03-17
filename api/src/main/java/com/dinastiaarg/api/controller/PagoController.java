package com.dinastiaarg.api.controller;


import com.dinastiaarg.api.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    // En PagoController.java agregá este nuevo método:

    @PostMapping("/procesar-pago")
    public ResponseEntity<?> procesarPago(@RequestBody Map<String, Object> paymentData) {
        String resultado = pagoService.procesarPago(paymentData);
        if (resultado.equals("error")) {
            return ResponseEntity.status(500).body("Error al procesar el pago");
        }
        return ResponseEntity.ok(Map.of("status", resultado));
    }

}