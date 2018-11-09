package scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.domain.Event;
import net.dv8tion.jda.core.JDA;


public class EventScheduler {
	// 14 Jours
	private final static int PERIOD = 7 * 24 * 60 * 60 * 1000 * 2;

	private static ScheduledExecutorService executor;
	

	public static void update(JDA jda, List<Event> events) {
		if (executor != null) {
			executor.shutdown();
		}
		executor = Executors.newScheduledThreadPool((int) (events.size()));
		for(Event event : events) {

			long initialDelay = event.getNextDate() - Calendar.getInstance().getTimeInMillis();
			
			executor.scheduleAtFixedRate(new EventRunnable(event.getAnnonceDate().getTextChannel(), event), initialDelay, PERIOD, TimeUnit.MILLISECONDS);
		}
	}
	public static Calendar getNextSchedul(Event jdrEvent) {
		Calendar schedul = Calendar.getInstance(Locale.FRANCE);
		schedul.setTime(new Date(jdrEvent.getNextDate()));
		while(Calendar.getInstance().getTimeInMillis() > schedul.getTimeInMillis() - 10000) {
			schedul.add(Calendar.WEEK_OF_YEAR, 2);
		}
		return schedul;
	}
}
