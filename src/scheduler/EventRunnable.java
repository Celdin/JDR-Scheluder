package scheduler;

import java.util.Optional;

import bot.controler.MessageManager;
import data.domain.Event;
import lombok.AllArgsConstructor;
import message.Statics;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import service.DataUtils;

@AllArgsConstructor
public class EventRunnable implements Runnable {
	TextChannel channel;
	Event event;

	@Override
	public void run() {
		Message message = channel.getMessageById(event.getAnnonceCooker().getId()).complete();
		Optional<User> oui = message.getReactions().stream().filter(reaction -> Statics.OUI.equals(reaction.getReactionEmote().getName())).findAny().get().getUsers().stream().filter(user -> !channel.getJDA().getSelfUser().equals(user)).findFirst();

		if(oui.isPresent()) {
			event.getHaveCooked().put(oui.get(), event.getHaveCooked().get(oui.get()) + 1);
		}
		event.setNextDate(EventScheduler.getNextSchedul(event).getTimeInMillis());
		MessageManager.createMessages(channel, event);
		DataUtils.save(event);
	}

}
