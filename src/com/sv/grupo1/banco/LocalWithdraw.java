package com.sv.grupo1.banco;

public class LocalWithdraw extends Transaction {
    private final BankAccount cuentaOrigen;

    public LocalWithdraw(String idTransaccion, BankAccount cuentaOrigen, double monto) {
        super(idTransaccion, monto);
        this.cuentaOrigen = cuentaOrigen;
    }

    @Override
    public Receipt call() {
        System.out.println("-> [Hilo " + Thread.currentThread().threadId() + "] Retiro local en " + cuentaOrigen.getNumeroCuenta());
        synchronized (cuentaOrigen) {
            boolean exito = cuentaOrigen.retirar(monto);
            if (exito) {
                return new Receipt(idTransaccion, "RETIRO_LOCAL", "EXITO", "Monto debitado: $" + monto + " de " + cuentaOrigen.getNumeroCuenta());
            } else {
                return new Receipt(idTransaccion, "RETIRO_LOCAL", "RECHAZADO", "Fondos insuficientes en " + cuentaOrigen.getNumeroCuenta());
            }
        }
    }
}