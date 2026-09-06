package com.sv.grupo1.banco;

public class LocalDeposit extends Transaction {
    private final BankAccount cuentaDestino;

    public LocalDeposit(String idTransaccion, BankAccount cuentaDestino, double monto) {
        super(idTransaccion, monto);
        this.cuentaDestino = cuentaDestino;
    }

    @Override
    public Receipt call() {
        System.out.println("-> [Hilo " + Thread.currentThread().threadId() + "] Depósito local en " + cuentaDestino.getNumeroCuenta());
        synchronized (cuentaDestino) {
            boolean exito = cuentaDestino.depositar(monto);
            if (exito) {
                return new Receipt(idTransaccion, "DEPOSITO_LOCAL", "EXITO", "Monto acreditado: $" + monto + " a " + cuentaDestino.getNumeroCuenta());
            } else {
                return new Receipt(idTransaccion, "DEPOSITO_LOCAL", "RECHAZADO", "Monto inválido: $" + monto);
            }
        }
    }
}
