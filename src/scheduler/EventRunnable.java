package scheduler;

import java.sql.SQLException;
import java.util.Optional;
import java.util.OptionalDouble;

import bot.controler.MessageManager;
import data.domain.Event;
import data.query.CookerQuery;
import data.query.EventQuery;
import lombok.AllArgsConstructor;
import message.Statics;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
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
		Optional<MessageReaction> hasOui = message.getReactions().stream()
				.filter(reaction -> Statics.OUI.equals(reaction.getReactionEmote().getName())).findAny();
		try {
			if (hasOui.isPresent()) {
				Optional<User> oui = hasOui.get().getUsers().stream()
						.filter(user -> !channel.getJDA().getSelfUser().equals(user)).findFirst();

				if (oui.isPresent()) {
					if (event.getHaveCooked().containsKey(oui.get())) {
						event.getHaveCooked().put(oui.get(), event.getHaveCooked().get(oui.get()) + 1);
					} else {
						OptionalDouble optionalAverage = event.getHaveCooked().values().stream()
								.mapToInt(Integer::intValue).average();
						if (optionalAverage.isPresent()) {
							event.getHaveCooked().put(oui.get(), (int) optionalAverage.getAsDouble());
						} else {
							event.getHaveCooked().put(oui.get(), 1);
						}
					}
					cookerQuery.save(eventQuery.getId(event), event);
				}
			}
			event.setNextDate(EventScheduler.getNextSchedul(event).getTimeInMillis());
			MessageManager.createMessages(channel, event);
			eventQuery.save(event);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
