package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;
import connection.HikariCpDBConnectionUtil;
import domain.DeliveryMan;
import domain.User;
import domain.WarehouseManager;
import dto.DeliveryManDto;
import dto.BusinessManDto;
import dto.WarehouseManagerDto;
import dao.UserDao;

public class UserService { //스프링 시큐리티의 UserDetails를 서비스에서 implements 함 ,

  private static final UserDao USER_DAO = new UserDao(); //DI , 하지만 스프링 없으니 불가능 , OCP DIP 위배 ㅜㅜ

  /**
   * -- 회원가입 검증 --
   * 1. 로그인 아이디 중복 아닌지 검증
   * 2. 비밀번호 , 비밀번호 재확인 검증
   */

  /**
   * User
   */

  public Integer businessManJoin(BusinessManDto businessManDto)
      throws SQLException { //SQLException은 어차피 처리 못해 db 에러이니 그냥 JVM까지 던지는 수밖에 없다. 오류 화면을 보여주거나 오류 api를 던지는 @ControllerAdvice의 @ExceptionHandler이 있는 것도 아니고
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);

      String businessName = businessManDto.getBusinessName();
      String businessNum = businessManDto.getBusinessNum();
      String name = businessManDto.getName();
      String phoneNumber = businessManDto.getPhoneNumber();
      String loginEmail = businessManDto.getLoginEmail();
      String password = businessManDto.getPassword();
      String rePassword = businessManDto.getRePassword();
      validateBeforeJoin(loginEmail, password, rePassword, con);

      User user = new DeliveryMan(businessName, businessNum, name, phoneNumber, loginEmail,
          password);
      Integer saveId = USER_DAO.save(user, con);
      con.commit();
      return saveId;
    } catch (IllegalArgumentException e) {
      System.out.println();
      System.out.println("=====ERROR=====");
      System.out.println(e.getMessage());
      System.out.println();
      con.rollback(); //어차피 처리 못해 그냥 main에 던져서 JVM으로 가서 실패할 수밖에 없음
    } finally {
      closeConnection(con);
    }
    return null;

  }




  public Integer deliveryManJoin(DeliveryManDto deliveryManDto) throws SQLException {
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);

      String deliveryManNum = deliveryManDto.getDeliveryManNum();
      String carNum = deliveryManDto.getCarNum();
      String name = deliveryManDto.getName();
      String phoneNumber = deliveryManDto.getPhoneNumber();
      String loginEmail = deliveryManDto.getLoginEmail();
      String password = deliveryManDto.getPassword();
      String rePassword = deliveryManDto.getRePassword();
      validateBeforeJoin(loginEmail, password, rePassword ,con);

      User user = new DeliveryMan(deliveryManNum ,carNum , name, phoneNumber, loginEmail, password);
      Integer saveId = USER_DAO.save(user, con);
      con.commit();
      return saveId;
    }catch (IllegalArgumentException e){
      System.out.println();
      System.out.println("=====ERROR=====");
      System.out.println(e.getMessage());
      System.out.println();
      con.rollback();
    } finally {
      closeConnection(con);
    }
    return null;
  }

  public Integer warehouseManagerJoin(WarehouseManagerDto warehouseManagerDto) throws SQLException {
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);

      String name = warehouseManagerDto.getName();
      String phoneNumber = warehouseManagerDto.getPhoneNumber();
      String loginEmail = warehouseManagerDto.getLoginEmail();
      String password = warehouseManagerDto.getPassword();
      String rePassword = warehouseManagerDto.getRePassword();
      validateBeforeJoin(loginEmail, password, rePassword ,con);

      User user = new WarehouseManager(name, phoneNumber, loginEmail, password);
      Integer saveId = USER_DAO.save(user, con);
      con.commit();
      return saveId;
    }catch (IllegalArgumentException e){
      System.out.println();
      System.out.println("=====ERROR=====");
      System.out.println(e.getMessage());
      System.out.println();
      con.rollback();
    }
    finally {
      closeConnection(con);
    }
    return null;
  }


  public User findUser(Integer id){
    return USER_DAO.findById(id).orElse(null);
  }

  /**
   * 회원가입 전 검증
   */
  private static void validateBeforeJoin(String loginEmail, String password, String rePassword , Connection con) {
    //1. 이미 존재하는 아이디인지
    USER_DAO.findByLoginEmail(loginEmail , con).ifPresent(a -> {
      throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
    });

    //2.이메일 형식 검증
    if (!Pattern.matches("^[a-z0-9A-Z._-]*@[a-z0-9A-Z]*.[a-zA-Z.]*$", loginEmail)){
      throw new IllegalArgumentException("이메일 형식을 다시 한 번 확인해주세요 ");
    }

    //3. 8자리 이상비밀번호 영문 , 특수문자 , 숫자 검증
    if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$" ,
        password)){
      throw new IllegalArgumentException("비밀번호는 특수문자 , 영문 , 숫자의 조합이어야합니다.");
    }

    // 4. 비밀번호 더블체크
    if (!password.equals(rePassword)) {
      throw new IllegalArgumentException("비밀번호를 다시 한 번 확인해주세요");
    }
  }

  /**
   * -- 로그인 검증 --
   * 1. 로그인 아이디 일치하는지
   * 2. 로그인 아이디 일치하면 비밀번호 일치한지
   */
  public User login(String loginEmail , String password) throws SQLException {
    Connection con = getConnection();
    con.setReadOnly(true);
    //이미 권한 다 할당된 사용자
    User user = USER_DAO.findByLoginEmail(loginEmail, con)
        .filter(u -> u.getPassword().equals(password))
        .orElseThrow(() -> new IllegalArgumentException("로그인 아이디 혹은 비밀번호를 다시 한 번 확인해주세요"));
    closeConnection(con);
    return user;

  }

  public void logout(User user){
    user = null;
  }

  public void validateIsLogin(Integer id){
    User user = findUser(id);

  }


  private static Connection getConnection(){
    return HikariCpDBConnectionUtil.getConnection();
  }

  private static void closeConnection(Connection con){
    if (con != null) {
      try {
        con.setAutoCommit(true); //종료 시에는 자동 커밋 모드로 ! , 커넥션 풀 쓸 경우
        if (con.isReadOnly()){
          con.setReadOnly(false);
        }
        con.close(); //Connection 닫기
      } catch (SQLException e) {
        System.out.println("error = " + e.getMessage());
      }
    }
  }
}
