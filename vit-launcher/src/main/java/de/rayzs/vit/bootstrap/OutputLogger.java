package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.file.FileDir;

import java.io.*;
import java.text.SimpleDateFormat;

public class OutputLogger extends PrintStream {

    private enum Level {
        INFO, ERROR
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy-HH-mm");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private static final String LOG_MESSAGE = "[%s | %s] %s";


    private static boolean initialized = false;


    /**
     * Initializes the logger. Should
     * only be called once during runtime.
     *
     * @throws Exception In case the logger file failed to be created.
     */
    public static void initialize() throws Exception {

        if (initialized) {
            throw new IllegalStateException("Output logger is already initialized");
        }


        initialized = true;

        final File logFile = FileDir.LOGS.getFile(DATE_FORMAT.format(System.currentTimeMillis()) + ".txt");
        logFile.createNewFile();

        final FileWriter fileWriter = new FileWriter(logFile);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        System.setOut(new OutputLogger(writer, System.out, OutputLogger.Level.INFO));
        System.setErr(new OutputLogger(writer, System.err, OutputLogger.Level.ERROR));
    }


    private final Level level;
    private final BufferedWriter writer;

    private OutputLogger(
            final BufferedWriter writer,
            final PrintStream out,
            final Level level
    ) {
        super(out);

        this.writer = writer;

        this.out = out;
        this.level = level;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            final String text = new String(buf, off, len);
            final String line = text.contains("\n") ? text : constructMessage(text);

            this.writer.write(line);
            this.writer.flush();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        super.write(buf, off, len);
    }

    private String constructMessage(final String message) {
        return LOG_MESSAGE.formatted(
                TIME_FORMAT.format(System.currentTimeMillis()),
                this.level.name().substring(0, 3),
                message
        );
    }
}