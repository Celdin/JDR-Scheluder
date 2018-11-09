package bot.controler;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import data.domain.Event;
import message.BotMessage;
import message.Statics;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import scheduler.EventScheduler;

public class MessageManager {
    private final static DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.FULL, Locale.FRANCE);
    private final static DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.FRANCE);

	public static void createMessages(MessageChannel channel, Event botEvent) {
		Calendar calendar = EventScheduler.getNextSchedul(botEvent);
		if(botEvent.getAnnonceDate()!=null) {
			botEvent.getAnnonceDate().unpin().complete();
		}
		Message annonceDateMessage = channel.sendMessage(String.format(BotMessage.ANNONCE, 
                DATE_FORMAT.format(calendar.getTime()),
                TIME_FORMAT.format(calendar.getTime()))).complete();
		annonceDateMessage.addReaction(Statics.OUI).complete();
		botEvent.setAnnonceDate(annonceDateMessage);
		Message annonceCookerMessage = channel.sendMessage(BotMessage.WHO_COOK).complete();
		annonceCookerMessage.addReaction(Statics.OUI).complete();
		annonceCookerMessage.addReaction(Statics.NON).complete();
		botEvent.setAnnonceCooker(annonceCookerMessage);
		annonceDateMessage.pin().complete();
	}
	
	public static void refreshMessageCooker(MessageChannel channel, Event botEvent) {
		Message message = channel.getMessageById(botEvent.getAnnonceCooker().getId()).complete();
		Optional<User> oui = message.getReactions().stream().filter(reaction -> Statics.OUI.equals(reaction.getReactionEmote().getName())).findAny().get().getUsers().stream().filter(user -> !channel.getJDA().getSelfUser().equals(user)).findFirst();

		if(oui.isPresent()) {
			message.editMessage(String.format(BotMessage.HE_COOK, oui.get().getName())).complete();
		} else {
			Map<Integer, List<User>> involved = new HashMap<>();
			List<User> coupables = new ArrayList<>(getInvolved(channel.getMessageById(botEvent.getAnnonceDate().getId()).complete()));
			coupables.removeAll(message.getReactions().stream().filter(reaction -> Statics.NON.equals(reaction.getReactionEmote().getName())).findFirst().get().getUsers().complete());
			if(coupables.size() > 0) {
				for(User user : coupables) {
					if(botEvent.getHaveCooked().containsKey(user)) {
						if(!involved.containsKey(botEvent.getHaveCooked().get(user))) {
							involved.put(botEvent.getHaveCooked().get(user), new ArrayList<>());
						}
						involved.get(botEvent.getHaveCooked().get(user)).add(user);
					} else {
						if(!involved.containsKey(0)) {
							involved.put(0, new ArrayList<>());
						}
						involved.get(0).add(user);
					}
				}
				coupables = new ArrayList<>(involved.get(Collections.min(involved.keySet())));
				Collections.shuffle(coupables);
				message.editMessage(BotMessage.WHO_COOK + "\n" + coupables.get(0).getName() + " ?").complete(); 
			} else {
				message.editMessage(BotMessage.WHO_COOK).complete(); 
			}
		}
	}

	private static List<User> getInvolved(Message message) {
		Optional<MessageReaction> oui = message.getReactions().stream()
			.filter(reaction -> Statics.OUI.equals(reaction.getReactionEmote().getName()))
			.findFirst();
		
		if(oui.isPresent())
			return oui.get().getUsers().complete();
		return new ArrayList<>();
	}
}
