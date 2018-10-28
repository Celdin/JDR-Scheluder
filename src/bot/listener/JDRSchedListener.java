package bot.listener;

import java.util.HashMap;
import java.util.Map;

import bot.controler.MessageManager;
import data.domain.Event;
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
			switch (Command.findByCommand(message.getContentDisplay().split(" ")[0])) {
			case START:
				Event botEvent = null;
				if(!datas.containsKey(event.getGuild())) {
					datas.put(event.getGuild(), new HashMap<>());
				}
				if(!datas.get(event.getGuild()).containsKey(event.getChannel())) {
					botEvent = new Event();
					datas.get(event.getGuild()).put(event.getChannel(), botEvent);
				}else {
					botEvent = datas.get(event.getGuild()).get(event.getChannel());
				}
				MessageManager.createMessages(event.getChannel(), botEvent);
				break;
			case STOP:
				DataUtils.removeEvent(event.getChannel().getId());
				datas.get(event.getGuild()).remove(event.getChannel());
				break;
			default:
				PrivateChannel privateChannel = event.getAuthor().openPrivateChannel().complete();
				privateChannel.sendMessage(BotMessage.MAUVAISE_COMMANDE);
				break;
			}
		}
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
