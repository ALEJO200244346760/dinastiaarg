import { useState } from 'react';

export default function Footer() {
  const [loading, setLoading] = useState(false);

  const handleSync = async () => {
    if (!confirm("¿Actualizar catálogo de Dinastía Arg?")) return;
    setLoading(true);

    // 1. Recuperamos el token de seguridad
    const token = localStorage.getItem('token'); 

    try {
        const res = await fetch("https://dinastiaarg-production.up.railway.app/api/productos/sincronizar-todo", {
            method: "GET", // O POST, según cómo lo tengas en el Controller
            headers: {
                "Content-Type": "application/json",
                // 2. Agregamos la autorización para que el backend nos deje pasar
                "Authorization": `Bearer ${token}` 
            }
        });
        
        if (res.status === 401 || res.status === 403) {
            throw new Error("Sesión expirada. Por favor, volvé a iniciar sesión.");
        }

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText);
        }

        alert("¡Sincronización Exitosa!");
        window.location.reload();

    } catch (error) {
        console.error(error);
        alert(error.message || "Error en la sincronización.");
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