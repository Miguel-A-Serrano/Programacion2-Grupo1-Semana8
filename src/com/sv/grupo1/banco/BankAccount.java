package com.sv.grupo1.banco;

/**
 * Entidad que representa una cuenta bancaria con operaciones hilo-seguras.
 */
public class BankAccount {
    private final String numeroCuenta;
    private double saldo;

    public BankAccount(String numeroCuenta, double saldoInicial) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldoInicial;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public synchronized double getSaldo() {
        return saldo;
    }

    public synchronized boolean retirar(double monto) {
        if (monto > 0 && (this.saldo - monto) >= 1.0) {
            this.saldo -= monto;
            return true;
        }
        return false;
    }

    public synchronized boolean depositar(double monto) {
        if (monto > 0) {
            this.saldo += monto;
            return true;
        }
        return false;
    }
}