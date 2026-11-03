package com.epicentroGourmet.dto;

public class ReporteMayoresCanonDTO {

    private String nombreUnidadVenta;
    private String codigo;
    private String tipoUnidadVenta;
    private Double canon;

    public ReporteMayoresCanonDTO(
            String nombreUnidadVenta,
            String codigo,
            String tipoUnidadVenta,
            Double canon) {

        this.nombreUnidadVenta = nombreUnidadVenta;
        this.codigo = codigo;
        this.tipoUnidadVenta = tipoUnidadVenta;
        this.canon = canon;
    }

    public String getNombreUnidadVenta() {
        return nombreUnidadVenta;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTipoUnidadVenta() {
        return tipoUnidadVenta;
    }

    public Double getCanon() {
        return canon;
    }
}
