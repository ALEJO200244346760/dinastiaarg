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

        <main className="w-full max-w-[1600px] mx-auto px-4 py-12">
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
            {productos.map(p => <ProductCard key={p.id} producto={p} />)}
          </div>
        </main>
      <Footer />
    </div>
  )
}

export default App