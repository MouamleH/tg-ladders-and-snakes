package me.mouamle.bot.data;

import lombok.Data;
import me.mouamle.bot.util.Constants;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@NamedQueries({
        @NamedQuery(
                name = "Player.findByUserId",
                query = "SELECT p FROM DBPlayer p where p.userId = :userId"
        )
})
public class DBPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(updatable = false, nullable = false, unique = true)
    private int userId;

    private String username;

    @Column(length = Integer.MAX_VALUE)
    private String displayName;

    private long totalScore;
    private long totalGames;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    @PrePersist
    private void prePersist() {
        regDate = Calendar.getInstance(Constants.DEFAULT_TIMEZONE).getTime();
    }

}
