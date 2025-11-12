package progettone.utils;

import java.time.LocalDate;
import java.util.Random;

import progettone.listaspesa.dto.UserDTO;

/**
 * Classe di utilità per simulare dati utente provenienti da una richiesta
 * esterna (es. API REST). Può generare utenti casuali o basati su parametri
 * specifici.
 */
public class ExternalUser {

	private static final String[] FIRST_NAMES = { "Mario", "Luigi", "Giulia", "Anna", "Marco", "Laura", "Davide",
			"Sara" };

	private static final String[] LAST_NAMES = { "Rossi", "Bianchi", "Verdi", "Neri", "Esposito", "Romano", "Colombo",
			"Galli" };
	
	private static final String[] PASSWORDS = { "Pass1234", "Qwerty2025", "Luca!567", "Anna2025", "Marco_890",
			"Laura99", "Davide#77", "Sara2025!" };

	private static final Random RANDOM = new Random();

	/**
	 * Genera un UserDTO con dati realistici e casuali, come se arrivasse da una
	 * chiamata API.
	 */
	public static UserDTO randomUser() {
		String firstName = random(FIRST_NAMES);
		String lastName = random(LAST_NAMES);
		String password = random(PASSWORDS);
		String email = (firstName + "." + lastName + "@example.com").toLowerCase();

		UserDTO dto = new UserDTO();
		dto.setPassword(password);
		dto.setEmail(email);
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		dto.setDateOfBirth(randomDateOfBirth());
		dto.setDeleted(false);

		return dto;
	}

	/**
	 * Crea un UserDTO con email personalizzata, utile per test mirati.
	 */
	public static UserDTO buildUser(String password, String email, String firstName, String lastName, LocalDate dob) {
		UserDTO dto = new UserDTO();
		dto.setPassword(password);
		dto.setEmail(email);
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		dto.setDateOfBirth(dob != null ? dob : LocalDate.of(1990, 1, 1));
		dto.setDeleted(false);
		return dto;
	}

	/*
	 * ================================================================ METODI DI
	 * SUPPORTO PRIVATI
	 * ================================================================
	 */

	private static String random(String[] array) {
		return array[RANDOM.nextInt(array.length)];
	}

	private static LocalDate randomDateOfBirth() {
		int year = 1970 + RANDOM.nextInt(30);
		int month = 1 + RANDOM.nextInt(12);
		int day = 1 + RANDOM.nextInt(25);
		return LocalDate.of(year, month, day);
	}
}