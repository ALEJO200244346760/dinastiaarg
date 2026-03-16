const API_URL = "http://localhost:8080/api";

export const getProductos = async () => {
  const res = await fetch(`${API_URL}/productos`);
  return await res.json();
};

export const iniciarPago = async (producto) => {
  const res = await fetch(`${API_URL}/pagos/crear-preferencia`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(producto)
  });
  return await res.text(); // Esto nos devuelve la URL de Mercado Pago
};