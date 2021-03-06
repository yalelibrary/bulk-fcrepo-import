package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.User;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface UserDAO extends GenericDAO<User, Integer> {

    List<User> findByEmail(String field);

    List<User> findByUsername(String field);

    String findUsernameByUserId(int userId);

    User findByUserId(int userId);

    List<String> getEmails();

    List<String> getUsernames();

}

