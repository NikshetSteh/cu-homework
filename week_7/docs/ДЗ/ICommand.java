import java.io.File;
import java.io.IOException;

interface ICommand {
    CommandExecutionReport call(File targetFile, String[] args) throws CommandException, IOException;

    String getCommandType();
}
