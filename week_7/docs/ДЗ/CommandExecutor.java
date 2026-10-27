import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

class CommandExecutor {
    private static final ICommand[] COMMAND = {new RangeCommand(), new RepeatCommand(), new AlternateCommand()};
    private static final String[] COMMAND_TYPES;

    static {
        COMMAND_TYPES = new String[COMMAND.length];
        for (int i = 0; i < COMMAND.length; i++) {
            COMMAND_TYPES[i] = COMMAND[i].getCommandType();
        }
    }

    public void run() {
        try (Scanner consoleReader = new Scanner(System.in)) {
            IO.println("Mode:");
            String sourceType = consoleReader.nextLine().strip();

            if (Objects.equals(sourceType, "CONSOLE")) {
                IO.println("Output directory:");
                Path targetDir = Path.of(consoleReader.nextLine().strip());

                prepareTargetDirectory(targetDir);
                executeCommandFromScanner(consoleReader, sourceType, targetDir);
                return;
            }

            if (Objects.equals(sourceType, "IMPORT")) {
                IO.println("Plan file:");
                Path planFile = Path.of(consoleReader.nextLine().strip());

                IO.println("Output directory:");
                Path targetDir = Path.of(consoleReader.nextLine().strip());

                prepareTargetDirectory(targetDir);

                try (Scanner planReader = new Scanner(
                        Files.newBufferedReader(planFile, StandardCharsets.UTF_8)
                )) {
                    executeCommandFromScanner(planReader, sourceType, targetDir);
                } catch (IOException e) {
                    IO.println("Can not read plan file: " + e.getMessage());
                }
                return;
            }

            IO.println("Unsupported mode: " + sourceType);
        }
    }

    private static void prepareTargetDirectory(Path targetDir) {
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Can not create output directory: " + e.getMessage(),
                    e
            );
        }
    }

    private void executeCommandFromScanner(Scanner commandStream, String sourceType, Path targetDir) {
        try (CommandLogger logger = new CommandLogger(targetDir, sourceType)) {
            int line = 0;
            while (commandStream.hasNext()) {
                String command = commandStream.nextLine();
                line++;
                if (Objects.equals(command, "END")) {
                    return;
                }

                try {
                    CommandExecutionReport report = this.parseAndExecuteCommand(command, targetDir);

                    logger.logSuccessfullyCommand(report);
                } catch (CommandException e) {
                    logger.logRejectedCommand(command, line, e);
                } catch (IOException e) {
                    IO.println("File error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CommandExecutionReport parseAndExecuteCommand(String command, Path targetDir)
            throws CommandException, IOException {
        String[] args = command.split("\\|");

        if (args.length < 2) {
            throw new CommandException("Can not parse command: too less args");
        }

        String commandType = args[0];
        String targetFileName = args[1];

        ICommand commandInstance;
        try {
            commandInstance = findCommand(commandType);
        } catch (IllegalArgumentException _) {
            throw new CommandException("Can not find command: " + commandType);
        }

        return commandInstance.call(targetDir.resolve(targetFileName).toFile(), sliceArgs(args));
    }

    private static ICommand findCommand(String commandType) {
        for (int i = 0; i < COMMAND.length; i++) {
            if (COMMAND_TYPES[i].equals(commandType)) {
                return COMMAND[i];
            }
        }

        throw new IllegalArgumentException("Unknown command type: " + commandType);
    }

    private static String[] sliceArgs(String[] array) {
        String[] result = new String[array.length - 2];
        System.arraycopy(array, 2, result, 0, array.length - 2);
        return result;
    }
}
