import { useState } from 'react';

export default function AdminPanel() {
  const [mostrar, setMostrar] = useState(false);
  const [form, setForm] = useState({ nombre: '', precio: '', imagenUrl: '', descripcion: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const res = await fetch("https://dinastiaarg-production.up.railway.app/api/productos/nuevo", {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    });

    if (res.ok) {
      alert("¡Joyita cargada con éxito!");
      window.location.reload();
    }
  };

  return (
    <div className="p-8 bg-black text-white">
      <button onClick={() => setMostrar(!mostrar)} className="text-xs border border-white px-4 py-2 rounded-full hover:bg-white hover:text-black transition">
        {mostrar ? "CERRAR PANEL" : "MODO ADMINISTRADORA"}
      </button>

      {mostrar && (
        <form onSubmit={handleSubmit} className="mt-8 max-w-md mx-auto space-y-4">
          <input 
            placeholder="Nombre del accesorio (ej: Pulsera Blanca)" 
            className="w-full p-2 bg-gray-900 border border-gray-700 rounded"
            onChange={e => setForm({...form, nombre: e.target.value})}
            required
          />
          <input 
            placeholder="Precio (solo números)" 
            type="number"
            className="w-full p-2 bg-gray-900 border border-gray-700 rounded"
            onChange={e => setForm({...form, precio: e.target.value})}
            required
          />
          <input 
            placeholder="Link de la imagen (podés sacarlo de MeLi)" 
            className="w-full p-2 bg-gray-900 border border-gray-700 rounded"
            onChange={e => setForm({...form, imagenUrl: e.target.value})}
            required
          />
          <textarea 
            placeholder="Descripción corta" 
            className="w-full p-2 bg-gray-900 border border-gray-700 rounded"
            onChange={e => setForm({...form, descripcion: e.target.value})}
          />
          <button type="submit" className="w-full bg-white text-black py-2 font-bold hover:bg-gray-200">
            PUBLICAR EN LA WEB
          </button>
        </form>
      )}
    </div>
  );
}