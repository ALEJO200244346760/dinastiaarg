import { useState } from 'react';

export default function Footer() {
  const [loading, setLoading] = useState(false);

  const handleSyncPro = async () => {
  const itemId = "MLA1706534491"; // Después podés poner un input para que ella pegue el ID
  
  try {
    // 1. Pedir a Mercado Libre (esto no falla porque lo hace tu navegador)
    const resMeli = await fetch(`https://api.mercadolibre.com/items/${itemId}`);
    const dataMeli = await resMeli.json();

    if(dataMeli.error) throw new Error("No se encontró el producto en MeLi");

    // 2. Preparar el paquete para nuestro backend
    const productoParaGuardar = {
      id: dataMeli.id,
      title: dataMeli.title,
      price: dataMeli.price,
      urlImagen: dataMeli.pictures[0]?.url || dataMeli.thumbnail
    };

    // 3. Mandarlo a Railway para que lo guarde en MySQL
    const resBack = await fetch("https://dinastiaarg-production.up.railway.app/api/productos/guardar-desde-meli", {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(productoParaGuardar)
    });

    const msj = await resBack.text();
    alert(msj);
    window.location.reload();

  } catch (error) {
    console.error(error);
    alert("Error al sincronizar: " + error.message);
  }
    };

  return (
    <footer className="mt-20 border-t border-gray-200 bg-white py-10">
      <div className="text-center">
        <p className="text-gray-400 text-sm">© 2026 DINASTÍA ARG - Joyería & Accesorios</p>
        
        {/* El botón "secreto" para tu mamá */}
        <button 
          onClick={handleSyncPro}
          disabled={loading}
          className="mt-6 text-[10px] uppercase tracking-widest text-gray-300 hover:text-black transition-colors"
        >
          {loading ? "Sincronizando..." : "Sincronizar Stock"}
        </button>
      </div>
    </footer>
  );
}