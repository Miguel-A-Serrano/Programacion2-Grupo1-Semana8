package com.sv.grupo1.banco;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Bank {
    private final String nombreBanco;
    private final Map<String, BankAccount> mapaCuentas = new HashMap<>();
    private final ExecutorService servicioEjecucion = Executors.newVirtualThreadPerTaskExecutor();

    public Bank(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public void registrarCuenta(BankAccount cuenta) {
        mapaCuentas.put(cuenta.getNumeroCuenta(), cuenta);
    }

    public BankAccount obtenerCuenta(String numeroCuenta) {
        return mapaCuentas.get(numeroCuenta);
    }

    public Future<Receipt> procesarTransaccion(Transaction transaccion) {
        return servicioEjecucion.submit(transaccion);
    }

    public void apagarServicio() {
        servicioEjecucion.shutdown();
    }

    public void imprimirAuditoriaSaldos() {
        System.out.println("\n==================================================");
        System.out.println("   AUDITORÍA FINAL DE CUENTAS - " + nombreBanco.toUpperCase());
        System.out.println("==================================================");
        mapaCuentas.values().forEach(cuenta ->
                System.out.printf("Cuenta: %-15s | Saldo Auditado: $%.2f%n", cuenta.getNumeroCuenta(), cuenta.getSaldo())
        );
        System.out.println("==================================================\n");
    }
}