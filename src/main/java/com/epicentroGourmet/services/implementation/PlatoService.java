package com.epicentroGourmet.services.implementation;
import com.epicentroGourmet.entities.Plato;
import com.epicentroGourmet.entities.UnidadVenta;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.repositories.IEmpleadoRepository;
import com.epicentroGourmet.repositories.IPlatoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatoService {

    private final IPlatoRepository platoRepository;
    private final IEmpleadoRepository empleadoRepository;

    public PlatoService(IPlatoRepository platoRepository, IEmpleadoRepository empleadoRepository) {
        this.platoRepository = platoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public List<Plato> obtenerPorUnidadVenta(UnidadVenta unidadVenta) {

        if (unidadVenta == null) {
            return List.of();
        }
        return platoRepository.findByUnidadVenta(unidadVenta);
    }

    @Transactional
    public void crear(Plato plato, String usernameResponsable) {
        Empleado responsable = empleadoRepository.findByUsuarioUsername(usernameResponsable)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe un empleado asociado al usuario " + usernameResponsable));

        UnidadVenta unidadVenta = responsable.getUnidadVentaACargo();
        if (unidadVenta == null) {
            throw new IllegalStateException(
                    "El responsable no tiene una unidad de venta asignada.");
        }

        Plato platoGuardado = platoRepository.save(plato);
        unidadVenta.getMenu().add(platoGuardado);
    }

    @Transactional
    public void modificar(Long id, Plato datos, String usernameResponsable) {
        UnidadVenta unidadVenta = obtenerUnidadVentaResponsable(usernameResponsable);
        Plato plato = obtenerPlatoDeUnidad(id, unidadVenta);

        plato.setNombre(datos.getNombre());
        plato.setPrecioVenta(datos.getPrecioVenta());
        plato.setPrecioCosto(datos.getPrecioCosto());
        platoRepository.save(plato);
    }

    @Transactional
    public void eliminar(Long id, String usernameResponsable) {
        UnidadVenta unidadVenta = obtenerUnidadVentaResponsable(usernameResponsable);
        Plato plato = obtenerPlatoDeUnidad(id, unidadVenta);

        unidadVenta.getMenu().removeIf(platoMenu ->
                platoMenu.getIdPlato().equals(plato.getIdPlato()));
        platoRepository.delete(plato);
    }

    private UnidadVenta obtenerUnidadVentaResponsable(String usernameResponsable) {
        Empleado responsable = empleadoRepository.findByUsuarioUsername(usernameResponsable)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe un empleado asociado al usuario " + usernameResponsable));

        if (responsable.getUnidadVentaACargo() == null) {
            throw new IllegalStateException(
                    "El responsable no tiene una unidad de venta asignada.");
        }
        return responsable.getUnidadVentaACargo();
    }

    private Plato obtenerPlatoDeUnidad(Long id, UnidadVenta unidadVenta) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un plato con el ID " + id + "."));

        boolean perteneceAUnidad = unidadVenta.getMenu().stream()
                .anyMatch(platoMenu -> platoMenu.getIdPlato().equals(plato.getIdPlato()));
        if (!perteneceAUnidad) {
            throw new IllegalArgumentException(
                    "El plato no pertenece a la unidad de venta del responsable.");
        }
        return plato;
    }
}
