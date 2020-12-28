import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProxyThread implements Runnable {
    private Socket clientSocket;
    private Socket serverSocket;
    private String proxyThreadIndentifier;
    private Log log;


    ProxyThread(Socket clientSocket, String proxyThreadIndentifier, Log log) {
        this.clientSocket = clientSocket;
        this.proxyThreadIndentifier = proxyThreadIndentifier;
        this.log = log;
    }

    @Override
    public void run() {
        try {
            byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            InputStream inFromClient = new BufferedInputStream(clientSocket.getInputStream());
            OutputStream outToClient = clientSocket.getOutputStream();
            BufferedWriter proxyToClientBw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

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
            if (hostname.isEmpty() || hostname.isBlank()) {
                clientSocket.close();
                return;
            }
            inFromClient.reset();
            Util.safePrintln("Connection IP: " + Util.ANSI_RED + clientSocket.getInetAddress().toString().substring(1) + Util.ANSI_RESET + "\nThread: " + proxyThreadIndentifier + "\nHostname: " + Util.ANSI_GREEN + hostname + Util.ANSI_RESET + "\nPort:" + Util.ANSI_GREEN + port + Util.ANSI_RESET + "\n\n");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            String logPayload = "[" + dtf.format(now) + "]    IP: " + clientSocket.getInetAddress().toString().substring(1) + "   Hostname:Port: " + hostname + ":" + port;
            log.add(logPayload);

            serverSocket = new Socket(hostname, port);

            final InputStream inFromServer = serverSocket.getInputStream();
            final OutputStream outToServer = serverSocket.getOutputStream();

            if (hostname.contains("https")) {
                for(int i=0;i<5;i++){ //FIXME possible bug
                    in.readLine();
                }
                String line = "HTTP/1.0 200 Connection established\r\n" +
                        "Proxy-Agent: ProxyServer/1.0\r\n" +
                        "\r\n";
                proxyToClientBw.write(line);
                proxyToClientBw.flush();
            }

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
        System.out.println("Thread: " + proxyThreadIndentifier + "          [" + Util.ANSI_RED + "CLOSED" + Util.ANSI_RESET + "]");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String logPayload = "[" + dtf.format(now) + "]    IP: " + clientSocket.getInetAddress().toString().substring(1) + "   Connection closed";
        log.add(logPayload);
    }
}
