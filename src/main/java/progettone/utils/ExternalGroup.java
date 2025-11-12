package progettone.utils;

import java.util.Random;

import progettone.listaspesa.dto.GroupDTO;

/**
 * Classe di utilità per simulare dati gruppo provenienti da una richiesta
 * esterna (es. API REST). Può generare gruppi casuali o basati su parametri
 * specifici, utile per test di integrazione o unit test.
 */
public class ExternalGroup {

	private static final String[] GROUP_NAMES = {
			"Famiglia Rossi", "Amici Università", "Colleghi Ufficio", "Viaggio Estivo",
			"Compleanno Anna", "Squadra Calcetto", "Vacanza Montagna", "Cena di Natale"
	};

	private static final String[] DESCRIPTIONS = {
			"Gruppo per condividere le spese di casa",
			"Spese universitarie e feste tra amici",
			"Organizzazione pranzi e regali in ufficio",
			"Gestione spese per il viaggio estivo",
			"Contributi e regali per la festa di compleanno",
			"Affitto campo e birre post partita",
			"Vacanza tra amici sulle Alpi",
			"Raccolta fondi per la cena aziendale di Natale"
	};

	private static final Random RANDOM = new Random();

	/**
	 * Genera un GroupDTO con dati realistici e casuali, come se arrivasse da una
	 * chiamata API.
	 */
	public static GroupDTO randomGroup() {
		String name = random(GROUP_NAMES);
		String description = random(DESCRIPTIONS);

		GroupDTO dto = new GroupDTO();
		dto.setName(name);
		dto.setDescription(description);

		dto.setDeleted(false);

		return dto;
	}

	/**
	 * Crea un GroupDTO con dati personalizzati, utile per test mirati o simulazioni
	 * specifiche.
	 */
	public static GroupDTO buildGroup(String name, String description, Long createdBy) {
		GroupDTO dto = new GroupDTO();
		dto.setName(name != null ? name : "Gruppo Generico");
		dto.setDescription(description != null ? description : "Descrizione non fornita");
		dto.setDeleted(false);
		return dto;
	}

	/*
	 * ================================================================
	 * METODI DI SUPPORTO PRIVATI
	 * ================================================================
	 */

	private static String random(String[] array) {
		return array[RANDOM.nextInt(array.length)];
	}
}

