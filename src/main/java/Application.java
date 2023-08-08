import lib.Receptor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("> INIT ERROR: Not enough arguments provided ...");
        }
        int port = 65432, num_messages = 0, answers = 0;
        try {
            num_messages = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            System.out.println("> FORMAT ERROR: Unable to convert number of messages argument ...");
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("> Server is listening on port " + port);
            Socket socket = serverSocket.accept();
            System.out.println("> Waiting for client messages ...");
            Receptor receptor = new Receptor(false);
            Instant start = Instant.now();
            for(int i = 0; i < num_messages; i++) {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                List<String> message = List.of(reader.readLine().split(":"));
                if(receptor.setTrama(message.get(0))) {
                    if(message.get(1).equals("1")) {
                        if(receptor.checkTrama() <= 0) {
                            System.out.println("> Bad message");
                        } else {
                            System.out.println("> "+receptor.binToString());
                            answers++;
                        }
                    } else {
                        if(receptor.fixTrama() == 0) {
                            System.out.println("> Bad message");
                        } else {
                            System.out.println("> "+receptor.binToString());
                            answers++;
                        }
                    }
                } else {
                    System.out.println("> Bad message");
                }
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println("");
            }
            socket.close();
            Instant end = Instant.now();
            System.out.println("\n> Total answers: "+answers+"/"+num_messages+" ("+(double)(num_messages/answers)*100+"%)");
            System.out.println("> Total time: "+ Duration.between(start, end).toSeconds()+" s");
        } catch (IOException ex) {
            System.out.println("> SERVER ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
