package com.epicentroGourmet.services.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.DetallePedido;
import com.epicentroGourmet.entities.Pedido;
import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.User;
import java.util.ArrayList;
import com.epicentroGourmet.repositories.IDetallePedidoRepository;
import com.epicentroGourmet.repositories.IPedidoRepository;
import com.epicentroGourmet.repositories.IPlatoRepository;
import com.epicentroGourmet.repositories.IUserRepository;

@Service
public class PedidoService {

    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detalleRepository;
    private final IPlatoRepository platoRepository;
    private final IUserRepository userRepository;

    public PedidoService(
            IPedidoRepository pedidoRepository,
            IDetallePedidoRepository detalleRepository,
            IPlatoRepository platoRepository,
            IUserRepository userRepository) {

        this.pedidoRepository = pedidoRepository;
        this.detalleRepository = detalleRepository;
        this.platoRepository = platoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Pedido crearPedido(
            String username,
            List<Long> idsPlatos,
            List<Integer> cantidades) {

        User usuario = userRepository.findByUsername(username);

        if (usuario == null ||
                !(usuario.getEmpleado() instanceof Cajero)) {
            throw new IllegalArgumentException(
                    "Solo los cajeros pueden crear pedidos");
        }

        Cajero cajero = (Cajero) usuario.getEmpleado();

        UnidadVenta unidad = cajero.getUnidadVentaACargo();

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "El cajero no tiene una unidad de venta asignada");
        }

        if (idsPlatos == null || cantidades == null ||
                idsPlatos.isEmpty() ||
                idsPlatos.size() != cantidades.size()) {
            throw new IllegalArgumentException(
                    "El pedido debe contener platos y cantidades válidas");
        }

        // Filtrar los platos con cantidad mayor a cero
        List<Long> idsSeleccionados = new ArrayList<>();
        List<Integer> cantidadesSeleccionadas = new ArrayList<>();

        for (int i = 0; i < idsPlatos.size(); i++) {

            Long idPlato = idsPlatos.get(i);
            Integer cantidad = cantidades.get(i);

            if (idPlato == null || cantidad == null || cantidad < 0) {
                throw new IllegalArgumentException(
                        "Los platos y cantidades deben ser válidos");
            }

            if (cantidad > 0) {
                idsSeleccionados.add(idPlato);
                cantidadesSeleccionadas.add(cantidad);
            }
        }

        // Verificar que haya seleccionado al menos un plato
        if (idsSeleccionados.isEmpty()) {
            throw new IllegalArgumentException(
                    "Seleccioná al menos un plato");
        }

        // Reemplazar las listas originales por las filtradas
        idsPlatos = idsSeleccionados;
        cantidades = cantidadesSeleccionadas;

        // Continuar con el método original
        List<Plato> menu =
                platoRepository.findByUnidadVenta(unidad);

        Pedido pedido = new Pedido(unidad, 0);

        double total = 0;

        for (int i = 0; i < idsPlatos.size(); i++) {

            Long idPlato = idsPlatos.get(i);
            Integer cantidad = cantidades.get(i);

            if (idPlato == null || cantidad == null ||
                    cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor a cero");
            }

            Plato plato = menu.stream()
                    .filter(p -> p.getIdPlato().equals(idPlato))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El plato no pertenece al menú de la unidad"));

            if (!Double.isFinite(plato.getPrecioVenta()) ||
                    plato.getPrecioVenta() < 0) {
                throw new IllegalArgumentException(
                        "El plato tiene un precio inválido");
            }

            double subtotal = plato.getPrecioVenta() * cantidad;

            if (!Double.isFinite(subtotal)) {
                throw new IllegalArgumentException(
                        "El subtotal es inválido");
            }

            total += subtotal;
        }

        if (!Double.isFinite(total)) {
            throw new IllegalArgumentException(
                    "El total del pedido es inválido");
        }

        pedido.setTotal(total);
        pedido = pedidoRepository.save(pedido);

        for (int i = 0; i < idsPlatos.size(); i++) {

            Long idPlato = idsPlatos.get(i);
            Integer cantidad = cantidades.get(i);

            Plato plato = menu.stream()
                    .filter(p -> p.getIdPlato().equals(idPlato))
                    .findFirst()
                    .orElseThrow();

            double subtotal = plato.getPrecioVenta() * cantidad;

            DetallePedido detalle = new DetallePedido(
                    pedido,
                    plato,
                    cantidad,
                    subtotal
            );

            detalleRepository.save(detalle);
        }

        return pedido;
    }
}