import java.util.ArrayList;
import java.util.Collections;

public class Log {
    private ArrayList<String> logs = new ArrayList<String>();

    synchronized void add(String payload) {
       if(!payload.equals("")) logs.add(payload);
    }

    synchronized String get() {
        if (logs.size() > 0) {
            String payload = logs.get(0);
            shiftLeft();
            return payload;
        }
        return "";
    }

    private synchronized void shiftLeft() {
        for (int i = 1; i < logs.size() - 1; i++) {
            logs.set(i - 1, logs.get(i));

        }
        logs.remove(logs.size() - 1);
    }

}
