
package com.epicentroGourmet.services.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.CierreCaja;
import com.epicentroGourmet.entities.DetallePedido;
import com.epicentroGourmet.entities.Pedido;
import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.User;

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
    private final CierreCajaService cierreCajaService;

    public PedidoService(
            IPedidoRepository pedidoRepository,
            IDetallePedidoRepository detalleRepository,
            IPlatoRepository platoRepository,
            IUserRepository userRepository,
            CierreCajaService cierreCajaService) {

        this.pedidoRepository = pedidoRepository;
        this.detalleRepository = detalleRepository;
        this.platoRepository = platoRepository;
        this.userRepository = userRepository;
        this.cierreCajaService = cierreCajaService;
    }

    // CREAR PEDIDO
    @Transactional
    public Pedido crearPedido(
            String username,
            List<Long> idsPlatos,
            List<Integer> cantidades,
            String observaciones) {

        // Verificar que el usuario sea cajero
        User usuario = userRepository.findByUsername(username);

        if (usuario == null ||
                !(usuario.getEmpleado() instanceof Cajero)) {

            throw new IllegalArgumentException(
                    "Solo los cajeros pueden crear pedidos");
        }

        Cajero cajero = (Cajero) usuario.getEmpleado();

        // Obtener la unidad de venta del cajero
        UnidadVenta unidad = cajero.getUnidadVentaACargo();

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "El cajero no tiene una unidad de venta asignada");
        }

        // Verificar que exista una caja abierta
        CierreCaja caja = cierreCajaService.obtenerCajaAbierta(unidad);

        // Validar las listas de platos y cantidades
        if (idsPlatos == null ||
                cantidades == null ||
                idsPlatos.isEmpty() ||
                idsPlatos.size() != cantidades.size()) {

            throw new IllegalArgumentException(
                    "El pedido debe contener platos y cantidades válidas");
        }

        // Validar las observaciones
        String observacionesLimpias =
                observaciones == null ? null : observaciones.trim();

        if (observacionesLimpias != null &&
                observacionesLimpias.length() > 500) {

            throw new IllegalArgumentException(
                    "Las observaciones no pueden superar los 500 caracteres");
        }

        if (observacionesLimpias != null &&
                observacionesLimpias.isEmpty()) {

            observacionesLimpias = null;
        }

        // Filtrar platos con cantidad mayor a cero
        List<Long> idsSeleccionados = new ArrayList<>();
        List<Integer> cantidadesSeleccionadas = new ArrayList<>();

        for (int i = 0; i < idsPlatos.size(); i++) {

            Long idPlato = idsPlatos.get(i);
            Integer cantidad = cantidades.get(i);

            if (idPlato == null ||
                    cantidad == null ||
                    cantidad < 0) {

                throw new IllegalArgumentException(
                        "Los platos y cantidades deben ser válidos");
            }

            if (cantidad > 0) {
                idsSeleccionados.add(idPlato);
                cantidadesSeleccionadas.add(cantidad);
            }
        }

        // Verificar que haya al menos un plato seleccionado
        if (idsSeleccionados.isEmpty()) {
            throw new IllegalArgumentException(
                    "Seleccioná al menos un plato");
        }

        // Obtener el menú de la unidad de venta
        List<Plato> menu = platoRepository.findByUnidadVenta(unidad);

        // Crear el pedido
        Pedido pedido = new Pedido(unidad, 0);

        double total = 0;

        // Calcular el total del pedido
        for (int i = 0; i < idsSeleccionados.size(); i++) {

            Long idPlato = idsSeleccionados.get(i);
            Integer cantidad = cantidadesSeleccionadas.get(i);

            Plato plato = menu.stream()
                    .filter(p -> p.getIdPlato().equals(idPlato))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El plato no pertenece al menú de la unidad"));

            // Validar el precio
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

        // Asignar los datos del pedido antes de guardarlo
        pedido.setTotal(total);
        pedido.setCierreCaja(caja);
        pedido.setObservaciones(observacionesLimpias);

        // Guardar el pedido en la base de datos
        pedido = pedidoRepository.save(pedido);

        // Crear y guardar los detalles del pedido
        for (int i = 0; i < idsSeleccionados.size(); i++) {

            Long idPlato = idsSeleccionados.get(i);
            Integer cantidad = cantidadesSeleccionadas.get(i);

            Plato plato = menu.stream()
                    .filter(p -> p.getIdPlato().equals(idPlato))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El plato no pertenece al menú de la unidad"));

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

    // CONSULTAR HISTORIAL DE PEDIDOS
    @Transactional(readOnly = true)
    public List<Pedido> obtenerHistorialPedidos(String username) {

        User usuario = userRepository.findByUsername(username);

        if (usuario == null ||
                !(usuario.getEmpleado() instanceof Cajero)) {

            throw new IllegalArgumentException(
                    "Solo los cajeros pueden consultar el historial");
        }

        Cajero cajero = (Cajero) usuario.getEmpleado();

        UnidadVenta unidad = cajero.getUnidadVentaACargo();

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "El cajero no tiene una unidad de venta asignada");
        }

        return pedidoRepository.obtenerHistorialConDetalles(unidad);
    }
}
