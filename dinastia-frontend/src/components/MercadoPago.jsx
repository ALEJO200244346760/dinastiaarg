import React from 'react';
import { initMercadoPago, CardPayment } from '@mercadopago/sdk-react';

// Public Key de Producción
initMercadoPago('APP_USR-c52e873f-116e-4a74-9215-fde999bddba5');

export default function MercadoPagoCheckout({ producto, onCancel }) {
  
  const initialization = {
    amount: producto.precio,
  };

  const onSubmit = async (formData) => {
    return new Promise((resolve, reject) => {
      fetch("https://dinastiaarg-production.up.railway.app/api/pagos/procesar-pago", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          token: formData.token,
          issuer_id: formData.issuer_id,
          payment_method_id: formData.payment_method_id,
          transaction_amount: formData.transaction_amount,
          installments: formData.installments,
          description: `Compra: ${producto.nombre}`,
          payer_email: formData.payer.email,
        }),
      })
      .then((response) => response.json())
      .then((data) => {
        // Asumiendo que el backend devuelve un objeto con { status: '...' }
        if (data.status === "approved" || data.status === "authorized") {
          window.location.href = "/?status=approved";
          resolve();
        } else {
          alert("Estado del pago: " + data.status);
          reject();
        }
      })
      .catch((error) => {
        console.error("Error procesando pago:", error);
        alert("Hubo un problema al procesar el pago. Intente nuevamente.");
        reject();
      });
    });
  };

  return (
    <div className="fixed inset-0 z-[100] flex justify-end bg-black/60 backdrop-blur-sm transition-opacity">
      {/* Panel Lateral Blanco */}
      <div className="h-full w-full max-w-md bg-white shadow-2xl p-8 overflow-y-auto animate-in slide-in-from-right duration-500">
        <header className="mb-10">
          <button 
            onClick={onCancel} 
            className="text-[10px] tracking-[0.3em] uppercase text-gray-400 hover:text-black transition-colors"
          >
            ← Volver a la galería
          </button>
        </header>

        <section className="mb-12">
          <h2 className="text-xl font-extralight tracking-[0.2em] uppercase text-gray-900">
            Finalizar Compra
          </h2>
          <div className="h-[1px] w-12 bg-black mt-4 mb-6"></div>
          
          <div className="flex items-center gap-4 bg-gray-50 p-4">
             <img src={producto.imagenUrl} className="w-16 h-20 object-cover" alt="miniatura" />
             <div>
               <p className="text-[10px] uppercase tracking-widest text-gray-500">{producto.nombre}</p>
               <p className="text-sm font-bold mt-1">${producto.precio.toLocaleString('es-AR')}</p>
             </div>
          </div>
        </section>

        {/* Formulario de Mercado Pago */}
        <div id="cardPaymentBrick_container">
          <CardPayment
            initialization={initialization}
            onSubmit={onSubmit}
            onReady={() => console.log("Formulario de tarjeta listo")}
            customization={{
              visual: {
                style: {
                  theme: 'flat', // Tema más limpio y moderno
                }
              }
            }}
          />
        </div>
      </div>
    </div>
  );
}