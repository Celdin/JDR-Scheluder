package scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.domain.Event;


public class EventScheduler {
	// 14 Jours
	private final static int PERIOD = 
			7 // DAY
			* 24 // TO HOURS
			* 60 // TO MINUTES
			* 60 // TO SENCONDS
			* 1000 // TO MILISECONDS
			* 2; // TWO WEEKS

	private static ScheduledExecutorService executor;
	

	public static void update(List<Event> events) {
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
		Calendar schedul = Calendar.getInstance();
		schedul.setTime(new Date(jdrEvent.getNextDate()));
		schedul.setTimeZone(TimeZone.getTimeZone("ECT"));
		while(Calendar.getInstance().getTimeInMillis() > schedul.getTimeInMillis() - 10000) {
			schedul.add(Calendar.WEEK_OF_YEAR, 2);
		}
		jdrEvent.setNextDate(schedul.getTimeInMillis());
		return schedul;
	}
}
