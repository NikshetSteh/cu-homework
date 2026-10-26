import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

class RepeatCommand implements ICommand {
    @Override
    public CommandExecutionReport call(File targetFile, String[] args) throws CommandException, IOException {
        if (args.length != 2) {
            throw new CommandException(
                    String.format(
                            "Invalid count of args: actual %d, expected %d",
                            args.length,
                            2
                    )
            );
        }

        try {
            int value = Integer.parseInt(args[0]);
            int count = Integer.parseInt(args[1]);

            if (count <= 0) {
                throw new CommandException(String.format("Invalid count: %d, count must be greater than 0", count));
            }

            try (BufferedWriter fileWriter = Files.newBufferedWriter(
                    targetFile.toPath(),
                    StandardCharsets.UTF_8
            )) {
                for (int i = 0; i < count; i++) {
                    fileWriter.write(String.valueOf(value));
                    fileWriter.newLine();
                }
            }

            return new CommandExecutionReport(count, this.getCommandType(), targetFile.getName());
        } catch (NumberFormatException e) {
            throw new CommandException(
                    String.format(
                            "Invalid argument format: can not convert to int : %s",
                            e.getMessage()
                    )
            );
        }
    }

    @Override
    public String getCommandType() {
        return "REPEAT";
    }
}
