import lib.Receptor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Application {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("> INIT ERROR: Not enough arguments provided ...");
        }
        int port = 65432, num_messages = 0;
        try {
            num_messages = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            System.out.println("> FORMAT ERROR: Unable to convert number of messages argument ...");
        }

        /*try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("> Server is listening on port " + port);
            Socket socket = serverSocket.accept();
            System.out.println("> Waiting for client messages ...");
            while (num_messages > 0) {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                String text = reader.readLine();
                String reverseText = new StringBuilder(text).reverse().toString();
                System.out.println(text);
                writer.println("");
                num_messages--;
            }
            socket.close();
        } catch (IOException ex) {
            System.out.println("> SERVER ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }*/
        String trama = "0100000111000000";

    }
}
