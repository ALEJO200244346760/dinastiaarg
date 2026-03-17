import { useState } from 'react';

export default function Footer() {
  const [loading, setLoading] = useState(false);

  const handleSync = async () => {
    if (!confirm("¿Actualizar catálogo?")) return;
    setLoading(true);
    try {
        const res = await fetch("https://dinastiaarg-production.up.railway.app/api/productos/sincronizar-todo");
        const msg = await res.text();
        alert(msg);
        window.location.reload();
    } catch (error) {
        alert("El servidor está ocupado o bloqueado. Reintentá en un minuto.");
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