import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ProxyThread implements Runnable {
    private Socket clientSocket;
    private Socket serverSocket;


    ProxyThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connection IP: " + Util.ANSI_RED + clientSocket.getInetAddress() + Util.ANSI_RESET);

            byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            InputStream inFromClient = new BufferedInputStream(clientSocket.getInputStream());
            OutputStream outToClient = clientSocket.getOutputStream();

            String inputLine;
            String hostname = "217.160.231.21";
            int port = 80;

            inFromClient.mark(0);
            BufferedReader in = new BufferedReader(new InputStreamReader(inFromClient));

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
            inFromClient.reset();
            System.out.println("Hostname: " + Util.ANSI_GREEN + hostname + Util.ANSI_RESET);
            System.out.println("Port: " + Util.ANSI_GREEN + port + Util.ANSI_RESET);

            serverSocket = new Socket(hostname, port);

            final InputStream inFromServer = serverSocket.getInputStream();
            final OutputStream outToServer = serverSocket.getOutputStream();

            new Thread(() -> {
                int bytes_read;
                while (true) {
                    try {
                        while ((bytes_read = inFromClient.read(request)) != -1) {
                            outToServer.write(request, 0, bytes_read);
                            outToServer.flush();
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            }).start();
            while (true) {
                int bytes_read;
                try {
                    while ((bytes_read = inFromServer.read(reply)) != -1) {
                        outToClient.write(reply, 0, bytes_read);
                        outToClient.flush();
                    }
                } catch (IOException e) {
                    break;
                }

                in.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
