package service;

import static domain.RoleType.BUSINESS_MAN;
import static domain.RoleType.DELIVERY_MAN;
import static domain.RoleType.WAREHOUSE_MANAGER;

import connection.HikariCpDBConnectionUtil;
import dao.UserDao;
import domain.BusinessMan;
import domain.DeliveryMan;
import domain.Region;
import domain.User;
import dto.PasswordResetDto;
import dto.savedto.BusinessManSaveDto;
import dto.savedto.DeliveryManSaveDto;
import dto.savedto.WarehouseManagerSaveDto;
import dto.updatedto.BusinessManUpdateDto;
import dto.updatedto.DeliveryManUpdateDto;
import dto.updatedto.WarehouseManagerUpdateDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;
import security.SHA256;

public class UserService { //스프링 시큐리티의 UserDetails를 서비스에서 implements 함 ,

  private static final UserDao userDao = new UserDao(); //DI , 하지만 스프링 없으니 불가능 , OCP DIP 위배 ㅜㅜ
  private static final SHA256 sha256 = new SHA256();
  /**
   * -- 회원가입 검증 --
   * 1. 로그인 아이디 중복 아닌지 검증
   * 2. 비밀번호 , 비밀번호 재확인 검증
   */

  /**
   * User
   */

  public Integer businessManJoin(BusinessManSaveDto businessManSaveDto) { //SQLException은 어차피 처리 못해 db 에러이니 그냥 JVM까지 던지는 수밖에 없다. 오류 화면을 보여주거나 오류 api를 던지는 @ControllerAdvice의 @ExceptionHandler이 있는 것도 아니고
    Connection con = null;
    Integer saveId = null;
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
      String passwordQuestion = businessManSaveDto.getPasswordQuestion();
      String passwordAnswer = businessManSaveDto.getPasswordAnswer();
      validateBeforeJoin(loginEmail, password, rePassword);

      //비밀번호 암호화(SHA-256 알고리즘)
      String encryptPassword = sha256.getEncryptPassword(password);
      BusinessMan businessMan = new BusinessMan(name, phoneNumber, loginEmail, encryptPassword , BUSINESS_MAN , passwordQuestion , passwordAnswer , businessName, businessNum);
      saveId = userDao.save(businessMan, con);
      con.commit();
    } catch (SQLException e){
      rollback(con);
      throw new RuntimeException(e);
    } finally {
      closeConnection(con);
    }
    return saveId;

  }




  public Integer deliveryManJoin(DeliveryManSaveDto deliveryManSaveDto){
    Connection con = null;
    Integer saveId;
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
      String passwordQuestion = deliveryManSaveDto.getPasswordQuestion();
      String passwordAnswer = deliveryManSaveDto.getPasswordAnswer();
      Region region = deliveryManSaveDto.getRegion();
      validateBeforeJoin(loginEmail, password, rePassword);

      //비밀번호 암호화(SHA-256 알고리즘)
      String encryptPassword = sha256.getEncryptPassword(password);

      User user = new DeliveryMan(name, phoneNumber, loginEmail, encryptPassword , DELIVERY_MAN , passwordQuestion , passwordAnswer , deliveryManNum ,carNum , region);
      saveId = userDao.save(user, con);
      con.commit();
    }catch (SQLException e){
      rollback(con);
      throw new RuntimeException(e);
    } finally {
      closeConnection(con);
    }
    return saveId;
  }

  //솔직히 서비스도 SQLException 하기 싫은데 트랜잭션을 프록시가 아닌 서비스가 직접 시작하므로 어쩔 수 없이 예외 처리를 해줘야됨 , 그래서 프레젠테이션 계층만이라도 독립적이게 하기 위해
  public Integer warehouseManagerJoin(WarehouseManagerSaveDto warehouseManagerSaveDto){
    Connection con = null;
    Integer saveId = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);

      String name = warehouseManagerSaveDto.getName();
      String phoneNumber = warehouseManagerSaveDto.getPhoneNumber();
      String loginEmail = warehouseManagerSaveDto.getLoginEmail();
      String password = warehouseManagerSaveDto.getPassword();
      String rePassword = warehouseManagerSaveDto.getRePassword();
      String passwordQuestion = warehouseManagerSaveDto.getPasswordQuestion();
      String passwordAnswer = warehouseManagerSaveDto.getPasswordAnswer();
      validateBeforeJoin(loginEmail, password, rePassword);


      //비밀번호 암호화(SHA-256 알고리즘)
      String encryptPassword = sha256.getEncryptPassword(password);
      User user = new User(name, phoneNumber, loginEmail, encryptPassword , WAREHOUSE_MANAGER, passwordQuestion , passwordAnswer);
      saveId = userDao.save(user, con);
      con.commit();
    }catch (SQLException e){
      rollback(con);
      throw new RuntimeException(e);
    }
    finally {
      closeConnection(con);
    }
    return saveId;
  }


  //DTO 십년 때매 어쩔 수 없이 등록 , 수정 분리해줘야됨!

  public void updateWarehouseManager(Integer id , WarehouseManagerUpdateDto warehouseManagerUpdateDto){
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      User warehouseManager = findUser(id);
      warehouseManager.changeBasicInformation(warehouseManagerUpdateDto.getName(), warehouseManagerUpdateDto.getPhoneNumber());
      userDao.updateBasicInformation(warehouseManager, con);
      con.commit();
    }catch (SQLException e) {
      rollback(con);
      throw new RuntimeException(e);
    }
    finally {
      closeConnection(con);
    }

  }
  public void updateBusinessMan(Integer id , BusinessManUpdateDto businessManUpdateDto) {
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
      userDao.updateBasicInformation(businessMan, con);
      con.commit();
    }catch (SQLException e) {
      rollback(con);
      throw new RuntimeException(e);
    }
    finally {
      closeConnection(con);
    }
  }
  public void updateDeliveryMan(Integer id , DeliveryManUpdateDto deliveryManUpdateDto) {
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
      userDao.updateBasicInformation(deliveryMan, con);
      con.commit();
    }catch (SQLException e) {
      rollback(con);
      throw new RuntimeException(e);
    }
    finally {
      closeConnection(con);
    }
  }

  public User findUser(Integer id) {
    Connection con = null;
    User findUser = null;
    try {
      con = getConnection();
      con.setReadOnly(true);
      findUser = userDao.findById(id, con).orElseThrow(() -> new IllegalArgumentException("찾으려는 회원 정보가 존재하지 않습니다")); //컨트롤러에서 처리하게 할까
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
    return findUser;

  }


  /**
   * findByLoginEmail , findByLoginEmailAndPassword 서비스 내에서만
   */
  private Optional<User> findByLoginEmail(String loginEmail){
    Connection con = null;
    Optional<User> findUser;
    try {
      con = getConnection();
      con.setReadOnly(true);
      findUser = userDao.findAll(con).stream()
          .filter(user -> user.getLoginEmail().equals(loginEmail))
          .findFirst();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
    return findUser;
  }

  private Optional<User> findByLoginEmailAndPassword(String loginEmail , String password){
    Connection con = null;
    Optional<User> findUser;
    try {
      con = getConnection();
      con.setReadOnly(true);
      String encryptPassword = sha256.getEncryptPassword(password);
      findUser = userDao.findAll(con).stream().filter(
              user -> user.getLoginEmail().equals(loginEmail) && user.getPassword().equals(encryptPassword))
          .findFirst();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
    return findUser;
  }



  /**
   * 회원가입 전 검증 , 원래 앞단에서 입력받을 때 Bean Validation에서 검증해줘야하는데 쩔 수 없이 서비스에서 해주는 것일 뿐 그래서 해당 예외를 컨트롤러에서 잡아서 처리해줭도미
   */
  private void validateBeforeJoin(String loginEmail, String password, String rePassword) {
    //1. 이미 존재하는 아이디인지
    findByLoginEmail(loginEmail).ifPresent(user -> {
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
  public User login(String loginEmail , String password){
    Connection con = null;
    User findUser;
    try {
      con = getConnection();
      con.setReadOnly(true);
      //이미 권한 다 할당된 사용자
      findUser = findByLoginEmailAndPassword(loginEmail, password).orElseThrow(() -> new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다"));
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
    return findUser;
  }

  public void logout(User user){
    user = null;

  }


  /**
   * 아이디 찾기
   */
  public void checkLoginEmailExists(String name , String phoneNumber){
    Connection con = null;
    try {
      con = getConnection();
      con.setReadOnly(true);
      userDao.findAll(con).stream()
          .filter(user -> user.getName().equals(name) && user.getPhoneNumber().equals(phoneNumber))
          .findFirst().ifPresentOrElse(
              user -> System.out.printf("%s님 아이디 : %s\n", user.getName(),
                  user.getLoginEmail().replaceAll("(?<=.{2}).", "*")),
              () -> {
                throw new IllegalArgumentException("입력한 정보에 해당되는 아이디가 존재하지 않습니다.");
              });
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
  }


  /**
   * 비밀번호 재설정
   */
  public User checkBeforeResetPassword(PasswordResetDto passwordResetDto) {
    Connection con = null;
    User findUser = null;
    try {
      con = getConnection();
      con.setReadOnly(true);

      findUser = findByLoginEmail(passwordResetDto.getLoginEmail()).filter(user ->
          user.getName().equals(passwordResetDto.getName()) &&
              user.getPhoneNumber().equals(passwordResetDto.getPhoneNumber()) &&
              user.getPasswordQuestion().equals(passwordResetDto.getPasswordQuestion()) &&
              user.getPasswordAnswer().equals(passwordResetDto.getPasswordAnswer())
      ).orElseThrow(() -> new IllegalArgumentException("입력된 정보가 일치하지 않습니다."));
      findUser.changePassword(findUser.getPassword());
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
    return findUser;
  }

  //변경 : 애플리케이션에서 변경 + DB에서 변경 ==> JPA 쓰면 영속성 컨텍스트를 통하여 엔티티들을 관리해줌으로써 더티체킹을 통해 애플리케이션에서 바뀐 내용들을 자동으로 db에서도 바귀게 해줌
  public void resetPassword(String newPassword , String reNewPassword , User user){
    Connection con = null;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      if (newPassword.equals(reNewPassword)) {
        user.changePassword(newPassword);
        userDao.updatePassword(user , con); //SQLException
        con.commit();
      } else {
        throw new IllegalArgumentException("비밀번호 재확인이 필요합니다"); //이건 이제 컨트롤러에서 처리하는 거지
      }
    }catch (SQLException e){
      rollback(con);
      throw new RuntimeException(e);
    }
    finally {
      closeConnection(con);
    }
  }


  private static Connection getConnection(){
    return HikariCpDBConnectionUtil.getInstance().getConnection();
  }

  private static void rollback(Connection con) {
    try {
      if (con != null) {
        con.rollback(); //어차피 처리 못해 그냥 main에 던져서 JVM으로 가서 실패할 수밖에 없음
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
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
        throw new RuntimeException(e);
      }
    }
  }
}
