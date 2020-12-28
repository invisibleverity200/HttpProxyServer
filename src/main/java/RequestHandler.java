import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Handler;

public class RequestHandler extends ServerSocket implements Runnable {
    private Log log = new Log();
    private LogHandler handler;

    RequestHandler(int port) throws IOException {
        super(port);

        this.handler = new LogHandler(new File("Log"),log);
        new Thread(handler).start();
    }

    @Override
    public void run() {
        int threadNumber = 0;
        while (true) {
            try {
                new Thread(new ProxyThread(this.accept(), "Thread" + String.valueOf(threadNumber), log)).start();
                System.out.println("Thread: " + Util.ANSI_GREEN + "Thread" + String.valueOf(threadNumber) + Util.ANSI_RESET + " created");
                threadNumber++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
