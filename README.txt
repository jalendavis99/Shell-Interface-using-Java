MyShell - Simple Command Line Shell

Introduction:
MyShell is a basic command-line interface that provides a shell-like environment for executing commands, navigating directories, managing environment variables, and running scripts. It supports various built-in commands and provides a simple prompt for user interaction.

Usage:
To use MyShell, compile and run the Java file "MyShell.java" using a Java compiler or an integrated development environment (IDE). Once the shell is running, you can enter commands and press Enter to execute them.

Commands:
- dir/ls: List files and directories in the current directory.
- pwd: Show the current working directory.
- cd <directory>: Change the current directory to the specified directory.
- history: Show the command history.
- !!: Rerun the previous command.
- set <variable> <value>: Set an environment variable.
- unset <variable>: Unset an environment variable.
- script <filename>: Execute a script file containing a sequence of commands.
- help: Display the list of available commands.
- exit: Terminate the shell.

Note: Commands, arguments, and filenames are case-sensitive.

Examples:
- Enter "dir" to list files and directories in the current directory.
- Enter "cd Documents" to change to the "Documents" directory.
- Enter "set JAVA_HOME /usr/lib/jvm/java-11" to set the "JAVA_HOME" environment variable.
- Enter "script myscript.txt" to execute commands from the "myscript.txt" file.

Command History:
The shell maintains a history of previously executed commands. You can view the command history using the "history" command and rerun the previous command using "!!".

Environment Variables:
You can set environment variables using the "set" command and unset them using the "unset" command. Environment variables can be referenced using the format "$<variable>".

Script Execution:
To execute a script file, use the "script" command followed by the filename. The script file should contain a sequence of commands, each on a new line. Blank lines are ignored.

Termination:
To terminate the shell, enter the "exit" command or use the appropriate termination option in your development environment.


