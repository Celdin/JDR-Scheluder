package bot;

import java.util.Map;

import javax.security.auth.login.LoginException;

import bot.listener.JDRSchedListener;
import data.domain.Event;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import service.DataUtils;

public class Bot {
	private JDA jda;

	public Bot(String token) throws LoginException, InterruptedException {
		jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).build();
		jda.awaitReady();
		jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, "planifier"));
		System.out.println("Connecte avec: " + jda.getSelfUser().getName());
		int size = jda.getGuilds().size();
		System.out.println("Autorisé sur " + size + " serveur" + (size > 1 ? "s" : ""));
		for (Guild guild : jda.getGuilds()) {
			System.out.println("	 - " + guild.getName());
		}
		Map<Guild, Map<MessageChannel, Event>> datas = DataUtils.retriveAll(jda);
		jda.addEventListener(new JDRSchedListener(datas, jda));
	}
}
