import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ProxyThread implements Runnable {
    private Socket clientSocket;
    private Socket serverSocket;
    private String proxyThreadIndentifier;


    ProxyThread(Socket clientSocket, String proxyThreadIndentifier) {
        this.clientSocket = clientSocket;
        this.proxyThreadIndentifier = proxyThreadIndentifier;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connection IP: " + Util.ANSI_RED + clientSocket.getInetAddress().toString().substring(1) + Util.ANSI_RESET);

            byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            InputStream inFromClient = new BufferedInputStream(clientSocket.getInputStream());
            OutputStream outToClient = clientSocket.getOutputStream();

            String inputLine;
            String hostname = "";
            int port = 80;

            inFromClient.mark(0);
            BufferedReader in = new BufferedReader(new InputStreamReader(inFromClient));
            try {
                while (!(inputLine = in.readLine()).equals("")) {
                    if (inputLine.startsWith("Host:")) {
                        hostname = inputLine.split(" ")[1].split(":")[0];
                        try {
                            port = Integer.valueOf(inputLine.split(" ")[1].split(":")[1]);
                            break;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            port = 80;
                        }
                    }
                }
            } catch (NullPointerException e) {

            }
            if(hostname.equals("") || port == 443){
                clientSocket.close();
                return;
            }
            inFromClient.reset();
            System.out.println("Thread: " + proxyThreadIndentifier + "\nHostname: " + Util.ANSI_GREEN + hostname + Util.ANSI_RESET);
            System.out.println("Port: " + Util.ANSI_GREEN + port + Util.ANSI_RESET+ "\n\n");

            serverSocket = new Socket(hostname, port);

            final InputStream inFromServer = serverSocket.getInputStream();
            final OutputStream outToServer = serverSocket.getOutputStream();

            new Thread(() -> {
                int bytes_read;
                try {
                    while ((bytes_read = inFromClient.read(request)) != -1) {
                        outToServer.write(request, 0, bytes_read);
                        outToServer.flush();
                    }
                } catch (IOException e) {
                }
            }).start();
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                }
            } catch (IOException e) {
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Thread " + proxyThreadIndentifier + "          [" + Util.ANSI_RED + "CLOSED" + Util.ANSI_RESET + "]");
    }
}
