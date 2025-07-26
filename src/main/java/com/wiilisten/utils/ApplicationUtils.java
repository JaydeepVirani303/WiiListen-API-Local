package com.wiilisten.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

public final class ApplicationUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationUtils.class);

	/**
	 * This method <code>dateToString</code> is used for convert the Date object to
	 * String Date
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(final Date date, final String format) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		String stringDate = null;

		try {

			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			stringDate = sdf.format(date);

		} catch (final Exception e) {
			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}

		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return stringDate;
	}

	/**
	 * Used to convert LocalDate to formatted string
	 *
	 * @param localDate
	 * @param pattern
	 * @return
	 */
	public static String formatLocalDate(final LocalDate localDate,
			final String pattern) {
		String date = null;
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			date = localDate.format(DateTimeFormatter.ofPattern(pattern));
		} catch (final Exception e) {
			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}
		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return date;
	}

	/**
	 * Used to convert LocalDateTime to formatted String
	 *
	 * @param localDateTime
	 * @param pattern
	 * @return
	 */
	public static String formatLocalDateTime(final LocalDateTime localDateTime,
			final String pattern) {
		String dateTime = null;
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			dateTime = localDateTime.format(DateTimeFormatter.ofPattern(pattern));
		} catch (final Exception e) {
			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}
		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return dateTime;
	}

	/**
	 * Used to convert LocalTime to formatted string
	 *
	 * @param localTime
	 * @param pattern
	 * @return
	 */
	public static String formatLocalTime(final LocalTime localTime,
			final String pattern) {
		String time = null;
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			time = localTime.format(DateTimeFormatter.ofPattern(pattern));
		} catch (final Exception e) {
			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}
		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return time;

	}

	/**
	 * This <code>isEmpty</code> method is responsible to check whether the
	 * Collection value passed is empty or not.
	 *
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isEmpty(final Double value) {
		return value == null;
	}

	public static boolean isEmpty(final Boolean value) {
		return value == null;
	}
	/**
	 * This <code>isEmpty</code> method is responsible to check whether the String
	 * value passed is empty or not.
	 *
	 * @param param
	 * @return
	 */
	public static boolean isEmpty(final Long param) {
		return param == null || param.longValue() <= 0;
	}

	public static boolean isEmpty(final Page<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * This <code>isEmpty</code> method is responsible to check whether the String
	 * value passed is empty or not.
	 *
	 * @param param
	 * @return
	 */
	public static boolean isEmpty(final String param) {

		return param == null || param.trim().length() <= 0;
	}

	public static Timestamp localDateTimeToTimestamp(final LocalDateTime localDateTime) {
		Timestamp timestamp = null;
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			timestamp = Timestamp.valueOf(localDateTime);
		} catch (final Exception e) {
			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}
		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return timestamp;
	}

	/**
	 * This Method is used to covert string to LocalDate Object
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate StringToLocalDate(final String date, final String pattern) {
		LocalDate localDate = null;
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
		} catch (final Exception e) {
			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		return localDate;
	}

	/**
	 * This Method is used to covert string(10:00 AM) to LocalTime Object
	 *
	 * @param time
	 * @return
	 */
	public static LocalTime StringToLocalTime(final String time, final String pattern) {
		LocalTime localTime = null;
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern));
		} catch (final Exception e) {
			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		return localTime;
	}

	private ApplicationUtils() {

	}
}
