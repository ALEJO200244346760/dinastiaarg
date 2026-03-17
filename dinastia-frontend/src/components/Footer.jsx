import { useState } from 'react';

export default function Footer() {
  const [loading, setLoading] = useState(false);

  const handleSync = async () => {
    if (!confirm("¿Querés actualizar los productos con lo nuevo de Mercado Libre?")) return;
    
    setLoading(true);
    const sellerId = "1297120798";
    const accessToken = "APP_USR-3616307332149511-031614-1239c11e12a3e409f03790faf7d193eb-43712155";

    try {
      // 1. Buscamos los IDs de los productos desde el navegador (Sin bloqueo)
      const respIds = await fetch(`https://api.mercadolibre.com/users/${sellerId}/items/search`, {
        headers: { 'Authorization': `Bearer ${accessToken}` }
      });
      const dataIds = await respIds.json();

      if (!dataIds.results || dataIds.results.length === 0) {
        alert("No se encontraron productos en Mercado Libre.");
        return;
      }

      // 2. Traemos el detalle de cada uno y lo mandamos a Railway
      for (let id of dataIds.results) {
        const respDetalle = await fetch(`https://api.mercadolibre.com/items/${id}`);
        const item = await respDetalle.json();

        const productoParaEnviar = {
          id: item.id,
          title: item.title,
          price: item.price,
          urlImagen: item.pictures[0]?.url || item.thumbnail
        };

        // 3. Enviamos a tu endpoint de Railway que ya está listo
        await fetch("https://dinastiaarg-production.up.railway.app/api/productos/guardar-desde-meli", {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(productoParaEnviar)
        });
      }

      alert("¡Sincronización exitosa! Las joyas de Dinastía ya están actualizadas.");
      window.location.reload(); 
    } catch (error) {
      console.error(error);
      alert("Error al sincronizar. Revisá la consola.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <footer className="mt-20 border-t border-gray-100 bg-white py-12">
      <div className="max-w-7xl mx-auto px-4 text-center">
        <p className="text-gray-400 text-sm font-light tracking-tighter">
          © 2026 DINASTÍA ARG • PIEZAS ÚNICAS
        </p>
        
        <button 
          onClick={handleSync}
          disabled={loading}
          className="mt-8 text-[9px] uppercase tracking-[0.3em] text-gray-200 hover:text-orange-500 transition-all duration-500"
        >
          {loading ? "PROCESANDO JOYAS..." : "ADMIN SYNC"}
        </button>
      </div>
    </footer>
  );
}