import { useEffect, useState } from 'react'
import { getProductos } from './services/api'
import ProductCard from './components/ProductCard'
import Footer from './components/Footer'

function App() {
  const [productos, setProductos] = useState([])

  useEffect(() => {
    getProductos().then(data => setProductos(data))
  }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Mensaje de éxito si el pago fue aprobado */}
      {status === 'approved' && (
        <div className="bg-green-600 text-white text-center py-4 font-bold animate-bounce">
          ¡Gracias por tu compra en Dinastía Arg! Nos pondremos en contacto con vos.
        </div>
      )}
      <header className="py-10 bg-white border-b shadow-sm">
        <h1 className="text-4xl font-serif text-center font-bold tracking-widest text-black">
          DINASTÍA ARG
        </h1>
        <p className="text-center text-gray-500 italic mt-2">Bolsas, Joyas & Accesorios</p>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-12">
        <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-12 p-10">
          {productos.map(p => <ProductCard key={p.id} producto={p} />)}
        </div>
      </main>
      <Footer />
    </div>
  )
}

export default App