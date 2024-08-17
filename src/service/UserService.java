package service;

import static domain.RoleType.BUSINESS_MAN;
import static domain.RoleType.DELIVERY_MAN;

import dao.UserDao;
import domain.BusinessMan;
import domain.RoleType;
import dto.updatedto.BusinessManUpdateDto;
import dto.updatedto.DeliveryManUpdateDto;
import dto.updatedto.WarehouseManagerUpdateDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;
import connection.HikariCpDBConnectionUtil;
import domain.DeliveryMan;
import domain.User;
import domain.WarehouseManager;
import dto.savedto.DeliveryManSaveDto;
import dto.savedto.BusinessManSaveDto;
import dto.savedto.WarehouseManagerSaveDto;

public class UserService { //스프링 시큐리티의 UserDetails를 서비스에서 implements 함 ,

  private static final UserDao userDao = new UserDao(); //DI , 하지만 스프링 없으니 불가능 , OCP DIP 위배 ㅜㅜ

  /**
   * -- 회원가입 검증 --
   * 1. 로그인 아이디 중복 아닌지 검증
   * 2. 비밀번호 , 비밀번호 재확인 검증
   */

  /**
   * User
   */

  public Integer businessManJoin(BusinessManSaveDto businessManSaveDto)
      throws SQLException { //SQLException은 어차피 처리 못해 db 에러이니 그냥 JVM까지 던지는 수밖에 없다. 오류 화면을 보여주거나 오류 api를 던지는 @ControllerAdvice의 @ExceptionHandler이 있는 것도 아니고
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);

      String businessName = businessManSaveDto.getBusinessName();
      String businessNum = businessManSaveDto.getBusinessNum();
      String name = businessManSaveDto.getName();
      String phoneNumber = businessManSaveDto.getPhoneNumber();
      String loginEmail = businessManSaveDto.getLoginEmail();
      String password = businessManSaveDto.getPassword();
      String rePassword = businessManSaveDto.getRePassword();
      validateBeforeJoin(loginEmail, password, rePassword, con);

      User user = new DeliveryMan(businessName, businessNum, name, phoneNumber, loginEmail, password , BUSINESS_MAN);
      Integer saveId = userDao.save(user, con);
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




  public Integer deliveryManJoin(DeliveryManSaveDto deliveryManSaveDto) throws SQLException {
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);

      String deliveryManNum = deliveryManSaveDto.getDeliveryManNum();
      String carNum = deliveryManSaveDto.getCarNum();
      String name = deliveryManSaveDto.getName();
      String phoneNumber = deliveryManSaveDto.getPhoneNumber();
      String loginEmail = deliveryManSaveDto.getLoginEmail();
      String password = deliveryManSaveDto.getPassword();
      String rePassword = deliveryManSaveDto.getRePassword();
      validateBeforeJoin(loginEmail, password, rePassword ,con);

      User user = new DeliveryMan(deliveryManNum ,carNum , name, phoneNumber, loginEmail, password , DELIVERY_MAN);
      Integer saveId = userDao.save(user, con);
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

  public Integer warehouseManagerJoin(WarehouseManagerSaveDto warehouseManagerSaveDto) throws SQLException {
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);

      String name = warehouseManagerSaveDto.getName();
      String phoneNumber = warehouseManagerSaveDto.getPhoneNumber();
      String loginEmail = warehouseManagerSaveDto.getLoginEmail();
      String password = warehouseManagerSaveDto.getPassword();
      String rePassword = warehouseManagerSaveDto.getRePassword();
      validateBeforeJoin(loginEmail, password, rePassword ,con);

      User user = new WarehouseManager(name, phoneNumber, loginEmail, password);
      Integer saveId = userDao.save(user, con);
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


  //DTO 십년 때매 어쩔 수 없이 등록 , 수정 분리해줘야됨!

  public void updateWarehouseManager(Integer id , WarehouseManagerUpdateDto warehouseManagerUpdateDto)
      throws SQLException {
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      WarehouseManager warehouseManager = (WarehouseManager) findUser(id);
      warehouseManager.changeBasicInformation(
          warehouseManagerUpdateDto.getName(),
          warehouseManagerUpdateDto.getPhoneNumber()
      );
      userDao.update(warehouseManager, con);
      con.commit();
    }catch (SQLException e) {
      con.rollback();
      System.out.println("수정에 오류가 발생하였습니다");
    }
    finally {
      closeConnection(con);
    }

  }
  public void updateBusinessMan(Integer id , BusinessManUpdateDto businessManUpdateDto) throws SQLException {
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      BusinessMan businessMan = (BusinessMan) findUser(id);
      businessMan.changeBasicInformation(
          businessManUpdateDto.getName(),
          businessManUpdateDto.getPhoneNumber(),
          businessManUpdateDto.getBusinessNum(),
          businessManUpdateDto.getBusinessName()
      );
      userDao.update(businessMan, con);
      con.commit();
    }catch (SQLException e) {
      con.rollback();
      System.out.println("수정에 오류가 발생하였습니다");
    }
    finally {
      closeConnection(con);
    }
  }
  public void updateDeliveryMan(Integer id , DeliveryManUpdateDto deliveryManUpdateDto)
      throws SQLException {
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      DeliveryMan deliveryMan = (DeliveryMan) findUser(id);
      deliveryMan.changeBasicInformation(
          deliveryManUpdateDto.getName(),
          deliveryManUpdateDto.getPhoneNumber(),
          deliveryManUpdateDto.getDeliveryManNum(),
          deliveryManUpdateDto.getCarNum()
      );
      userDao.update(deliveryMan, con);
      con.commit();
    }catch (SQLException e) {
      con.rollback();
      System.out.println("수정에 오류가 발생하였습니다");
    }
    finally {
      closeConnection(con);
    }
  }

  public User findUser(Integer id) throws SQLException {
    Connection con = getConnection();
    con.setReadOnly(true);
    User findUser = userDao.findById(id, con)
        .orElseThrow(() -> new IllegalArgumentException("찾으려는 회원 정보가 존재하지 않습니다")); //컨트롤러에서 처리하게 할까
    con.setReadOnly(false);
    closeConnection(con);
    return findUser;

  }


  public User findByLoginEmail(String loginEmail) throws SQLException {
    Connection con = getConnection();
    con.setReadOnly(true);
    User findUser = userDao.findAll(con).stream()
        .filter(user -> user.getLoginEmail().equals(loginEmail))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("아이디가 일치하지 않습니다."));
    con.setReadOnly(false);
    closeConnection(con);
    return findUser;
  }

  public User findByLoginEmailAndPassword(String loginEmail , String password) throws SQLException {
    Connection con = getConnection();
    con.setReadOnly(true);
    User findUser = userDao.findAll(con).stream().filter(
            user -> user.getLoginEmail().equals(loginEmail) && user.getPassword().equals(password))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("아이디가 일치하지 않습니다."));
    con.setReadOnly(false);
    closeConnection(con);
    return findUser;
  }





  /**
   * 회원가입 전 검증
   */
  private void validateBeforeJoin(String loginEmail, String password, String rePassword , Connection con) {
    //1. 이미 존재하는 아이디인지
    userDao.findByLoginEmail(loginEmail , con).ifPresent(a -> {
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
    User findUser = userDao.findAll(con).stream()
        .filter(user -> user.getPassword().equals(loginEmail) && user.getLoginEmail().equals(password))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다."));
    closeConnection(con);
    return findUser;
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
