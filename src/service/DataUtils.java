package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.dataObject.CookerDataObject;
import data.dataObject.EventDataObject;
import data.domain.Event;
import data.query.CookerQuery;
import data.query.EventQuery;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class DataUtils {
	public static Map<Guild, Map<MessageChannel, Event>> retriveAll(JDA jda) {
		EventQuery eventQuery = new EventQuery();
		CookerQuery cookerQuery = new CookerQuery();
		
		Map<Guild, Map<MessageChannel, Event>> servers = new HashMap<>();
		for(Guild guild : jda.getGuilds()) {
			List<String> channelsId = new ArrayList<>();
			for(Channel channel : guild.getTextChannels()) {
				channelsId.add(channel.getId());
			}
			List<EventDataObject> eventDOs = eventQuery.getEventByChannelIds(channelsId);
			Map<MessageChannel, Event> channels = new HashMap<>();
			servers.put(guild, channels);
			for(EventDataObject eventDO : eventDOs) {
				TextChannel channel = guild.getTextChannelById(eventDO.getChannelId());
				Event event = new Event();
				event.setAnnonceCooker(channel.getMessageById(eventDO.getAnnonceCookerId()).complete());
				event.setAnnonceDate(channel.getMessageById(eventDO.getAnnonceDateId()).complete());
				Map<User, Integer> cookers = new HashMap<>();
				for(CookerDataObject cookerDO : cookerQuery.getCookerByEventId(eventDO.getId())) {
					cookers.put(jda.getUserById(cookerDO.getUserId()), cookerDO.getHaveCooked());
				}
				event.setHaveCooked(cookers);
				event.setNextDate(eventDO.getNextDate());
				channels.put(channel, event);
			}
		}
		return servers;
	}
	
	public static void removeEvent(String channelId) {
		EventQuery eventQuery = new EventQuery();
		CookerQuery cookerQuery = new CookerQuery();
		
		cookerQuery.deleteEvent(channelId);
		eventQuery.deleteEvent(channelId);
	}

	public static void save(Event event) {
		EventQuery eventQuery = new EventQuery();
		CookerQuery cookerQuery = new CookerQuery();
		
		cookerQuery.save(eventQuery.save(event), event);
	}
}
