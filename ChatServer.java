import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(1234);
            System.out.println("Chat Server started... Type 'exit' to stop it.");

           
            new Thread(() -> {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    while (true) {
                        String command = consoleReader.readLine();
                        if ("exit".equalsIgnoreCase(command)) {
                            if (serverSocket != null && !serverSocket.isClosed()) {
                                serverSocket.close();
                                System.out.println("Server socket closed.");
                            }
                            System.exit(0); 
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new ClientHandler(clientSocket).start();
                } catch (SocketException e) {
                    System.out.println("Server stopped accepting new connections.");
                    break; 
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);

                String message;
                while ((message = in.readLine()) != null) {
                    for (PrintWriter writer : clientWriters) {
                        writer.println(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    clientWriters.remove(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
