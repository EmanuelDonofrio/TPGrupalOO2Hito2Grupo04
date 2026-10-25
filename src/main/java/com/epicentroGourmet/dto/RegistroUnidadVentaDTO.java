package com.epicentroGourmet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RegistroUnidadVentaDTO {
    // --- Unidad de Venta ---
    @NotBlank(message = "El nombre comercial es obligatorio")
    private String nombreComercial;

    @NotNull(message = "La superficie es obligatoria")
    @Positive(message = "La superficie debe ser mayor a 0")
    private Double superficie;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 10, max = 10, message="El código debe tener exactamente 10 caracteres")
    private String codigo;

    @NotBlank( message = "Debe seleccionar un tipo de unidad")
    private String tipoUnidad; //ya sea FOOD_TRUCK o PUESTO_DESARMABLE

    // Especifico Food Truck
    private String patente;
    private Boolean conexionElectrica;

    // Especifico PuestoDesarmable;
    private Integer cantCarpas;
    private Integer tiempoMontajeMin;

    // --- Responsable (Staff / Empleado) ---
    @NotBlank(message = "El nombre del  responsable es obligatorio")
    private String nombreResponsable;

    @NotBlank (message = "El apellido del responsable es obligatorio")
    private String apellidoResponsable;

    @NotNull (message = "El DNI es obligatorio")
    private Long dniResponsable;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    // --- Credenciales obligatorias del Usuario ---
    @NotBlank(message =  "El nombre de usuario es obligatorio")
    private String username;


    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;




}
