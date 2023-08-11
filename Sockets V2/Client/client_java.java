package Client;

import java.net.Socket;
import java.net.InetAddress;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.CRC32;

public class client_java {

    private static String HOST = "127.0.0.1";
    private static int PORT = 6000;

    public static String preguntarCodificacion() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n¿Cómo deseas codificar tu mensaje?");
        System.out.println("1. Hamming");
        System.out.println("2. CRC-32");
        System.out.println("3. Sin codificación");

        String opcion = scanner.nextLine();
        return opcion;
    }

    public static String codificarHamming(String mensaje) {
        // Para simplificar, usaremos paridad de un bit
        // Si hay un número impar de '1's, agregamos un '1' al final, de lo contrario un
        // '0'
        long count = mensaje.chars().filter(ch -> ch == '1').count();
        return count % 2 == 0 ? mensaje + "0" : mensaje + "1";
    }

    public static String codificarCRC32Binario(String mensaje) {
        CRC32 crc = new CRC32();
        crc.update(mensaje.getBytes());
        String crc_value = String.format("%32s", Long.toBinaryString(crc.getValue())).replace(' ', '0');
        return mensaje + crc_value; // Concatenamos el CRC-32 al final del mensaje
    }

    public static String stringABinario(String mensaje) {
        StringBuilder resultado = new StringBuilder();
        for (char caracter : mensaje.toCharArray()) {
            resultado.append(String.format("%08d", Integer.parseInt(Integer.toBinaryString((int) caracter))));
        }
        return resultado.toString();
    }

    public static String agregarRuido(String mensajeBinario, double umbral) {
        StringBuilder mensajeRuidoso = new StringBuilder();
        Random rand = new Random();
        for (char bit : mensajeBinario.toCharArray()) {
            if (rand.nextDouble() < umbral) {
                mensajeRuidoso.append(bit == '0' ? '1' : '0');
            } else {
                mensajeRuidoso.append(bit);
            }
        }
        return mensajeRuidoso.toString();
    }

    public static void enviarMensaje() throws IOException, UnknownHostException {

        Scanner scanner = new Scanner(System.in);
        String codificacion = preguntarCodificacion();

        System.out.print("Introduce el mensaje a enviar: ");
        String payload = scanner.nextLine();
        String payloadBinario = stringABinario(payload);

        String prefijo = "";
        if ("1".equals(codificacion)) {
            payloadBinario = codificarHamming(payloadBinario);
            prefijo = "HAM:";
        } else if ("2".equals(codificacion)) {
            payloadBinario = codificarCRC32Binario(payloadBinario);
            prefijo = "CRC:";
        } else if (!"3".equals(codificacion)) {
            System.out.println("Opción no válida. Enviando mensaje sin codificar.");
        }

        String mensajeFinal = prefijo + payloadBinario;

        // Agregamos ruido al mensaje binario antes de enviar
        mensajeFinal = agregarRuido(mensajeFinal, 0.01);

        OutputStreamWriter writer = null;
        System.out.println(" + Emisor Java Sockets\n");

        Socket socketCliente = new Socket(InetAddress.getByName(HOST), PORT);
        System.out.println(" + Enviando Data\n");

        writer = new OutputStreamWriter(socketCliente.getOutputStream());
        writer.write(mensajeFinal);
        writer.flush();

        System.out.println(" + Liberando Sockets\n");
        writer.close();
        socketCliente.close();
    }

    // Función para generar una cadena aleatoria de una longitud dada
    public static String generarCadenaAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder cadenaAleatoria = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            cadenaAleatoria.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
        }

        return cadenaAleatoria.toString();
    }

    public static void enviarMensajeSimulado() throws IOException, UnknownHostException {

        Scanner scanner = new Scanner(System.in);
        String codificacion = preguntarCodificacion();

        // Preguntar al usuario cuántas letras quiere en la cadena aleatoria
        System.out.print("Introduce la cantidad de letras para el mensaje aleatorio: ");
        int longitudMensaje = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        for (int i = 0; i < 10; i++) {
            // Generar una cadena aleatoria con esa cantidad de letras
            String payload = generarCadenaAleatoria(longitudMensaje);
            System.out.println("Mensaje aleatorio generado: " + payload);

            String payloadBinario = stringABinario(payload);

            String prefijo = "";
            if ("1".equals(codificacion)) {
                payloadBinario = codificarHamming(payloadBinario);
                prefijo = "HAM:";
            } else if ("2".equals(codificacion)) {
                payloadBinario = codificarCRC32Binario(payloadBinario);
                prefijo = "CRC:";
            } else if (!"3".equals(codificacion)) {
                System.out.println("Opción no válida. Enviando mensaje sin codificar.");
            }

            String mensajeFinal = prefijo + payloadBinario;

            // Agregamos ruido al mensaje binario antes de enviar
            mensajeFinal = agregarRuido(mensajeFinal, 0.01);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("../simulacion.txt", true))) {
                writer.write(payload + "\n");
            }

            OutputStreamWriter writer = null;
            System.out.println(" + Emisor Java Sockets\n");

            Socket socketCliente = new Socket(InetAddress.getByName(HOST), PORT);
            System.out.println(" + Enviando Data\n");

            writer = new OutputStreamWriter(socketCliente.getOutputStream());
            writer.write(mensajeFinal);
            writer.flush();

            System.out.println(" + Liberando Sockets\n");
            writer.close();
            socketCliente.close();

            // Pausa de 0.2 segundos
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException, UnknownHostException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        while (!salir) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Enviar mensajes");
            System.out.println("2. Simular mensajes");
            System.out.println("3. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    enviarMensaje();
                    break;
                case 2:
                    enviarMensajeSimulado();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }
}
