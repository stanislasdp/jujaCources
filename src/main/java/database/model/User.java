package database.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = "actions")
@Entity
@Table(name = "userlog")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Action> actions = new ArrayList<>();

    public void addAction(Action action) {
        actions.add(action);
        action.setUser(this);
    }
}
