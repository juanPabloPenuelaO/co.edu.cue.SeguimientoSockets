package Socket1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    //declara la variable para establecer la conexión
    private DataInputStream bufferDeEntrada = null;
    //declara la variable para leer ls datos del socket
    private DataOutputStream bufferDeSalida = null;
    //declara la variable para agregar o escribir datos del socket
    Scanner teclado = new Scanner(System.in);
    final String COMANDO_TERMINACION = "salir()";
    //para salir de la conexión cuando se requiera

    public void levantarConexion(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            mostrarTexto("Conectado a :" + socket.getInetAddress().getHostName());
        } catch (Exception e) {
            mostrarTexto("Excepción al levantar conexión: " + e.getMessage());
            System.exit(0);
        }
    }
    //este metodo usa los parametros del ip y el puerto para poder establecer la conexión del socket con el servidor.

    public static void mostrarTexto(String s) {
        System.out.println(s);
    }

    public void abrirFlujos() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            mostrarTexto("Error en la apertura de flujos");
        }
    }
    //este metodo permite que haya comunicación entre el cliente y servidor.

    public void subirTexto(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            mostrarTexto("IOException on enviar");
        }
    }
    //este metodo es para enviar un mensaje al servidor con el socket que se estableció.

    public void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
            mostrarTexto("Conexión terminada");
        } catch (IOException e) {
            mostrarTexto("IOException on cerrarConexion()");
        }finally{
            System.exit(0);
        }
    }
    //este metodo permite cerrar la conexión y los flujos que esten abiertos en el servidor

    public void ejecutarConexion(String ip, int puerto) {
        Thread hilo = new Thread(() -> {
            try {
                levantarConexion(ip, puerto);
                abrirFlujos();
                recibirArchivo();
            } finally {
                cerrarConexion();
            }
        });
        hilo.start();
    }
    //el metodo crea un hilo que se encarga de establecer la conexión con el servidor, luego recibe datos
    // y finalmente cierra la conexión, se puede decir que cumple con el proceso completo de forma asincrona.

    public void recibirArchivo() {
        String st = "";
        try {
            do {
                st = bufferDeEntrada.readUTF();
                mostrarTexto("\n[Servidor] => " + st);
                System.out.print("\n[Usted] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {}
    }
    //este metodo recibe datos del servidor con el flujo de entrada que se asocia al socket y luego los muestra en consola

    public void escribirDatos() {
        String entrada = "";
        while (true) {
            System.out.print("[Usted] => ");
            entrada = teclado.nextLine();
            if(entrada.length() > 0)
                subirTexto(entrada);
        }
    }
    //este metodo deja al usuario poder ingresar los datos desde la consola y usa el socket para enviarlo al servidor.
    public static void main(String[] argumentos) {
        Client cliente = new Client();
        Scanner escaner = new Scanner(System.in);
        mostrarTexto("Ingresa la IP: [localhost por defecto] ");
        String ip = escaner.nextLine();
        if (ip.length() <= 0) ip = "localhost";

        mostrarTexto("Puerto: [5050 por defecto] ");
        String puerto = escaner.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        cliente.ejecutarConexion(ip, Integer.parseInt(puerto));
        cliente.escribirDatos();
    }
    //con este Main podemos crear una instancia de la clase, que pide que el usuario ingrese los parametros
    // de ip y puerto para poder conectarse al servidor y poder establecer la conexión con este.
}
