import React, { useEffect } from 'react';
import { initMercadoPago, CardPayment } from '@mercadopago/sdk-react';

// Tu Public Key de producción
initMercadoPago('APP_USR-c52e873f-116e-4a74-9215-fde999bddba5');

export default function MercadoPagoCheckout({ producto, onCancel }) {
  
  const initialization = {
    amount: producto.precio,
  };

  const onSubmit = async (formData) => {
    // Esto llama al nuevo endpoint /procesar-pago que arreglamos en el backend
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
          description: producto.nombre,
          payer_email: formData.payer.email, // Importante para MP
        }),
      })
      .then((response) => response.json())
      .then((data) => {
        if (data.status === "approved") {
          window.location.href = "/?status=approved";
          resolve();
        } else {
          alert("El pago fue " + data.status);
          reject();
        }
      })
      .catch((error) => {
        alert("Error en el servidor");
        reject();
      });
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-end bg-black/40 backdrop-blur-sm">
      <div className="h-full w-full max-w-md bg-white p-8 shadow-2xl animate-fade-in-right overflow-y-auto">
        <button onClick={onCancel} className="text-[10px] tracking-widest uppercase text-gray-400 hover:text-black mb-10">
          ← Volver a la galería
        </button>
        
        <h2 className="text-xl font-light tracking-[0.2em] uppercase mb-2">Finalizar Compra</h2>
        <p className="text-xs text-gray-500 mb-8">{producto.nombre}</p>

        <CardPayment
          initialization={initialization}
          onSubmit={onSubmit}
          onReady={() => console.log("Brick listo")}
        />
      </div>
    </div>
  );
}