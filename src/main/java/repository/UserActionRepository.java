package repository;

import database.model.User;

public interface UserActionRepository {

    void save(User user);

}
