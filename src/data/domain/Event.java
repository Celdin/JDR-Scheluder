package data.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

@Data
public class Event {
	private List<User> involved = new ArrayList<>();
	private Map<User, Integer> haveCooked = new HashMap<>();
	private String occurence;
	private Message annonceDate;
	private Message annonceCooker;
}
