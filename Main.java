import java.io.*;
import java.util.*;

public class Main {

    public static List<String> parseInput(String input) {
        List<String> args = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Backslash OUTSIDE quotes

            if (c == '\\' && !inSingleQuote && !inDoubleQuote) {
                if (i + 1 < input.length()) {
                    current.append(input.charAt(i + 1));
                    i++;
                }
                continue;
            }
            
            // Backslash INSIDE double quotes

            if (c == '\\' && inDoubleQuote) {
                if (i + 1 < input.length()) {
                    char next = input.charAt(i + 1);

                    if (next == '"' || next == '\\') {
                        current.append(next);
                        i++;
                    } else {
                        current.append('\\'); // keep backslash
                    }
                } else {
                    current.append('\\');
                }
                continue;
            }

            // ===============================
            // Single Quote Handling
            // ===============================
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            }

            // ===============================
            // Double Quote Handling
            // ===============================
            if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            // ===============================
            // Whitespace outside quotes
            // ===============================
            if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    args.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            args.add(current.toString());
        }

        return args;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                break;
            }

            List<String> tokens = parseInput(input);
            if (tokens.isEmpty())
                continue;

            // ===============================
            // 🔥 Detect Redirection
            // ===============================
            String redirectFile = null;
            List<String> cleanTokens = new ArrayList<>();

            for (int i = 0; i < tokens.size(); i++) {
                String t = tokens.get(i);

                if (t.equals(">") || t.equals("1>")) {
                    if (i + 1 < tokens.size()) {
                        redirectFile = tokens.get(i + 1);
                        i++; // skip filename
                    }
                } else {
                    cleanTokens.add(t);
                }
            }

            if (cleanTokens.isEmpty())
                continue;

            String command = cleanTokens.get(0);

            PrintStream output = System.out;

            // If redirection exists, create file stream
            if (redirectFile != null) {
                File file = new File(redirectFile);
                output = new PrintStream(new FileOutputStream(file, false));
            }

            // =========================
            // Built-in: echo
            // =========================
            if (command.equals("echo")) {
                for (int i = 1; i < cleanTokens.size(); i++) {
                    output.print(cleanTokens.get(i));
                    if (i < cleanTokens.size() - 1) {
                        output.print(" ");
                    }
                }
                output.println();
                if (redirectFile != null)
                    output.close();
            }

            // =========================
            // Built-in: type
            // =========================
            else if (command.equals("type")) {
                if (cleanTokens.size() > 1) {
                    String arg = cleanTokens.get(1);

                    if (arg.equals("echo") || arg.equals("exit") || arg.equals("type")) {
                        output.println(arg + " is a shell builtin");
                    } else {
                        String path = System.getenv("PATH");
                        String[] pathDirs = path.split(File.pathSeparator);
                        boolean found = false;

                        for (String dir : pathDirs) {
                            File file = new File(dir, arg);
                            if (file.exists() && file.canExecute()) {
                                output.println(arg + " is " + file.getAbsolutePath());
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            System.out.println(arg + ": not found");
                        }
                    }
                }
                if (redirectFile != null)
                    output.close();
            }

            // =========================
            // External commands
            // =========================
            else {
                String path = System.getenv("PATH");
                String[] pathDirs = path.split(File.pathSeparator);
                boolean found = false;

                for (String dir : pathDirs) {
                    File file = new File(dir, command);

                    if (file.exists() && file.canExecute()) {
                        found = true;

                        String[] cmdArgs = cleanTokens.toArray(new String[0]);

                        ProcessBuilder pb = new ProcessBuilder(cmdArgs);

                        if (redirectFile != null) {
                            pb.redirectOutput(new File(redirectFile));
                            // DO NOT redirect error stream
                        }

                        Process process = pb.start();

                        if (redirectFile == null) {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                            }
                        }

                        // Always print stderr to terminal
                        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String err;
                        while ((err = errReader.readLine()) != null) {
                            System.out.println(err);
                        }

                        process.waitFor();
                        break;
                    }
                }

                if (!found) {
                    System.out.println(command + ": command not found");
                }
            }
        }

        scanner.close();
    }

}