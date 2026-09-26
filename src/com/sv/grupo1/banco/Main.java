package com.sv.grupo1.banco;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

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

        DespachadorOrdenes despachador = new DespachadorOrdenes(bancoPrincipal);

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

            despachador.despachar(transaccion);
        }

        // Prueba puntual: depósito con monto inválido, debe quedar RECHAZADO
        Transaction depositoInvalido = new LocalDeposit("TXN-051", bancoPrincipal.obtenerCuenta("CTA-001"), -20.0);
        despachador.despachar(depositoInvalido);

        bancoPrincipal.apagarServicio();

        System.out.println("\n==================================================");
        System.out.println("        RECEPCIÓN DE COMPROBANTES ");
        System.out.println("==================================================");

        List<Receipt> comprobantesCompletados = despachador.recolectarComprobantes();
        comprobantesCompletados.forEach(comprobante -> System.out.println("<- [SALIDA] " + comprobante));

        bancoPrincipal.imprimirAuditoriaSaldos();
        System.out.println("Total de cuentas activas en el banco: " + bancoPrincipal.contarCuentasActivas());
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