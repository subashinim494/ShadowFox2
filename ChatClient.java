import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public ChatClient(String serverAddress, int port) {
        
        setTitle("Java Chat - Client");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage()); 

        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            
            new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        chatArea.append(msg + "\n");
                    }
                } catch (IOException ex) {
                    chatArea.append("Connection closed.\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to server");
            e.printStackTrace();
        }

        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient("localhost", 1234));
    }
}

