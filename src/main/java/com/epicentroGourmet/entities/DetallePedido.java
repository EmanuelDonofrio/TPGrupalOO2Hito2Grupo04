package com.epicentroGourmet.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Getter @Setter @NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetallePedido;

    //Bidireccionalidad
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idPedido", nullable = false)
    private Pedido pedido;

    //Un plato puede estar en muchos detalles pedidos y un detalle pedido tiene un unico plato
    //Unidireccional
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idPlato", nullable = false)
    private Plato plato;

    private Integer cantidad;

    private double subtotal;

    public DetallePedido( Pedido pedido, Plato plato, Integer cantidad, double subtotal) {
        this.pedido = pedido;
        this.plato = plato;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }
}
