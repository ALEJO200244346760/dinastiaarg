import { useState } from 'react';

export default function Footer() {
  const [loading, setLoading] = useState(false);

  const handleSync = async () => {
    if (!confirm("¿Querés actualizar los productos con lo nuevo de Mercado Libre?")) return;
    
    setLoading(true);
    try {
      // Usamos la URL de tu backend en Railway
      const res = await fetch("https://dinastiaarg-production.up.railway.app/api/productos/importar/1297120798");
      const data = await res.text();
      alert(data);
      window.location.reload(); // Recarga para mostrar los nuevos productos
    } catch (error) {
      alert("Error al sincronizar");
    } finally {
      setLoading(false);
    }
  };

  return (
    <footer className="mt-20 border-t border-gray-200 bg-white py-10">
      <div className="text-center">
        <p className="text-gray-400 text-sm">© 2026 DINASTÍA ARG - Joyería & Accesorios</p>
        
        {/* El botón "secreto" para tu mamá */}
        <button 
          onClick={handleSync}
          disabled={loading}
          className="mt-6 text-[10px] uppercase tracking-widest text-gray-300 hover:text-black transition-colors"
        >
          {loading ? "Sincronizando..." : "Sincronizar Stock"}
        </button>
      </div>
    </footer>
  );
}