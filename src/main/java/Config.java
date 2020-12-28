import java.io.*;

public class Config {
    static int getPort() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("config")));
        return 0;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
