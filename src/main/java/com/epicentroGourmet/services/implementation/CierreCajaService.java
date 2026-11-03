package com.epicentroGourmet.services.implementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epicentroGourmet.entities.Cajero;
import com.epicentroGourmet.entities.CierreCaja;
import com.epicentroGourmet.entities.Pedido;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.User;

import com.epicentroGourmet.repositories.ICierreCajaRepository;
import com.epicentroGourmet.repositories.IPedidoRepository;
import com.epicentroGourmet.repositories.IUserRepository;

@Service
public class CierreCajaService {

    private final ICierreCajaRepository cierreCajaRepository;
    private final IPedidoRepository pedidoRepository;
    private final IUserRepository userRepository;

    public CierreCajaService(
            ICierreCajaRepository cierreCajaRepository,
            IPedidoRepository pedidoRepository,
            IUserRepository userRepository) {

        this.cierreCajaRepository = cierreCajaRepository;
        this.pedidoRepository = pedidoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public boolean cajaAbierta(UnidadVenta unidad) {
        return cierreCajaRepository.existeCajaAbierta(unidad);
    }

    @Transactional
    public CierreCaja abrirCaja(String username) {

        Cajero cajero = obtenerCajero(username);
        UnidadVenta unidad = obtenerUnidad(cajero);

        if (cajaAbierta(unidad)) {
            throw new IllegalArgumentException(
                "La caja ya se encuentra abierta"
            );
        }

        CierreCaja caja = new CierreCaja(cajero, unidad);

        return cierreCajaRepository.save(caja);
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosPendientes(String username) {

        Cajero cajero = obtenerCajero(username);
        UnidadVenta unidad = obtenerUnidad(cajero);

        return cierreCajaRepository
            .findByUnidadVentaAndAbiertaTrue(unidad)
            .map(pedidoRepository::findByCierreCaja)
            .orElseGet(List::of);
    }


@Transactional
public CierreCaja cerrarCaja(String username) {

    Cajero cajero = obtenerCajero(username);
    UnidadVenta unidad = obtenerUnidad(cajero);

    CierreCaja caja = cierreCajaRepository
        .findByUnidadVentaAndAbiertaTrue(unidad)
        .orElseThrow(() -> new IllegalArgumentException(
            "No hay una caja abierta para cerrar"
        ));

    List<Pedido> pedidos =
        pedidoRepository.findByCierreCaja(caja);

    double total = pedidos.stream()
        .mapToDouble(Pedido::getTotal)
        .sum();

    caja.setCantidadPedidos(pedidos.size());
    caja.setTotalRecaudado(total);
    caja.setFechaCierre(LocalDateTime.now());
    caja.setAbierta(false);

    return cierreCajaRepository.save(caja);
}


    private Cajero obtenerCajero(String username) {

        User usuario = userRepository.findByUsername(username);

        if (usuario == null ||
            !(usuario.getEmpleado() instanceof Cajero)) {

            throw new IllegalArgumentException(
                "Solo los cajeros pueden realizar esta operación"
            );
        }

        return (Cajero) usuario.getEmpleado();
    }

    private UnidadVenta obtenerUnidad(Cajero cajero) {

        UnidadVenta unidad = cajero.getUnidadVentaACargo();

        if (unidad == null) {
            throw new IllegalArgumentException(
                "El cajero no tiene una unidad de venta asignada"
            );
        }

        return unidad;
    }
    
    @Transactional(readOnly = true)
    public boolean cajaAbiertaParaCajero(String username) {

        Cajero cajero = obtenerCajero(username);
        UnidadVenta unidad = obtenerUnidad(cajero);

        return cajaAbierta(unidad);
    }
    
    @Transactional(readOnly = true)
    public CierreCaja obtenerCajaAbierta(UnidadVenta unidad) {
        return cierreCajaRepository
            .findByUnidadVentaAndAbiertaTrue(unidad)
            .orElseThrow(() -> new IllegalArgumentException(
                "La caja está cerrada. Debés abrirla antes de crear pedidos."
            ));
    }
}

