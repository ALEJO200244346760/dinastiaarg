import React, { useState } from 'react';
import MercadoPagoCheckout from './MercadoPago';

export default function ProductCard({ producto }) {
  const [showOptions, setShowOptions] = useState(false);
  const [showCardForm, setShowCardForm] = useState(false);

  // Opción 1: Redirigir a Mercado Pago (Checkout Pro)
  const handleMercadoPagoDirecto = async () => {
    try {
      const response = await fetch("https://dinastiaarg-production.up.railway.app/api/pagos/crear-preferencia", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          titulo: producto.nombre,
          precio: producto.precio,
          cantidad: 1
        }),
      });
      const url = await response.text();
      window.location.href = url;
    } catch (e) { alert("Error al conectar"); }
  };

  return (
    <div className="group relative bg-white border border-gray-100 flex flex-col">
      {/* IMAGEN Y TEXTO IGUAL QUE ANTES... */}
      <div className="aspect-[3/4] overflow-hidden bg-gray-50">
        <img className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105" src={producto.imagenUrl} alt={producto.nombre} />
      </div>
      
      <div className="p-6 text-center">
        <h3 className="text-xs tracking-widest uppercase font-light">{producto.nombre}</h3>
        <p className="mt-2 text-sm font-medium">${producto.precio.toLocaleString('es-AR')}</p>

        {!showOptions ? (
          <button 
            onClick={() => setShowOptions(true)}
            className="mt-6 w-full py-3 bg-black text-white text-[10px] tracking-[0.2em] font-bold uppercase hover:bg-gray-800 transition-all"
          >
            ADQUIRIR PIEZA
          </button>
        ) : (
          <div className="mt-4 grid grid-cols-1 gap-2 animate-fade-in">
            <button 
              onClick={() => setShowCardForm(true)}
              className="py-2 border border-black text-[9px] tracking-widest font-bold uppercase hover:bg-black hover:text-white transition-all"
            >
              Tarjeta Débito / Crédito
            </button>
            <button 
              onClick={handleMercadoPagoDirecto}
              className="py-2 bg-blue-600 text-white text-[9px] tracking-widest font-bold uppercase hover:bg-blue-700"
            >
              Mercado Pago App
            </button>
            <button onClick={() => setShowOptions(false)} className="text-[8px] text-gray-400 uppercase mt-2">Cancelar</button>
          </div>
        )}
      </div>

      {/* Si elige tarjeta, se abre el modal lateral */}
      {showCardForm && (
        <MercadoPagoCheckout 
          producto={producto} 
          onCancel={() => setShowCardForm(false)} 
        />
      )}
    </div>
  );
}