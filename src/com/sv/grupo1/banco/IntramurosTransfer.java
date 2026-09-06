package com.sv.grupo1.banco;

import java.util.Random;

public class IntramurosTransfer extends Transaction {
    private final BankAccount cuentaOrigen;
    private final BankAccount cuentaDestino;
    private final Random generadorAleatorio = new Random();

    public IntramurosTransfer(String idTransaccion, BankAccount cuentaOrigen, BankAccount cuentaDestino, double monto) {
        super(idTransaccion, monto);
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
    }

    @Override
    public Receipt call() {
        if (cuentaOrigen == null || cuentaDestino == null) {
            return new Receipt(idTransaccion, "TRANSFERENCIA", "RECHAZADA",
                    "Cuenta origen o destino no valida!.");
        }

        if (monto <= 0) {
            return new Receipt(idTransaccion, "TRANSFERENCIA", "RECHAZADA",
                    "Monto invalido para transferencia: $" + monto);
        }

        if (cuentaOrigen.getNumeroCuenta().equals(cuentaDestino.getNumeroCuenta())) {
            return new Receipt(idTransaccion, "TRANSFERENCIA", "RECHAZADA",
                    "La cuenta de origen y destino no pueden ser la misma: " + cuentaOrigen.getNumeroCuenta());
        }

        System.out.println("call() => [Hilo " + Thread.currentThread().threadId() + "] Transferencia de "
                + cuentaOrigen.getNumeroCuenta() + " a " + cuentaDestino.getNumeroCuenta());

        // Prevención de Deadlock mediante ordenamiento de bloqueos
        BankAccount primerBloqueo = cuentaOrigen.getNumeroCuenta().compareTo(cuentaDestino.getNumeroCuenta()) < 0 ? cuentaOrigen : cuentaDestino;
        BankAccount segundoBloqueo = cuentaOrigen.getNumeroCuenta().compareTo(cuentaDestino.getNumeroCuenta()) < 0 ? cuentaDestino : cuentaOrigen;

        synchronized (primerBloqueo) {
            synchronized (segundoBloqueo) {
                if (cuentaOrigen.retirar(monto)) {
                    // Simulación de respuesta de destino (85% éxito, 15% fallo)
                    boolean destinoRespondio = generadorAleatorio.nextDouble() > 0.15;

                    if (destinoRespondio) {
                        cuentaDestino.depositar(monto);
                        return new Receipt(idTransaccion, "TRANSFERENCIA", "EXITO",
                                "Monto transferido: $" + monto + " de " + cuentaOrigen.getNumeroCuenta() + " a " + cuentaDestino.getNumeroCuenta());
                    } else {
                        // Reversión por consistencia de fondos
                        cuentaOrigen.depositar(monto);
                        return new Receipt(idTransaccion, "TRANSFERENCIA", "FALLO_REVERTIDO",
                                "Destino no respondio. Fondos revertidos: $" + monto);
                    }
                } else {
                    return new Receipt(idTransaccion, "TRANSFERENCIA", "RECHAZADA",
                            "Fondos insuficientes en cuenta origen: " + cuentaOrigen.getNumeroCuenta());
                }
            }
        }
    }
}