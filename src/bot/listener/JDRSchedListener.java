package bot.listener;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import bot.controler.MessageManager;
import data.domain.Event;
import data.query.EventQuery;
import lombok.AllArgsConstructor;
import message.BotMessage;
import message.Command;
import message.Statics;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import scheduler.EventScheduler;
import service.DataUtils;

@AllArgsConstructor
public class JDRSchedListener extends ListenerAdapter {
	
	private Map<Guild, Map<MessageChannel, Event>> datas;
	private JDA jda;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		super.onMessageReceived(event);
		if(!event.getAuthor().equals(jda.getSelfUser())) {
			Message message = event.getMessage();
			Event botEvent = null;
			Command command = Command.findByCommand(message.getContentDisplay().split(" ")[0]);
			if(command != null) {
				switch (command) {
				case START:
					botEvent = getEvent(event);
					MessageManager.createMessages(event.getChannel(), botEvent);
					try {
						EventQuery.create(botEvent);
						EventScheduler.update(DataUtils.retriveAllEvents(jda));
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;
				case STOP:
					delete(event);
					break;
				case COOKER:
					botEvent = getEvent(event);
					displayCooker(botEvent);
					break;
				default:
					PrivateChannel privateChannel = event.getAuthor().openPrivateChannel().complete();
					privateChannel.sendMessage(BotMessage.MAUVAISE_COMMANDE);
					break;
				}
			}
		}
	}

	private Event getEvent(MessageReceivedEvent event) {
		Event botEvent = null;
		if(!datas.containsKey(event.getGuild())) {
			datas.put(event.getGuild(), new HashMap<>());
		}
		if(!datas.get(event.getGuild()).containsKey(event.getChannel())) {
			botEvent = new Event();
			datas.get(event.getGuild()).put(event.getChannel(), botEvent);
		}else {
			delete(event);
			botEvent = datas.get(event.getGuild()).get(event.getChannel());
		}
		return botEvent;
	}

	private void displayCooker(Event botEvent) {
		String message = botEvent.getHaveCooked().keySet().stream().map(user -> user + " a cuisiné " + botEvent.getHaveCooked().get(user) + "fois").collect(Collectors.joining( "\n" ));
		botEvent.getAnnonceDate().getChannel().sendMessage(message).complete();
		
	}

	private void delete(MessageReceivedEvent event) {
		try {
			DataUtils.removeEvent(event.getChannel().getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		datas.get(event.getGuild()).remove(event.getChannel());
	}
	
	@Override
	public void onGenericMessageReaction(GenericMessageReactionEvent event) {
		super.onGenericMessageReaction(event);
		if(!event.getUser().equals(jda.getSelfUser())) {
			Event botEvent = datas.get(event.getGuild()).get(event.getChannel());
			if(event.getMessageId().equals(botEvent.getAnnonceDate().getId())) {
				if(Statics.OUI.equals(event.getReactionEmote().getName())) {
					MessageManager.refreshMessageCooker(event.getChannel(), botEvent);
				}
			}
			if(event.getMessageId().equals(botEvent.getAnnonceCooker().getId())) {
				MessageManager.refreshMessageCooker(event.getChannel(), botEvent);
			}
		}
	}
}
