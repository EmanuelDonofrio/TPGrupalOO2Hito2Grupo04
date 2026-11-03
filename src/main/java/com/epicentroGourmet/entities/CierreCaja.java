
package com.epicentroGourmet.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCierreCaja;

    private LocalDateTime fechaCierre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idCajero", nullable = false)
    private Cajero cajero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUnidadVenta", nullable = false)
    private UnidadVenta unidadVenta;

    private int cantidadPedidos;

    private double totalRecaudado;

    @OneToMany(mappedBy = "cierreCaja")
    private Set<Pedido> pedidos = new HashSet<>();
    
    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;

    @Column(name = "abierta", nullable = false)
    private boolean abierta = false;

    public CierreCaja(Cajero cajero, UnidadVenta unidadVenta) {
        this.cajero = cajero;
        this.unidadVenta = unidadVenta;
        this.fechaApertura = LocalDateTime.now();
        this.fechaCierre = null;
        this.abierta = true;
        this.cantidadPedidos = 0;
        this.totalRecaudado = 0;
    }
    

}
