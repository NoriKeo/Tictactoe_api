package tmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Commander {

    public static void main(String[] args) {
        Commander commander = new Commander();
        commander.runShellCommand("colima start");
        commander.runShellCommand("docker restart some-postgres");
    }

    public void runShellCommand(String command) {
        try {
            System.out.println("\n>> run: " + command);

            String[] cmd = {"/bin/zsh", "-l", "-c", command};

            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Bye Bye ^^ " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
