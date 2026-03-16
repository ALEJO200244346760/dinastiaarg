import { useEffect, useState } from 'react'
import { getProductos } from './services/api'
import ProductCard from './components/ProductCard'

function App() {
  const [productos, setProductos] = useState([])

  useEffect(() => {
    getProductos().then(data => setProductos(data))
  }, [])

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="py-10 bg-white border-b shadow-sm">
        <h1 className="text-4xl font-serif text-center font-bold tracking-widest text-black">
          DINASTÍA ARG
        </h1>
        <p className="text-center text-gray-500 italic mt-2">Bolsas, Joyas & Accesorios</p>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-12">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
          {productos.map(p => (
            <ProductCard key={p.id} producto={p} />
          ))}
        </div>
      </main>
    </div>
  )
}

export default App