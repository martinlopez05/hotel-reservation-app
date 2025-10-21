const API_BASE = "http://localhost:"; // ajusta a tu backend

// Cargar hoteles al iniciar
document.addEventListener("DOMContentLoaded", () => {
  cargarHoteles();
  document.getElementById("form-reserva").addEventListener("submit", crearReserva);
});

// Traer hoteles de la API
async function cargarHoteles() {
  try {
    const res = await fetch(`${API_BASE}/hoteles`);
    const hoteles = await res.json();

    const lista = document.getElementById("hoteles-lista");
    const select = document.getElementById("hotel");

    lista.innerHTML = "";
    select.innerHTML = "<option value=''>Seleccione un hotel</option>";

    hoteles.forEach(hotel => {
      // tarjetas en pantalla
      const card = document.createElement("div");
      card.classList.add("card");
      card.innerHTML = `<h3>${hotel.nombre}</h3><p>${hotel.ubicacion}</p>`;
      card.addEventListener("click", () => cargarHabitaciones(hotel.id));
      lista.appendChild(card);

      // opción en select
      const opt = document.createElement("option");
      opt.value = hotel.id;
      opt.textContent = hotel.nombre;
      select.appendChild(opt);
    });

    select.addEventListener("change", e => cargarHabitaciones(e.target.value));
  } catch (err) {
    console.error("Error cargando hoteles", err);
  }
}

// Traer habitaciones de un hotel
async function cargarHabitaciones(hotelId) {
  if (!hotelId) return;
  try {
    const res = await fetch(`${API_BASE}/hoteles/${hotelId}/habitaciones`);
    const habitaciones = await res.json();

    const lista = document.getElementById("habitaciones-lista");
    const select = document.getElementById("habitacion");

    lista.innerHTML = "";
    select.innerHTML = "<option value=''>Seleccione una habitación</option>";

    habitaciones.forEach(h => {
      // tarjetas en pantalla
      const card = document.createElement("div");
      card.classList.add("card");
      card.innerHTML = `<h3>Habitación ${h.numero}</h3><p>Tipo: ${h.tipo}</p><p>Precio: $${h.precio}</p>`;
      lista.appendChild(card);

      // opción en select
      const opt = document.createElement("option");
      opt.value = h.id;
      opt.textContent = `Habitación ${h.numero} - $${h.precio}`;
      select.appendChild(opt);
    });
  } catch (err) {
    console.error("Error cargando habitaciones", err);
  }
}

// Crear reserva
async function crearReserva(e) {
  e.preventDefault();

  const reserva = {
    hotelId: document.getElementById("hotel").value,
    habitacionId: document.getElementById("habitacion").value,
    usuarioId: document.getElementById("usuario").value,
    fechaInicio: document.getElementById("fechaInicio").value,
    fechaFin: document.getElementById("fechaFin").value,
  };

  try {
    const res = await fetch(`${API_BASE}/reservas`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reserva),
    });

    if (res.ok) {
      alert("Reserva creada con éxito ✅");
      document.getElementById("form-reserva").reset();
    } else {
      alert("Error al crear la reserva ❌");
    }
  } catch (err) {
    console.error("Error creando reserva", err);
  }
}
