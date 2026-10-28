
package com.epicentroGourmet.services.implementation;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.epicentroGourmet.entities.Empleado;
import com.epicentroGourmet.entities.Festival;
import com.epicentroGourmet.entities.Cocinero;
import com.epicentroGourmet.entities.Cajero;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ReciboSueldoPdfService {

    private final EmpleadoService empleadoService;

    public ReciboSueldoPdfService(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    public byte[] generarRecibo(Empleado empleado, Festival festival) {

        double sueldo = empleadoService.calcularSueldoPorFestival(
                empleado, festival);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            Document documento = new Document();
            PdfWriter.getInstance(documento, salida);

            documento.open();

            documento.add(new Paragraph("EPICENTRO GOURMET"));
            documento.add(new Paragraph("RECIBO DE SUELDO"));
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "Empleado: " + empleado.getNombre() + " "
                    + empleado.getApellido()));

            documento.add(new Paragraph(
                    "DNI: " + empleado.getDni()));

            String categoria = "Empleado";

            if (empleado instanceof Cocinero cocinero) {
                categoria = cocinero.getEspecialidad().toString();
            } else if (empleado instanceof Cajero) {
                categoria = "Cajero";
            }

            documento.add(new Paragraph("Categoria: " + categoria));

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Festival: " + festival.getNombre()));

            documento.add(new Paragraph(
                    "Fecha de inicio: "
                    + festival.getFechaInicio().format(formato)));

            documento.add(new Paragraph(
                    "Fecha de fin: "
                    + festival.getFechaFin().format(formato)));

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(
                    String.format(java.util.Locale.forLanguageTag("es-AR"),
                            "Sueldo calculado: $ %,.2f", sueldo)));

            documento.close();

            return salida.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo generar el recibo PDF", e);
        }
    }
}
