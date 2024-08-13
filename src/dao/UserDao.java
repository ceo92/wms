package dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import domain.User;
import org.junit.jupiter.api.Test;

/**
 * 추후 JDBC로 마이그레이션
 */



public class UserDao {

  private static final Map<Integer, User> store = new HashMap<>();
  private static Integer sequence = 0;

  public Integer save(User user , Connection con){
    store.put(user.getId(), user);
    return user.getId();
  }


  public Optional<User> findById(Integer id){
    return Optional.ofNullable(store.get(id));
  }

  public Optional<User> findByLoginEmail(String loginEmail , Connection con){
    return store.values().stream().filter(user -> user.getLoginEmail().equals(loginEmail)).findFirst();
  }

  public List<User> findAll(){
    return new ArrayList<>(store.values());
  }

  public void removeUser(User user){
    store.remove(user.getId(), user);
  }

}
