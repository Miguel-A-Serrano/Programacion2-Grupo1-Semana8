package com.sv.grupo1.banco;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class Main {
    private static final String ARCHIVO = "transacciones_grupo_1.dat";

    public static void main(String[] args) {
        Bank bancoPrincipal = new Bank("Banco del Grupo");

        // Inicialización de cuentas de prueba
        bancoPrincipal.registrarCuenta(new BankAccount("CTA-001", 15000.0));
        bancoPrincipal.registrarCuenta(new BankAccount("CTA-002", 8000.0));

        System.out.println("==================================================");
        System.out.println(" INICIO DE PRUEBAS MASIVAS CONCURRENTES (50 HILOS)");
        System.out.println("==================================================");

        List<Future<Receipt>> listaFuturos = new ArrayList<>();

        // Generar 50 transacciones mixtas concurrentes
        for (int i = 1; i <= 50; i++) {
            String idTransaccion = String.format("TXN-%03d", i);
            BankAccount origen = (i % 2 == 0) ? bancoPrincipal.obtenerCuenta("CTA-001") : bancoPrincipal.obtenerCuenta("CTA-002");
            BankAccount destino = (i % 2 == 0) ? bancoPrincipal.obtenerCuenta("CTA-002") : bancoPrincipal.obtenerCuenta("CTA-001");

            Transaction transaccion;
            if (i % 5 == 0) {
                transaccion = new LocalDeposit(idTransaccion, origen, 50.0);
            } else if (i % 7 == 0) {
                transaccion = new LocalWithdraw(idTransaccion, origen, 30.0);
            } else {
                transaccion = new IntramurosTransfer(idTransaccion, origen, destino, 100.0);
            }

            listaFuturos.add(bancoPrincipal.procesarTransaccion(transaccion));
        }

        bancoPrincipal.apagarServicio();

        System.out.println("\n==================================================");
        System.out.println("        RECEPCIÓN DE COMPROBANTES ");
        System.out.println("==================================================");

        List<Receipt> comprobantesCompletados = new ArrayList<>();

        for (Future<Receipt> futuro : listaFuturos) {
            try {
                Receipt comprobante = futuro.get();
                System.out.println("<- [SALIDA] " + comprobante);
                comprobantesCompletados.add(comprobante);
            } catch (Exception e) {
                System.err.println("Error procesando comprobante: " + e.getMessage());
            }
        }

        bancoPrincipal.imprimirAuditoriaSaldos();
        guardarEnArchivoDat(comprobantesCompletados, ARCHIVO);
    }

    private static void guardarEnArchivoDat(List<Receipt> comprobantes, String rutaArchivo) {
        try (ObjectOutputStream flujoSalida = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            flujoSalida.writeObject(comprobantes);
            System.out.println("-> Archivo guardado con éxito: " + rutaArchivo);
        } catch (Exception e) {
            System.err.println("Error al persistir el archivo .dat: " + e.getMessage());
        }
    }
}
