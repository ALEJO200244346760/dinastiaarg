import React, { useState } from 'react';

export default function ProductCard({ producto }) {
  const [isBuying, setIsBuying] = useState(false);

  // Solucionamos el ReferenceError: definimos la función de pago
  const iniciarPago = async (prod) => {
    try {
      const response = await fetch("https://dinastiaarg-production.up.railway.app/api/pagos/crear-preferencia", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          id: prod.id,
          titulo: prod.nombre,
          precio: prod.precio,
          cantidad: 1
        }),
      });

      if (!response.ok) throw new Error("Error al generar preferencia");
      
      // El backend debe devolver el init_point de Mercado Pago
      return await response.text(); 
    } catch (error) {
      console.error("Error en iniciarPago:", error);
      return null;
    }
  };

  const handleComprar = async () => {
    setIsBuying(true);
    const urlPago = await iniciarPago(producto);
    
    if (urlPago && urlPago.startsWith("http")) {
      window.location.href = urlPago;
    } else {
      alert("No se pudo conectar con Mercado Pago. Verificá el backend.");
      setIsBuying(false);
    }
  };

  return (
    <div className="group relative bg-white flex flex-col items-center transition-all duration-700 hover:shadow-2xl">
      {/* CONTENEDOR DE IMAGEN: Proporción de catálogo de moda */}
      <div className="relative w-full aspect-[3/4] overflow-hidden bg-gray-100">
        <img 
          className="w-full h-full object-cover transition-transform duration-[1.5s] ease-out group-hover:scale-110" 
          src={producto.imagenUrl} 
          alt={producto.nombre} 
        />
        {/* Overlay sutil al pasar el mouse */}
        <div className="absolute inset-0 bg-black/5 opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
      </div>

      {/* DETALLES: Tipografía refinada */}
      <div className="w-full p-8 flex flex-col items-center text-center bg-white">
        <p className="text-[10px] tracking-[0.4em] text-gray-400 uppercase mb-3 font-light">
          Dinastía Arg • Heritage
        </p>
        
        <h3 className="text-sm font-light tracking-[0.15em] text-gray-800 uppercase min-h-[40px] leading-relaxed">
          {producto.nombre}
        </h3>
        
        <div className="mt-4 h-[1px] w-8 bg-gray-200" />
        
        <p className="mt-4 text-lg font-light text-gray-900 tracking-wider">
          ${producto.precio.toLocaleString('es-AR')}
        </p>

        {/* BOTÓN: Minimalista y Boutique */}
        <button
          onClick={handleComprar}
          disabled={isBuying}
          className="mt-8 w-full border border-black py-4 text-[10px] tracking-[0.3em] font-bold transition-all duration-500 relative overflow-hidden group/btn"
        >
          <span className="relative z-10 text-black group-hover/btn:text-white transition-colors duration-500 uppercase">
            {isBuying ? "PROCESANDO..." : "ADQUIRIR PIEZA"}
          </span>
          <div className="absolute inset-0 bg-black translate-y-full group-hover/btn:translate-y-0 transition-transform duration-500" />
        </button>
      </div>
    </div>
  );
}