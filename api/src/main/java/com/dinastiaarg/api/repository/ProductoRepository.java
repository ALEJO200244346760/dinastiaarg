package com.dinastiaarg.api.repository;

import com.dinastiaarg.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Para filtrar por tipo de accesorio en el frontend
    List<Producto> findByCategoria(String categoria);

    // Para buscar si ya importamos un producto de MeLi
    Producto findByMercadoLibreId(String mercadoLibreId);
}