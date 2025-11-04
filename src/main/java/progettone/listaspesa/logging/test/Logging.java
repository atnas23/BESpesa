package progettone.listaspesa.logging.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {

	private static final Logger logger = LogManager.getLogger(Logging.class);

    public static void main(String[] args) {
        // Creazione della cartella log PRIMA di inizializzare Log4j2
        File logDir = new File("C:/Users/santa/Progetti/Progettone/log");
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (!created) {
                System.err.println("Impossibile creare la cartella log! Controlla i permessi.");
                return; // termina se non si può scrivere
            }
        }

        // Inizializzazione del logger
        Logger logger = LogManager.getLogger(Logging.class);

        // Log di test
        logger.info("Applicazione avviata");
        logger.warn("Questo è un warning");
        logger.error("Questo è un messaggio di errore");

        System.out.println("Controlla il file C:/Users/santa/Progetti/Progettone/log/app.log");
    }
}
