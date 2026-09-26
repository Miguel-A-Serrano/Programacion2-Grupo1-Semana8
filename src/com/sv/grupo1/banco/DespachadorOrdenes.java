package com.sv.grupo1.banco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Encargado de despachar (enviar a procesar) un lote de órdenes/transacciones
 * al banco, y recolectar los comprobantes resultantes.
 */
public class DespachadorOrdenes {

    private final Bank banco;
    private final List<Future<Receipt>> ordenesEnCurso = new ArrayList<>();

    public DespachadorOrdenes(Bank banco) {
        this.banco = banco;
    }

    /** Despacha una orden (transacción) al banco para su procesamiento concurrente. */
    public void despachar(Transaction orden) {
        Future<Receipt> futuro = banco.procesarTransaccion(orden);
        ordenesEnCurso.add(futuro);
    }

    /** Espera a que todas las órdenes despachadas terminen y devuelve sus comprobantes. */
    public List<Receipt> recolectarComprobantes() {
        List<Receipt> comprobantes = new ArrayList<>();
        for (Future<Receipt> futuro : ordenesEnCurso) {
            try {
                comprobantes.add(futuro.get());
            } catch (Exception e) {
                System.err.println("Error despachando orden: " + e.getMessage());
            }
        }
        return comprobantes;
    }
}