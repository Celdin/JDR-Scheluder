package bot.controler;

import static java.lang.String.format;
import static message.BotMessage.COOKERS;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import data.dataObject.CookerDataObject;
import data.domain.Event;
import data.query.CookerQuery;
import data.query.EventQuery;
import message.BotMessage;
import message.Statics;
import net.dv8tion.jda.core.entities.Guild;
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
		if (botEvent.getAnnonceDate() != null) {
			botEvent.getAnnonceDate().unpin().complete();
		}
		Message annonceDateMessage = channel.sendMessage(String.format(BotMessage.ANNONCE,
				DATE_FORMAT.format(calendar.getTime()), TIME_FORMAT.format(calendar.getTime()))).complete();
		annonceDateMessage.addReaction(Statics.OUI).complete();
		botEvent.setAnnonceDate(annonceDateMessage);
		Message annonceCookerMessage = channel.sendMessage(BotMessage.WHO_COOK).complete();
		annonceCookerMessage.addReaction(Statics.OUI).complete();
		annonceCookerMessage.addReaction(Statics.NON).complete();
		botEvent.setAnnonceCooker(annonceCookerMessage);
		annonceDateMessage.pin().complete();
	}

	public static void refreshMessageCooker(MessageChannel channel, Event botEvent) throws SQLException {
		Message messageCooker = channel.getMessageById(botEvent.getAnnonceCooker().getId()).complete();
		Message messageAnnonce = channel.getMessageById(botEvent.getAnnonceDate().getId()).complete();
		Guild guild = botEvent.getAnnonceCooker().getGuild();
		Optional<MessageReaction> traitre = messageAnnonce.getReactions().stream()
				.filter(reaction -> Statics.NON.equals(reaction.getReactionEmote().getName())).findAny();
		List<User> non = new ArrayList<>();
		if (traitre.isPresent()) {
			non = traitre.get().getUsers().stream().filter(user -> !channel.getJDA().getSelfUser().equals(user)).collect(Collectors.toList());
		} else {
			messageAnnonce.addReaction(Statics.NON).complete();
		}
		if (non.isEmpty()) {
			Optional<MessageReaction> elu = messageCooker.getReactions().stream()
					.filter(reaction -> Statics.OUI.equals(reaction.getReactionEmote().getName())).findAny();
			Optional<User> oui = Optional.empty();
			if (elu.isPresent()) {
				oui = elu.get().getUsers().stream().filter(user -> !channel.getJDA().getSelfUser().equals(user))
						.findFirst();
			} else {
				messageCooker.addReaction(Statics.OUI).complete();
			}

			if (oui.isPresent()) {
				messageCooker.editMessage(String.format(BotMessage.HE_COOK, getUsername(guild, oui.get()))).complete();
			} else {
				Map<Integer, List<User>> involved = new HashMap<>();
				List<User> coupables = new ArrayList<>(getInvolved(channel, botEvent));
				Optional<MessageReaction> indispo = messageCooker.getReactions().stream()
						.filter(reaction -> Statics.NON.equals(reaction.getReactionEmote().getName())).findFirst();
				if (indispo.isPresent()) {
					coupables.removeAll(indispo.get().getUsers().complete());
				} else {
					messageCooker.addReaction(Statics.NON).complete();
				}
				if (coupables.size() > 0) {
					for (User user : coupables) {
						if (botEvent.getHaveCooked().containsKey(user)) {
							if (!involved.containsKey(botEvent.getHaveCooked().get(user))) {
								involved.put(botEvent.getHaveCooked().get(user), new ArrayList<>());
							}
							involved.get(botEvent.getHaveCooked().get(user)).add(user);
						} else {
							if (!involved.containsKey(0)) {
								involved.put(0, new ArrayList<>());
							}
							involved.get(0).add(user);
						}
					}
					coupables = new ArrayList<>(involved.get(Collections.min(involved.keySet())));
					messageCooker.editMessage(BotMessage.WHO_COOK + "\n" + getUsername(guild, coupables.get(0)) + " ?")
							.complete();
				} else {
					messageCooker.editMessage(BotMessage.WHO_COOK).complete();
				}
			}
		} else {
			if(non.size() == 1) {
				messageCooker.editMessage(format(BotMessage.EST_PAS_LA, getUsername(guild, non.get(0)))).complete();
			} else {
				List<String> noms = non.stream().map(user -> getUsername(guild, user)).collect(Collectors.toList());
				String mecreants = String.join(", ", noms.subList(0, noms.size() - 1)) + " et " + noms.get(noms.size() - 1);
				messageCooker.editMessage(format(BotMessage.SONT_PAS_LA, mecreants)).complete();
			}
			messageCooker.clearReactions().complete();
		}
	}

	public static void displayCooker(Event botEvent) {
		Message annonceDate = botEvent.getAnnonceDate();
		String message = botEvent.getHaveCooked().keySet().stream()
				.sorted(Comparator.comparing(user -> botEvent.getHaveCooked().get(user)).reversed())
				.map(user -> format(COOKERS, getUsername(annonceDate.getGuild(), user),
						botEvent.getHaveCooked().get(user).toString()))
				.collect(Collectors.joining("\n"));
		annonceDate.getChannel().sendMessage(message).complete();

	}

	public static void displayTsof(Event botEvent) throws SQLException {
		Message annonceDate = botEvent.getAnnonceDate();
		Guild guild = annonceDate.getGuild();
		EventQuery eventQuery = new EventQuery();
		CookerQuery cookerQuery = new CookerQuery();
		List<CookerDataObject> cookerDataObject = cookerQuery.getCookerByEventId(eventQuery.getId(botEvent));
		String message = cookerDataObject.stream()
				.map(user -> getUsername(guild, guild.getJDA().getUserById(user.getUserId())))
				.collect(Collectors.joining("\n"));
		annonceDate.getChannel().sendMessage(message).complete();

	}

	public static String getUsername(Guild guild, User user) {
		String nickName = getNickname(guild, user);
		return nickName != null ? nickName : user.getName();
	}

	private static String getNickname(Guild guild, User user) {
		return guild.getMemberById(user.getId()).getNickname();
	}

	private static List<User> getInvolved(MessageChannel channel, Event event) throws SQLException {
		CookerQuery cookerQuery = new CookerQuery();
		EventQuery eventQuery = new EventQuery();
		List<CookerDataObject> cookers = cookerQuery.getCookerByEventId(eventQuery.getId(event));
		return cookers.stream().map(cooker -> channel.getJDA().getUserById(cooker.getUserId()))
				.collect(Collectors.toList());
	}
}
