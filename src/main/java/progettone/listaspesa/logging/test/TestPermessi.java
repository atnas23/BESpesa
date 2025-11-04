package progettone.listaspesa.logging.test;

import java.io.FileWriter;
import java.io.IOException;

public class TestPermessi {
    public static void main(String[] args) {
        String path = "C:/Users/santa/Progetti/Progettone/log/test.txt";
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.write("Test di scrittura da Eclipse\n");
            System.out.println("✅ Scrittura riuscita in: " + path);
        } catch (IOException e) {
            System.err.println("❌ Scrittura fallita: " + e.getMessage());
        }
    }
}