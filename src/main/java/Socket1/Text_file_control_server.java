package Socket1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Text_file_control_server {
    private Socket socket;
    private ServerSocket serverSocket;
    //se declaran estas dos variables de socket y serverSocket para poder
    // ejecutar una conexión del cliente y el socket del servidor
    private DataInputStream bufferDeEntrada = null;
    //este objeto se usa para leer los datos del socket
    private DataOutputStream bufferDeSalida = null;
    //este objeto permite escribir los datos del socket.
    Scanner escaner = new Scanner(System.in);
    final String COMANDO_TERMINACION = "salir()";
    //se usa para terminar la conexión cuando se requiera

    public void levantarConexion(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            mostrarTexto("Esperando conexión entrante en el puerto " + String.valueOf(puerto) + "...");
            socket = serverSocket.accept();
            mostrarTexto("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");
        } catch (Exception e) {
            mostrarTexto("Error en levantarConexion(): " + e.getMessage());
            System.exit(0);
        }
    }
    //este metodo inicia una conexión del servidor con el cliente
    public void flujos() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            mostrarTexto("Error en la apertura de flujos");
        }
    }
    //este metodo prepara los flujos de entrada y de salida de los datos entre el servidor y el cliente mediante el socket, para que el servidor
// lea los datos enviados por el cliente y que el servidor envie los datos de nuevo al cliente.
    public void recibirArchivos() {
        String st = "";
        try {
            do {
                st = bufferDeEntrada.readUTF();
                mostrarTexto("\n[Cliente] => " + st);
                System.out.print("\n[Usted] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {
            cerrarConexion();
        }
    }
    //este metodo recibe los datos enviados por el cliente y los muestra en la consola.


    public void enviar(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            mostrarTexto("Error en enviar(): " + e.getMessage());
        }
    }
    //este metodo es para enviarle un mensaje al cliente mediante el socket de salida.

    public static void mostrarTexto(String s) {
        System.out.print(s);
    }

    public void agregarArchivos() {
        while (true) {
            System.out.print("[Usted] => ");
            enviar(escaner.nextLine());
        }
    }
    //permite al servidor enviarle mensajes al cliente, o sea ejecuta una comunicación entre el servidor y el cliente.

    public void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
        } catch (IOException e) {
            mostrarTexto("Excepción en cerrarConexion(): " + e.getMessage());
        } finally {
            mostrarTexto("Conversación finalizada....");
            System.exit(0);

        }
    }
    //este metodo cierra la conexión con el cliente cuando no se requiera mas de esta.

    public void ejecutarConexion(int puerto) {
        Thread hilo = new Thread(() -> {
            while (true) {
                try {
                    levantarConexion(puerto);
                    flujos();
                    recibirArchivos();
                } finally {
                    cerrarConexion();
                }
            }
        });
        hilo.start();
    }
    //usa el parametro de puerto para ejecutar el servidor en un hilo,
    // esto es con el fin de que el servidor acepte entradas de conexión.

    public static void main(String[] args) throws IOException {
        Text_file_control_server s = new Text_file_control_server();
        Scanner sc = new Scanner(System.in);

        mostrarTexto("Ingresa el puerto [5050 por defecto]: ");
        String puerto = sc.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        s.ejecutarConexion(Integer.parseInt(puerto));
        s.agregarArchivos();
    }
    //el Main en esta clase inicializa el servidor mediante el socket, pide el puerto en el que el servidor tenga una
    // conexión, ejecuta el servidor por un thread y deja que el servidor envie mensajes al cliente.
}
