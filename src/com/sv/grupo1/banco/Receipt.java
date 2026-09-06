package com.sv.grupo1.banco;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa el comprobante digital resultante de una transacción bancaria.
 */
public class Receipt implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String idTransaccion;
    private final String tipoOperacion;
    private final String estado;
    private final String detalle;
    private final LocalDateTime fechaHora;

    public Receipt(String idTransaccion, String tipoOperacion, String estado, String detalle) {
        this.idTransaccion = idTransaccion;
        this.tipoOperacion = tipoOperacion;
        this.estado = estado;
        this.detalle = detalle;
        this.fechaHora = LocalDateTime.now();
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public String getEstado() {
        return estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isExitoso() {
        return "EXITO".equalsIgnoreCase(estado);
    }

    public boolean isRechazado() {
        return estado != null && (estado.contains("RECHAZAD") || estado.contains("FALLO"));
    }

    public String getFormatoResumido() {
        return String.format("%s (%s): %s - %s", idTransaccion, tipoOperacion, estado, detalle);
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format("[%s] | %s | %s | %s | %s",
                fechaHora.format(formato), idTransaccion, tipoOperacion, estado, detalle);
    }
}