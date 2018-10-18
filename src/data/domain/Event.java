package data.domain;

import java.util.List;
import java.util.Map;

import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@Data
public class Event {
	private List<User> involved;
	private Map<User, Integer> haveCooked;
	private String occurence;
	private Message annonceDate;
	private Message annonceCooker;
}
