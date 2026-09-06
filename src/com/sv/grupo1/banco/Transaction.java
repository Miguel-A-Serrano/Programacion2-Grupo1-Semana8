package com.sv.grupo1.banco;

import java.util.concurrent.Callable;

/**
 * Clase abstracta que define la estructura polimórfica de una transacción concurrente.
 */
public abstract class   Transaction implements Callable<Receipt> {
    protected final String idTransaccion;
    protected final double monto;

    public Transaction(String idTransaccion, double monto) {
        this.idTransaccion = idTransaccion;
        this.monto = monto;
    }

    @Override
    public abstract Receipt call() throws Exception;

    public String getidTransaccion (){
        return this.idTransaccion;
    }
}