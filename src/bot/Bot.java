package bot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

public class Bot {
	private JDA jda;

	public Bot(String token) throws LoginException, InterruptedException {
		jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
		jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, "planifier"));
		System.out.println("Connecte avec: " + jda.getSelfUser().getName());
		int size = jda.getGuilds().size();
		System.out.println("Autorisé sur " + size + " serveur" + (size > 1 ? "s" : ""));
		for (Guild guild : jda.getGuilds()) {
			System.out.println("	 - " + guild.getName());
		}
	}
}
