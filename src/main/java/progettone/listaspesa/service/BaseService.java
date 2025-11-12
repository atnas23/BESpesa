package progettone.listaspesa.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseService {

	private static final Logger LOGGER = LogManager.getLogger(BaseService.class);

	public void reflectionMapper(Object toBeSet, Object toBeGet) {

		if (toBeSet == null || toBeGet == null) {
			LOGGER.warn("Uno degli oggetti passati è null, impossibile mappare.");
			return;
		}

		List<Field> fieldsToSet = getAllFields(toBeSet.getClass());
		List<Field> fieldsToGet = getAllFields(toBeGet.getClass());

		for (Field fieldToSet : fieldsToSet) {
			for (Field fieldToGet : fieldsToGet) {

				if (fieldToSet.getName().equals(fieldToGet.getName())) {

					String name = fieldToSet.getName();
					String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1);

					String getterName = "get" + capitalized;
					if (fieldToGet.getType() == boolean.class || fieldToGet.getType() == Boolean.class) {
						if (!methodExists(toBeGet.getClass(), getterName)) {
							getterName = "is" + capitalized;
						}
					}
					String setterName = "set" + capitalized;

					try {
						Method getter = toBeGet.getClass().getMethod(getterName);
						Method setter = toBeSet.getClass().getMethod(setterName, fieldToSet.getType());

						try {
							Object value = getter.invoke(toBeGet);
							setter.invoke(toBeSet, value);
							LOGGER.debug("Campo mappato con successo: {}", name);
						} catch (IllegalAccessException | InvocationTargetException e) {
							LOGGER.info("Errore nel copiare campo '{}'", name, e.getMessage());
						}

					} catch (NoSuchMethodException e) {
						LOGGER.debug("Getter o setter non trovato per campo '{}'", name, e.getMessage());
					}
					break;

				}
			}
		}
	}

	private List<Field> getAllFields(Class<?> type) {
		List<Field> fields = new ArrayList<>();
		while (type != null) {
			fields.addAll(Arrays.asList(type.getDeclaredFields()));
			type = type.getSuperclass();
		}
		return fields;
	}

	private boolean methodExists(Class<?> clazz, String methodName) {
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(methodName)) {
				return true;
			}
		}
		return false;
	}
}
