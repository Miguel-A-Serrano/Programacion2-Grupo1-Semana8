package com.sv.grupo1.banco;

public class LocalDeposit extends Transaction {
    private final BankAccount cuentaDestino;

    public LocalDeposit(String idTransaccion, BankAccount cuentaDestino, double monto) {
        super(idTransaccion, monto);
        this.cuentaDestino = cuentaDestino;
    }

    @Override
    public Receipt call() {
        System.out.println("call() => [Hilo " + Thread.currentThread().threadId() + "] Deposito local en " + cuentaDestino.getNumeroCuenta());
        synchronized (cuentaDestino) {
            cuentaDestino.depositar(monto);
            return new Receipt(idTransaccion, "DEPOSITO_LOCAL", "EXITO", "Monto acreditado: $" + monto + " a " + cuentaDestino.getNumeroCuenta());
        }
    }
}
