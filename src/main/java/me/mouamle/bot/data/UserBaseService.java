package me.mouamle.bot.data;

import me.mouamle.bot.game.objects.Player;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Component
public class UserBaseService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRED)
    public DBPlayer savePlayer(Player player) {
        DBPlayer dbPlayer = new DBPlayer();
        dbPlayer.setDisplayName(player.getDisplayName());
        dbPlayer.setUsername(player.getUsername());
        dbPlayer.setUserId(player.getUserId());

        em.persist(dbPlayer);
        return dbPlayer;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<Player> getPlayerByUserId(int userId) {
        TypedQuery<DBPlayer> query = em.createNamedQuery("Player.findByUserId", DBPlayer.class);
        query.setParameter("userId", userId);

        try {
            DBPlayer dbPlayer = query.getSingleResult();

            Player player = new Player();
            player.setDisplayName(dbPlayer.getDisplayName());
            player.setUsername(dbPlayer.getUsername());
            player.setUserId(dbPlayer.getUserId());

            return Optional.of(player);
        } catch (PersistenceException ex) {
            return Optional.empty();
        }
    }

}
