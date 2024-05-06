import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ArrayList<Socket> clientSockets = new ArrayList<>();
    private ArrayList<String> clientNames = new ArrayList<>();

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcast(String message) {
        for (Socket socket : clientSockets) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 8000;
        Server server = new Server();
        server.start(port);
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientName = in.readLine();
                clientNames.add(clientName);
                System.out.println("New connection: " + clientName);

                broadcast(clientName + " joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    broadcast(clientName + ": " + message);
                }

                clientNames.remove(clientName);
                clientSockets.remove(clientSocket);
                System.out.println("Connection closed: " + clientName);
                broadcast(clientName + " left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}