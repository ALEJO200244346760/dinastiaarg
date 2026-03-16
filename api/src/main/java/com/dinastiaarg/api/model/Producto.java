package com.dinastiaarg.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    private String imagenUrl;

    // Este campo es clave para vincular con lo que ya tiene en MeLi
    private String mercadoLibreId;

    private String categoria; // "joyas", "bolsas", "accesorios"

    private boolean activo = true;
}