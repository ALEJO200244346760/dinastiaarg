import React, { useState } from 'react';
import MercadoPagoCheckout from './MercadoPago';

export default function ProductCard({ producto }) {
  const [showOptions, setShowOptions] = useState(false);
  const [showCardForm, setShowCardForm] = useState(false);

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
    } catch (e) {
      alert("Error al conectar con la pasarela");
    }
  };

  return (
    <div className="group relative bg-white flex flex-col h-full border border-gray-100 hover:shadow-2xl transition-all duration-500">
      {/* IMAGEN: Zoom suave al pasar el mouse */}
      <div className="aspect-[3/4] overflow-hidden bg-gray-50">
        <img 
          className="w-full h-full object-cover transition-transform duration-[1.5s] group-hover:scale-110" 
          src={producto.imagenUrl} 
          alt={producto.nombre} 
        />
      </div>
      
      {/* INFO DEL PRODUCTO */}
      <div className="p-6 flex flex-col items-center text-center flex-grow">
        <span className="text-[9px] tracking-[0.3em] text-gray-400 uppercase mb-2 font-light">
          Dinastía Arg
        </span>
        <h3 className="text-xs tracking-[0.15em] uppercase font-light text-gray-800 min-h-[32px] leading-relaxed">
          {producto.nombre}
        </h3>
        <p className="mt-4 text-base font-medium text-gray-900 tracking-wider">
          ${producto.precio.toLocaleString('es-AR')}
        </p>

        {/* SELECTOR DE PAGO */}
        <div className="mt-6 w-full">
          {!showOptions ? (
            <button 
              onClick={() => setShowOptions(true)}
              className="w-full py-3.5 bg-black text-white text-[10px] tracking-[0.2em] font-bold uppercase hover:bg-orange-600 transition-all duration-500"
            >
              ADQUIRIR PIEZA
            </button>
          ) : (
            <div className="flex flex-col gap-2 animate-in fade-in slide-in-from-bottom-2 duration-500">
              <button 
                onClick={() => setShowCardForm(true)}
                className="group relative py-3 border border-black text-[9px] tracking-[0.2em] font-bold uppercase overflow-hidden"
              >
                <span className="relative z-10 group-hover:text-white transition-colors duration-300">
                  Tarjeta Débito / Crédito
                </span>
                <div className="absolute inset-0 bg-black translate-y-full group-hover:translate-y-0 transition-transform duration-300"></div>
              </button>
              
              <button 
                onClick={handleMercadoPagoDirecto}
                className="py-3 bg-[#009EE3] text-white text-[9px] tracking-[0.2em] font-bold uppercase hover:bg-[#007EB5] transition-colors"
              >
                Mercado Pago App
              </button>
              
              <button 
                onClick={() => setShowOptions(false)} 
                className="text-[8px] text-gray-400 uppercase mt-2 tracking-widest hover:text-black transition-colors"
              >
                ← Volver
              </button>
            </div>
          )}
        </div>
      </div>

      {/* MODAL LATERAL DE TARJETA */}
      {showCardForm && (
        <MercadoPagoCheckout 
          producto={producto} 
          onCancel={() => setShowCardForm(false)} 
        />
      )}
    </div>
  );
}