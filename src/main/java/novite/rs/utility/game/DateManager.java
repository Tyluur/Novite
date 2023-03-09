package novite.rs.utility.game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @since Dec 14, 2013
 */
public class DateManager {

	public void setTime() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Toronto"));
	}

	@SuppressWarnings("deprecation")
	public List<Date> getFirstWeekendOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDate());
		List<Date> weekendDates = new ArrayList<>();
		for (Date date : getDaysInMonth(calendar.getTime())) {
			if (weekendDates.size() == 2) {
				break;
			}
			int day = date.getDay();
			switch (day) {
			case 6:
			case 0:
				weekendDates.add(date);
				break;
			}
		}
		return weekendDates;
	}

	/**
	 * Calculates if we are currently in the first weekend of the month.
	 *
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean isFirstWeekendOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDate());
		int date = calendar.getTime().getDate();
		for (Date weekendDate : getFirstWeekendOfMonth()) {
			if (weekendDate.getDate() == date) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the string formatted time until the next double experience weekend
	 * 
	 * @return
	 */
	public String timeTillDXP() {
		List<Date> weekend = getFirstWeekendOfMonth();
		Date saturday = weekend.get(0);
		long saturdayMS = saturday.getTime();
		long currentTime = System.currentTimeMillis();
		long difference = saturdayMS - currentTime;
		if (difference < 0) {
			return "Passed...";
		}
		int seconds = (int) (difference / 1000) % 60;
		int minutes = (int) ((difference / (1000 * 60)) % 60);
		int hours = (int) ((difference / (1000 * 60 * 60)) % 24);
		long days = difference / (24 * 60 * 60 * 1000);
		return days + ":" + hours + ":" + minutes + ":" + seconds;
	}

	/**
	 * Gets the amount of days that are in a month
	 * 
	 * @param calendar
	 *            The calendar
	 * @return
	 */
	private int getDaysPerMonth(Calendar calendar) {
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * This method will create a blank list and populate it with every single
	 * date in the month. The list size is capped by
	 * {@link #getDaysPerMonth(Calendar)}.
	 *
	 * @param currentDate
	 *            The current date that is used to calculate the month and year.
	 * @return A {@code List<Date>} {@code Object}
	 */
	@SuppressWarnings("deprecation")
	private List<Date> getDaysInMonth(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		List<Date> dates = new ArrayList<Date>();
		for (int date = 1; date <= getDaysPerMonth(calendar); date++) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			c.set(Calendar.MONTH, calendar.getTime().getMonth());
			c.set(Calendar.DAY_OF_MONTH, date);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			dates.add(c.getTime());
		}
		return dates;
	}

	/**
	 * Gets the time in a read format until the next weekday
	 * 
	 * @return A {@code String} {@code Object}
	 */
	public String timeTillWeekday() {
		long diff = get().getNextWeekday().getTime() - get().getDate().getTime();
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000);
		long diffSeconds = diff / 1000 % 60;
		return diffHours + ":" + diffMinutes + ":" + diffSeconds;
	}

	/**
	 * Finds when the next weekday is
	 * 
	 * @return
	 */
	public Date getNextWeekday() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDate());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.FRIDAY) {
			calendar.add(Calendar.DATE, 3);
		} else if (dayOfWeek == Calendar.SATURDAY) {
			calendar.add(Calendar.DATE, 2);
		} else if (EXTENDED_WEEKEND && dayOfWeek == Calendar.MONDAY) {
			calendar.add(Calendar.DATE, 1);
		} else {
			calendar.add(Calendar.DATE, 1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public Date getDate() {
		Date date = new Date();
		return date;
	}

	private static final DateManager INSTANCE = new DateManager();

	public static DateManager get() {
		return INSTANCE;
	}

	/**
	 * If we are on an extended weekend experience.
	 */
	private static final boolean EXTENDED_WEEKEND = Boolean.TRUE;

}
