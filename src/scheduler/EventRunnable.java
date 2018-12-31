package scheduler;

import java.sql.SQLException;
import java.util.Optional;

import bot.controler.MessageManager;
import data.domain.Event;
import data.query.CookerQuery;
import data.query.EventQuery;
import lombok.AllArgsConstructor;
import message.Statics;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

@AllArgsConstructor
public class EventRunnable implements Runnable {
	TextChannel channel;
	Event event;

	@Override
	public void run() {
		EventQuery eventQuery = new EventQuery();
		CookerQuery cookerQuery = new CookerQuery();
		Message message = channel.getMessageById(event.getAnnonceCooker().getId()).complete();
		Optional<User> oui = message.getReactions().stream().filter(reaction -> Statics.OUI.equals(reaction.getReactionEmote().getName())).findAny().get().getUsers().stream().filter(user -> !channel.getJDA().getSelfUser().equals(user)).findFirst();
		try {
			if(oui.isPresent()) {
				event.getHaveCooked().put(oui.get(), (event.getHaveCooked().containsKey(oui.get())?event.getHaveCooked().get(oui.get()):0) + 1);
				cookerQuery.save(eventQuery.getId(event), event);
			}
			event.setNextDate(EventScheduler.getNextSchedul(event).getTimeInMillis());
			MessageManager.createMessages(channel, event);
			eventQuery.save(event);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
