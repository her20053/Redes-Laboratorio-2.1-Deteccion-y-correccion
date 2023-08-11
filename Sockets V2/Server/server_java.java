package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class server_java {

    private static final int PORT = 6000;

    public static String descodificarHamming(String mensajeBinario) {
        // Simplemente eliminamos el último bit (bit de paridad) para esta
        // implementación simplificada
        return mensajeBinario.substring(0, mensajeBinario.length() - 1);
    }

    public static String descodificarCRC32(String mensajeBinario) {
        // El CRC-32 no se "descodifica" como tal, pero podemos verificar su validez.
        // En este caso, simplemente devolvemos el mensaje original sin el CRC-32 al
        // final.
        return mensajeBinario.substring(0, mensajeBinario.length() - 32);
    }

    public static String binarioAString(String mensajeBinario) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < mensajeBinario.length(); i += 8) {
            int k = Integer.parseInt(mensajeBinario.substring(i, i + 8), 2);
            resultado.append((char) k);
        }
        return resultado.toString();
    }

    public static void procesarMensaje(String mensaje) {
        String prefijo = mensaje.substring(0, 4); // Tomamos los primeros 4 caracteres como prefijo
        String contenido = mensaje.substring(4);

        String mensajeDescodificado = "";
        if ("HAM:".equals(prefijo)) {
            System.out.println(" + Mensaje recibido con codificación Hamming");
            mensajeDescodificado = descodificarHamming(contenido);
        } else if ("CRC:".equals(prefijo)) {
            System.out.println(" + Mensaje recibido con codificación CRC-32");
            mensajeDescodificado = descodificarCRC32(contenido);
        } else {
            System.out.println(" + Mensaje recibido sin codificación");
            mensajeDescodificado = contenido;
        }

        String texto_decodificado = binarioAString(mensajeDescodificado);

        System.out.println(" + Contenido (binario): " + mensajeDescodificado);
        System.out.println(" + Contenido (texto): " + texto_decodificado);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("../simulacion.txt", true))) {
            writer.write(texto_decodificado + "\n");
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Creamos el socket del servidor
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println(" + Esperando conexiones en " + PORT);

            while (true) { // Bucle infinito para escuchar conexiones
                // El servidor se queda esperando una conexión
                Socket clientSocket = serverSocket.accept();

                System.out.println(" + Conectado a " + clientSocket.getRemoteSocketAddress());

                // Creamos un lector para leer los datos enviados por el cliente
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Leemos el mensaje
                String message = reader.readLine();
                procesarMensaje(message);

                // Cerramos el socket del cliente
                clientSocket.close();
            }
            // No cerramos el socket del servidor ya que queremos que siga escuchando
            // conexiones

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
