import React from 'react';

export default function ProductCard({ producto }) {
  
  const iniciarPago = async (prod) => {
  try {
    const response = await fetch("https://dinastiaarg-production.up.railway.app/api/pagos/crear-preferencia", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        titulo: prod.nombre,  // Antes quizás decía "nombre", ahora "titulo"
        precio: prod.precio,  // Asegurate que sea el número pelado (ej: 5000)
        cantidad: 1
      }),
    });
    return await response.text(); 
  } catch (error) {
    console.error("Error:", error);
    return null;
  }
};

  const handleComprar = async () => {
    const urlPago = await iniciarPago(producto);
    if (urlPago && urlPago.startsWith("http")) {
      window.location.href = urlPago;
    } else {
      alert("No se pudo conectar con Mercado Pago. Revisá el backend.");
    }
  };

  return (
    <div className="group relative flex flex-col bg-white overflow-hidden transition-all duration-500 hover:shadow-2xl border border-gray-100 rounded-sm">
      {/* IMAGEN: Ahora es más grande y con zoom al pasar el mouse */}
      <div className="aspect-[4/5] overflow-hidden bg-gray-50">
        <img 
          className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" 
          src={producto.imagenUrl} 
          alt={producto.nombre} 
        />
      </div>

      {/* TEXTO: Minimalista y centrado */}
      <div className="p-6 flex flex-col items-center text-center">
        <span className="text-[10px] tracking-[0.3em] text-gray-400 uppercase mb-2">
          Dinastía Arg Colección
        </span>
        <h3 className="text-sm font-light tracking-widest text-gray-800 uppercase min-h-[40px] px-2">
          {producto.nombre}
        </h3>
        <p className="mt-4 text-lg font-medium text-gray-900">
          ${producto.precio.toLocaleString('es-AR')}
        </p>

        {/* BOTÓN: Estilo boutique (finito y elegante) */}
        <button
          onClick={handleComprar}
          className="mt-6 w-full py-3 text-[10px] tracking-[0.2em] font-bold text-white bg-black hover:bg-orange-600 transition-all duration-300 rounded-none uppercase"
        >
          ADQUIRIR PIEZA
        </button>
      </div>
    </div>
  );
}