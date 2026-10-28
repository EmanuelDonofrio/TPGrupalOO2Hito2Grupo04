package com.epicentroGourmet.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentaDTO {

    private String nombreFestival;
    private BigDecimal recaudacionTotal;
}
