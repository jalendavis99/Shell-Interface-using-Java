import java.io.*;
import java.util.*;

public class MyShell {
    private static List<String> commandHistory = new ArrayList<>();
    private static Map<String, String> environmentVariables = new HashMap<>();

    public static void main(String[] args) throws IOException {
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        File currentDirectory = new File(System.getProperty("user.dir"));

        while (true) {
            System.out.print("mysh> ");
            commandLine = console.readLine();

            if (commandLine.isEmpty()) {
                continue;
            }

            // Add the command to history
            commandHistory.add(commandLine);

            // Parse the command and arguments
            List<String> commands = parseCommands(commandLine);

            // Execute pipeline commands
            executePipelineCommands(commands, currentDirectory);
        }
    }

    private static List<String> parseCommands(String commandLine) {
        List<String> commands = new ArrayList<>();
        String[] commandParts = commandLine.split("\\|");
        for (String part : commandParts) {
            commands.add(part.trim());
        }
        return commands;
    }

    private static void executePipelineCommands(List<String> commands, File currentDirectory) throws IOException {
        ProcessBuilder processBuilder = null;
        Process previousProcess = null;

        for (String commandLine : commands) {
            // Parse the command and arguments
            String[] commandParts = commandLine.trim().split(" ");
            String command = commandParts[0];

            // Handle different commands
            switch (command) {
                case "dir":
                case "ls":
                    processBuilder = createProcessBuilder(commandParts);
                    break;
                case "pwd":
                    System.out.println(currentDirectory.getAbsolutePath());
                    break;
                case "cd":
                    if (commandParts.length > 1) {
                        changeDirectory(commandParts[1], currentDirectory);
                    } else {
                        System.out.println("Invalid command: Missing directory argument");
                    }
                    break;
                case "history":
                    printCommandHistory();
                    break;
                case "!!":
                    rerunPreviousCommand();
                    break;
                case "set":
                    setEnvironmentVariable(commandParts);
                    break;
                case "unset":
                    unsetEnvironmentVariable(commandParts);
                    break;
                case "script":
                    if (commandParts.length > 1) {
                        executeScript(commandParts[1], currentDirectory);
                    } else {
                        System.out.println("Invalid command: Missing script filename");
                    }
                    break;
                case "help":
                    displayHelp();
                    break;
                case "exit":
                    System.out.println("Shell terminated.");
                    System.exit(0);
                default:
                    System.out.println("Invalid command: " + command);
            }

            if (processBuilder != null) {
                processBuilder.directory(currentDirectory);
                Process process = processBuilder.start();

                if (previousProcess != null) {
                    redirectOutput(previousProcess, process);
                }

                previousProcess = process;
            }
        }

        if (previousProcess != null) {
            // Output the contents returned by the last command in the pipeline
            BufferedReader reader = new BufferedReader(new InputStreamReader(previousProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static ProcessBuilder createProcessBuilder(String[] commandParts) {
        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            List<String> input = new ArrayList<>();
            input.add("cmd");
            input.add("/c");
            input.addAll(Arrays.asList(commandParts));
            processBuilder = new ProcessBuilder(input);
        } else {
            processBuilder = new ProcessBuilder(commandParts);
        }
        return processBuilder;
    }

    private static void changeDirectory(String directory, File currentDirectory) {
        File newDirectory;
        if (directory.equals("..")) {
            newDirectory = currentDirectory.getParentFile();
        } else {
            newDirectory = new File(currentDirectory, directory);
        }

        if (newDirectory.exists() && newDirectory.isDirectory()) {
            currentDirectory = newDirectory;
        } else {
            System.out.println("Directory not found: " + newDirectory.getAbsolutePath());
        }
    }

    private static void printCommandHistory() {
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println(i + " " + commandHistory.get(i));
        }
    }

    private static void rerunPreviousCommand() throws IOException {
        if (!commandHistory.isEmpty()) {
            String previousCommand = commandHistory.get(commandHistory.size() - 1);
            executePipelineCommands(parseCommands(previousCommand), new File(System.getProperty("user.dir")));
        } else {
            System.out.println("No previous command found.");
        }
    }

    private static void setEnvironmentVariable(String[] commandParts) {
        if (commandParts.length > 2) {
            String variable = commandParts[1];
            String value = commandParts[2];
            environmentVariables.put(variable, value);
            System.out.println("Environment variable set: " + variable + "=" + value);
        } else {
            System.out.println("Invalid command: Missing variable or value argument");
        }
    }

    private static void unsetEnvironmentVariable(String[] commandParts) {
        if (commandParts.length > 1) {
            String variable = commandParts[1];
            if (environmentVariables.containsKey(variable)) {
                environmentVariables.remove(variable);
                System.out.println("Environment variable unset: " + variable);
            } else {
                System.out.println("Environment variable not found: " + variable);
            }
        } else {
            System.out.println("Invalid command: Missing variable argument");
        }
    }

    private static void executeScript(String scriptFilename, File currentDirectory) throws IOException {
        File scriptFile = new File(currentDirectory, scriptFilename);
        if (scriptFile.exists() && scriptFile.isFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    executePipelineCommands(parseCommands(line), currentDirectory);
                }
            }
            reader.close();
        } else {
            System.out.println("Script file not found: " + scriptFile.getAbsolutePath());
        }
    }

    private static void redirectOutput(Process previousProcess, Process currentProcess) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(previousProcess.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(currentProcess.getOutputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayHelp() {
        System.out.println("Available Commands:");
        System.out.println("dir/ls - List files and directories");
        System.out.println("pwd - Show current working directory");
        System.out.println("cd <directory> - Change current directory");
        System.out.println("history - Show command history");
        System.out.println("!! - Rerun previous command");
        System.out.println("set <variable> <value> - Set environment variable");
        System.out.println("unset <variable> - Unset environment variable");
        System.out.println("script <filename> - Execute script file");
        System.out.println("help - Display this help message");
        System.out.println("exit - Terminate the shell");
    }
}