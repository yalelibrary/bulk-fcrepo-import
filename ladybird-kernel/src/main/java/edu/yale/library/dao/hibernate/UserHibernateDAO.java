package edu.yale.library.dao.hibernate;


import edu.yale.library.beans.User;
import edu.yale.library.dao.UserDAO;

import java.util.Collections;

public class UserHibernateDAO extends GenericHibernateDAO<User, Integer> implements UserDAO
{

}