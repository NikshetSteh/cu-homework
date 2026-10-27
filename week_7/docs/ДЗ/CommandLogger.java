import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class CommandLogger implements AutoCloseable {
    private final BufferedWriter manifestWriter;
    private final BufferedWriter rejectedWriter;
    private final String sourceType;

    private int executedCommands = 0;
    private int rejectedCommands = 0;
    private int generatedValues = 0;
    private String biggestFileName = null;
    private int biggestFileValuesCount = 0;

    private String[] files = {};

    public CommandLogger(Path dir, String sourceType) throws IOException {
        this.manifestWriter = Files.newBufferedWriter(
                dir.resolve("manifest.txt"),
                StandardCharsets.UTF_8
        );
        this.rejectedWriter = Files.newBufferedWriter(
                dir.resolve("rejected-commands.txt"),
                StandardCharsets.UTF_8
        );
        this.sourceType = sourceType;
    }

    public void logSuccessfullyCommand(CommandExecutionReport report) throws IOException {
        executedCommands++;

        int generatedValues = report.generatedValues();
        this.generatedValues += generatedValues;
        if (generatedValues > this.biggestFileValuesCount) {
            this.biggestFileValuesCount = generatedValues;
            this.biggestFileName = report.targetFile();
        }

        this.manifestWriter.write(String.format(
                "%s|%s|%d",
                report.targetFile(),
                report.commandType(),
                generatedValues
        ));
        this.manifestWriter.newLine();

        this.addFile(report.targetFile());
    }

    public void logRejectedCommand(String command, int line, CommandException exception) throws IOException {
        executedCommands++;
        rejectedCommands++;

        this.rejectedWriter.write(String.format(
                "%s|%d|%s|%s",
                this.sourceType,
                line,
                exception.getMessage(),
                command
        ));
        this.rejectedWriter.newLine();
    }

    @Override
    public void close() throws IOException {
        this.manifestWriter.close();
        this.rejectedWriter.close();

        IO.println(String.format("Commands processed: %d", this.executedCommands));
        IO.println(String.format("Generated files: %d", this.files.length));
        IO.println(String.format("Rejected commands: %d", this.rejectedCommands));
        IO.println(String.format("Generated values: %d", this.generatedValues));
        IO.println(String.format("Largest file: %s", this.biggestFileName != null ? this.biggestFileName : "n/a"));
        IO.println(String.format("Largest file values: %s", this.biggestFileName != null ? this.biggestFileValuesCount : "n/a"));
    }

    private void addFile(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName is null");
        }

        for (String file : this.files) {
            if (fileName.equals(file)) {
                return;
            }
        }

        String[] newFileList = new String[this.files.length + 1];
        System.arraycopy(this.files, 0, newFileList, 0, this.files.length);
        newFileList[this.files.length] = fileName;
        this.files = newFileList;
    }
}
