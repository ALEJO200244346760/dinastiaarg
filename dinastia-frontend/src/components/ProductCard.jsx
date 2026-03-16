export default function ProductCard({ producto }) {
  
  const handleComprar = async () => {
    const urlPago = await iniciarPago(producto);
    if (urlPago.startsWith("http")) {
      window.location.href = urlPago; // Redirige a Mercado Pago
    } else {
      alert("Hubo un error al generar el pago");
    }
  };

  return (
    <div className="max-w-sm bg-white border border-gray-200 rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300">
      <img 
        className="w-full h-64 object-cover" 
        src={producto.imagenUrl} 
        alt={producto.nombre} 
      />
      <div className="p-5 text-center">
        <h5 className="mb-2 text-xl font-bold tracking-tight text-gray-900 uppercase">
          {producto.nombre}
        </h5>
        <p className="mb-3 font-normal text-gray-600">
          {producto.descripcion}
        </p>
        <p className="text-2xl font-extrabold text-negro-premium mb-4">
          ${producto.precio.toLocaleString('es-AR')}
        </p>
        <button
          onClick={handleComprar}
          className="w-full inline-flex justify-center items-center px-6 py-3 text-sm font-bold text-white bg-black rounded-full hover:bg-gray-800 transition-colors focus:ring-4 focus:outline-none focus:ring-gray-300"
        >
          COMPRAR AHORA
          <svg className="w-3.5 h-3.5 ml-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M1 5h12m0 0L9 1m4 4L9 9"/>
          </svg>
        </button>
      </div>
    </div>
  );
}