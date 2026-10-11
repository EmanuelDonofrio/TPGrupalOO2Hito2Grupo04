package com.epicentroGourmet.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    //Bidireccionalidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUnidadVenta")
    private UnidadVenta puesto;

    //Un pedido tiene muchos detalles pedidos y un detalle pedido pertenece a un unico pedido
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido")
    private Set<DetallePedido> detallesPedidos = new HashSet<>();

    private double total;

    public Pedido(UnidadVenta puesto, double total) {
        this.puesto = puesto;
        this.total = total;
    }
}
