import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogHandler implements Runnable {
    private File file;
    private Log log;

    LogHandler(File file, Log log) {
        this.file = file;
        this.log = log;
    }

    @Override
    public void run() {
        while (true) {
            PrintWriter output = null;
            try {
                Thread.sleep(100);
                String payload = log.get();
                if ((!payload.isEmpty() && payload.contains("IP")) || (!payload.isBlank() && payload.contains("IP"))) {
                    output = new PrintWriter(new FileWriter(file, true));
                    output.printf("%s\n", log.get());
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
