package util;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

/**
 * Guarda entradas de tiempos en logs/times.csv
 * Formato CSV: from,to,algorithm,timeNs,timestamp
 *
 * Notas:
 * - addListener(listener) permite que la UI sea notificada al agregar una entrada.
 * - getLatestManualTime(from,to) busca la última línea MANUAL para esa conexión (retorna nanos o -1).
 */
public class ExecutionTimeStore {

    private static final String LOG_DIR = "logs";
    private static final String FILE = LOG_DIR + "/times.csv";
    private static final Object LOCK = new Object();

    public static class Entry {
        public final String from;
        public final String to;
        public final String algorithm;
        public final long timeNs;
        public final String timestamp;

        public Entry(String from, String to, String algorithm, long timeNs, String timestamp) {
            this.from = from;
            this.to = to;
            this.algorithm = algorithm;
            this.timeNs = timeNs;
            this.timestamp = timestamp;
        }
    }

    private static final List<Consumer<Entry>> listeners = new ArrayList<>();

    public static void init() {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) dir.mkdirs();

        File f = new File(FILE);
        try {
            boolean newFile = f.createNewFile();
            if (newFile) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
                    pw.println("from,to,algorithm,timeNs,timestamp");
                }
            }
        } catch (IOException ignored) {}

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                long total = totalTimeNs();
                append("TOTAL", "", "TOTAL", total);
            } catch (Exception ignored) {}
        }));
    }

    public static void append(String from, String to, String algorithm, long timeNs) {
        Entry e;
        synchronized (LOCK) {
            try {
                File f = new File(FILE);
                f.getParentFile().mkdirs();
                String ts = Instant.now().toString();
                try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
                    pw.printf("%s,%s,%s,%d,%s%n",
                            safe(from), safe(to), safe(algorithm), timeNs, ts);
                }
                e = new Entry(from, to, algorithm, timeNs, ts);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        notifyListeners(e);
    }

    private static void notifyListeners(Entry e) {
        synchronized (listeners) {
            for (Consumer<Entry> l : listeners) {
                try { l.accept(e); } catch (Exception ignored) {}
            }
        }
    }

    public static void addListener(Consumer<Entry> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public static void removeListener(Consumer<Entry> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public static List<Entry> loadAll() {
        List<Entry> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        synchronized (LOCK) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first && line.startsWith("from,to,algorithm")) { first = false; continue; }
                    first = false;
                    String[] p = line.split(",", 5);
                    if (p.length >= 5) {
                        String from = p[0];
                        String to = p[1];
                        String algorithm = p[2];
                        long timeNs = 0;
                        try { timeNs = Long.parseLong(p[3]); } catch (Exception ignored) {}
                        String ts = p[4];
                        list.add(new Entry(from, to, algorithm, timeNs, ts));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void saveAll(List<Entry> entries) {
        synchronized (LOCK) {
            try {
                File f = new File(FILE);
                f.getParentFile().mkdirs();
                try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
                    pw.println("from,to,algorithm,timeNs,timestamp");
                    for (Entry en : entries) {
                        pw.printf("%s,%s,%s,%d,%s%n",
                                safe(en.from), safe(en.to), safe(en.algorithm), en.timeNs, safe(en.timestamp));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long totalTimeNs() {
        long sum = 0;
        for (Entry e : loadAll()) {
            if ("TOTAL".equals(e.algorithm) && "TOTAL".equals(e.from)) continue;
            sum += e.timeNs;
        }
        return sum;
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replaceAll("[\\n\\r]", " ").replace(",", " ");
    }

    /**
     * Busca la última entrada MANUAL para from->to y devuelve su timeNs.
     * Si no encuentra nada retorna -1.
     */
    public static long getLatestManualTimeNs(String from, String to) {
        List<Entry> all = loadAll();
        for (int i = all.size() - 1; i >= 0; i--) {
            Entry e = all.get(i);
            if ("MANUAL".equals(e.algorithm) && e.from.equals(from) && e.to.equals(to)) {
                return e.timeNs;
            }
        }
        return -1;
    }
}
