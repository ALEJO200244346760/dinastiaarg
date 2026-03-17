import { useState } from 'react';

export default function Footer() {
  const [loading, setLoading] = useState(false);

  const handleSync = async () => {
    if (!confirm("¿Actualizar catálogo de Dinastía Arg?")) return;
    setLoading(true);

    try {
        // LLAMADA ÚNICA A TU BACKEND
        // Este endpoint en Java se encarga de todo: buscar IDs y guardar detalles
        const res = await fetch("https://dinastiaarg-production.up.railway.app/api/productos/sincronizar-todo");
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText);
        }

        alert("¡Sincronización Exitosa! Los productos ya están en la base de datos.");
        window.location.reload();

    } catch (error) {
        console.error(error);
        alert("Error: El servidor de MeLi bloqueó la conexión temporalmente.");
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