package com.epicentroGourmet.services.implementation;
import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.repositories.IEmpleadoRepository;
import org.springframework.stereotype.Service;

@Service("empleadoService")
public class EmpleadoService {

    private final IEmpleadoRepository empleadoRepository;

    public EmpleadoService(IEmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado obtenerEmpleado(String username) {

        return empleadoRepository.findByUsuarioUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe un empleado asociado al usuario " + username));

    }
}
