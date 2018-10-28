package bot.controler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import data.domain.Event;
import message.BotMessage;
import message.Statics;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageManager {

	public static void createMessages(MessageReceivedEvent event, Event botEvent) {
		Message annonceDateMessage = event.getChannel().sendMessage(String.format(BotMessage.ANNONCE, "Mercredi")).complete();
		annonceDateMessage.addReaction(Statics.OUI).complete();
		botEvent.setAnnonceDate(annonceDateMessage);
		Message annonceCookerMessage = event.getChannel().sendMessage(BotMessage.WHO_COOK).complete();
		annonceCookerMessage.addReaction(Statics.OUI).complete();
		annonceCookerMessage.addReaction(Statics.NON).complete();
		botEvent.setAnnonceCooker(annonceCookerMessage);
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
