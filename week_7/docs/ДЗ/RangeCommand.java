import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

class RangeCommand implements ICommand {
    @Override
    public CommandExecutionReport call(File targetFile, String[] args) throws CommandException, IOException {
        if (args.length != 3) {
            throw new CommandException(
                    String.format(
                            "Invalid count of args: actual %d, expected %d",
                            args.length,
                            3
                    )
            );
        }

        try {
            int start = Integer.parseInt(args[0]);
            int end = Integer.parseInt(args[1]);
            int step = Integer.parseInt(args[2]);

            if (
                    (end > start && step < 0) || ((end < start && step > 0) || (step == 0))
            ) {
                throw new CommandException(String.format(
                        "Invalid range %d -> %d, step %d, end is unreachable",
                        start, end, step
                ));
            }

            int values = 0;
            try (BufferedWriter fileWriter = Files.newBufferedWriter(
                    targetFile.toPath(),
                    StandardCharsets.UTF_8
            )) {
                for (
                        int i = start;
                        step > 0 ? i <= end : i >= end;
                        i += step
                ) {
                    fileWriter.write(String.valueOf(i));
                    fileWriter.newLine();
                    values += 1;
                }
            }
            return new CommandExecutionReport(values, this.getCommandType(), targetFile.getAbsolutePath());
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
        return "RANGE";
    }
}
