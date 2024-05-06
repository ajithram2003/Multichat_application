import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {
    private JFrame frame;
    private JTextArea chatTextArea;
    private JTextField messageTextField;
    private JButton sendButton;

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String serverIP, int port, String clientName) {
        try {
            clientSocket = new Socket(serverIP, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println(clientName);

            frame = new JFrame("Chat Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setLayout(new BorderLayout());

            chatTextArea = new JTextArea();
            chatTextArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(chatTextArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BorderLayout());

            messageTextField = new JTextField();
            inputPanel.add(messageTextField, BorderLayout.CENTER);

            sendButton = new JButton("Send");
            inputPanel.add(sendButton, BorderLayout.EAST);

            frame.add(inputPanel, BorderLayout.SOUTH);

            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String message = messageTextField.getText();
                    out.println(message);
                    messageTextField.setText("");
                }
            });

            Thread serverThread = new Thread(new ServerListener());
            serverThread.start();

            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String message) {
        chatTextArea.append(message + "\n");
    }

    private class ServerListener implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    appendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String serverIP = "127.0.0.1";
        int port = 8000;
        String clientName = JOptionPane.showInputDialog("Enter your name:");

        Client client = new Client(serverIP, port, clientName);
    }
}
