import { useState } from 'react';

export default function Footer() {
  const [loading, setLoading] = useState(false);

  const handleSync = async () => {
    if (!confirm("¿Actualizar catálogo de Dinastía Arg?")) return;
    setLoading(true);

    const sellerId = "1297120798";
    // Tu token de App que empieza con 3616
    const token = "APP_USR-3616307332149511-031614-1239c11e12a3e409f03790faf7d193eb-43712155";

    try {
        // 1. El NAVEGADOR busca los IDs (aquí no hay error 403)
        const respIds = await fetch(`https://api.mercadolibre.com/users/${sellerId}/items/search`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const dataIds = await respIds.json();

        if (!dataIds.results) throw new Error("No se obtuvieron resultados");

        // 2. Por cada ID, el NAVEGADOR busca el detalle y lo manda a Railway
        for (let id of dataIds.results) {
            const respDetalle = await fetch(`https://api.mercadolibre.com/items/${id}`);
            const item = await respDetalle.json();

            const productoData = {
                id: item.id,
                title: item.title,
                price: item.price,
                urlImagen: item.pictures?.[0]?.url || item.thumbnail
            };

            // 3. Enviamos a tu endpoint de Railway (que ya vimos que funciona bien)
            await fetch("https://dinastiaarg-production.up.railway.app/api/productos/guardar-desde-meli", {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(productoData)
            });
        }

        alert("¡Sincronización Exitosa! Los productos ya están en la base de datos.");
        window.location.reload();

    } catch (error) {
        console.error(error);
        alert("Error de conexión con MeLi. Probá de nuevo en unos segundos.");
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