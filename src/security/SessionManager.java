package security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

  //세션 : getSession

  //DB에 추후 연동 , 세션 테이블로서?
  Map<String, Object> sessionStore = new ConcurrentHashMap<>();

  public static final String SESSION_COOKIE_NAME = "mySessionId";


  public void createSession(){
    String sessionId = UUID.randomUUID().toString();

  }



}
