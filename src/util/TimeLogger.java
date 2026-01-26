package util;

import java.io.File;
import java.io.FileWriter;

public class TimeLogger {

    private static final String LOG_DIR = "logs";
    private static final String FILE = LOG_DIR + "/times.csv";

    public static void log(String algorithm, long timeNs) {

        try {
            // Crear carpeta logs si no existe
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Escribir en el CSV
            try (FileWriter fw = new FileWriter(FILE, true)) {
                fw.write(algorithm + "," + timeNs + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
