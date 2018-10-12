import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import bot.Bot;
import bot.domain.Data;
import bot.presistance.DataPersist;

public class JDRSched {
    public static void main(String[] args) throws Exception{
        if(args.length > 0) {
        	new DataPersist();
            new Bot(args[0]);
        } else {
            System.out.println("Vieulliez indiquer le token du bot");
        }
    }
}
