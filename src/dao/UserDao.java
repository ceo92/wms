package dao;

import static domain.RoleType.BUSINESS_MAN;
import static domain.RoleType.DELIVERY_MAN;

import domain.Admin;
import domain.BusinessMan;
import domain.DeliveryMan;
import domain.RoleType;
import domain.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 추후 JDBC로 마이그레이션
 */



public class UserDao {


  /**
   * 둘 다 해보자 PreparedStatement 하나랑 두 개일 때 둘 다 해보자 !
   */
  public Integer save(User user , Connection con) throws SQLException {
    //PreparedStatement는 각각의 쿼리에 지정해줘야됨
    String superTableSql = "insert into user(name , phone_number , login_email , password , role_type) values(? , ? , ? , ? , ?)";
    String subTableSql = null;
    PreparedStatement superTablePstmt = null;
    PreparedStatement subTablePstmt = null;

    superTablePstmt = con.prepareStatement(superTableSql , Statement.RETURN_GENERATED_KEYS);
    superTablePstmt.setString(1, user.getName()); //인덱스 1로 해서 1번필드에는 memberid 지정
    superTablePstmt.setString(2, user.getPhoneNumber()); //인덱스 1로 해서 1번필드에는 memberid 지정
    superTablePstmt.setString(3, user.getLoginEmail()); //인덱스 1로 해서 1번필드에는 memberid 지정
    superTablePstmt.setString(4, user.getPassword()); //인덱스 1로 해서 1번필드에는 memberid 지정
    superTablePstmt.setString(5, user.getRoleType().name()); //인덱스 1로 해서 1번필드에는 memberid 지정
    superTablePstmt.executeUpdate();
    ResultSet rs = superTablePstmt.getGeneratedKeys();
    int generatedId = 0;
    if (rs.next()){
      generatedId = rs.getInt(1); //첫번째 할당된 PK
    }
    //WAREHOUSE_MANAGER일 때는 추가적으로
    if (user instanceof DeliveryMan deliveryMan){
      subTableSql = "insert into delivery_man values(? ,? , ?)";
      subTablePstmt = con.prepareStatement(subTableSql);

      subTablePstmt.setInt(1 , generatedId); //슈퍼타입테이블의 Generated된 PK 값 할당
      subTablePstmt.setString(2 , deliveryMan.getDeliveryManNum());
      subTablePstmt.setString(3 , deliveryMan.getCarNum());
      subTablePstmt.executeUpdate();
    }
    else if (user instanceof BusinessMan businessMan){
      subTableSql = "insert into business_man values(? , ? , ?)";
      subTablePstmt = con.prepareStatement(subTableSql); //이렇게 하면 User과 식별관계인 BusinessMan테이블에 User의 PK 값이 자동으로 삽입됨

      subTablePstmt.setInt(1 , generatedId); //슈퍼타입테이블의  Generated된 PK 값 할당
      subTablePstmt.setString(2 , businessMan.getBusinessName());
      subTablePstmt.setString(3 , businessMan.getBusinessName());
      subTablePstmt.executeUpdate();
    }

    //커넥션 연결 => 쿼리 요청 역순으로 close
    close(subTablePstmt, null);
    close(superTablePstmt, rs);
    return generatedId;
  }


  public Optional<User> findById(Integer id , Connection con) throws SQLException {
    StringBuilder sql = new StringBuilder("select * from user where id = ?");
    PreparedStatement superTablePstmt = null;
    PreparedStatement subTablePstmt = null;

    ResultSet superTableRs = null;
    ResultSet subTableRs = null;
    String name = null, phoneNumber = null, loginEmail = null , password = null;
    RoleType roleType = null;

    try {
      superTablePstmt = con.prepareStatement(sql.toString());
      superTablePstmt.setInt(1 , id);
      superTableRs = superTablePstmt.executeQuery();
      if (superTableRs.next()) {
        name = superTableRs.getString("name");
        phoneNumber = superTableRs.getString("phone_number");
        loginEmail = superTableRs.getString("login_email");
        password = superTableRs.getString("password");
        roleType = RoleType.valueOf(superTableRs.getString("role_type"));
      }

      if (roleType == BUSINESS_MAN){
        sql.replace(15 , 19 , "business_man");
        subTablePstmt = con.prepareStatement(sql.toString());
        subTablePstmt.setInt(1, id);
        subTableRs = subTablePstmt.executeQuery();
        if (subTableRs.next()) {
          BusinessMan businessMan = new BusinessMan(id , name , phoneNumber , loginEmail , password , roleType ,
              subTableRs.getString("business_num") ,subTableRs.getString("business_name"));
          return Optional.of(businessMan);
        }
      } else if (roleType == DELIVERY_MAN){
        sql.replace(15 , 19 , "delivery_man");
        subTablePstmt = con.prepareStatement(sql.toString());
        subTablePstmt.setInt(1, id);
        subTableRs = subTablePstmt.executeQuery();
        if (subTableRs.next()) {
          DeliveryMan deliveryMan = new DeliveryMan(id, name, phoneNumber, loginEmail, password , roleType,
              subTableRs.getString("delivery_man_num") , subTableRs.getString("car_num"));
          return Optional.of(deliveryMan);
        }
      }

    }catch (Exception e){
      throw e;
    }finally {
      close(subTablePstmt , subTableRs);
      close(superTablePstmt , superTableRs);
    }
    //BusinessMan , DeliveryMan도 아니면 ADMIN 혹은 WAREHOUSE_MANAGER이므로 해당 데이터를 던져주면 됨 ㅇㅇ 이게 사실은 else문이 되게 됨
    return Optional.of(new User(id, name, phoneNumber, loginEmail, password, roleType));
  }

  /*public Optional<User> findById(Integer id , RoleType roleType , Connection con) throws SQLException {
    String sql = "select * from user ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    //id , name , phoneNumber , loginEmail , password , roleType , deliveryManNum , carNum
    try {
      if (roleType == DELIVERY_MAN) {
        sql += "join delivery_man on user.id = delivery_man.id and user.id = ?"; //
        pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();
        if (rs.next()) {
          DeliveryMan deliveryMan = new DeliveryMan(
              rs.getObject("user_id", Integer.class),
              rs.getString("name"),
              rs.getString("phone_number"),
              rs.getString("login_email"),
              rs.getString("password"),
              roleType,
              rs.getString("delivery_man_num"),
              rs.getString("car_num")
          );
          return Optional.of(deliveryMan);
        }
      }
      //1. 조인 조회 => ResultSet하나하나 꺼내서 User 및 DeliveryMan 할당
      //2. 조회 쿼리 각 테이블에 두 번 => 각 테이블에서 꺼내진 데이터들 바탕으로 User

      else if (roleType == BUSINESS_MAN) {
        sql += "join business_man on user.id = business_man.id and user.id = ?";
        pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();
        if (rs.next()) {
          BusinessMan businessMan = new BusinessMan(
              rs.getObject("user_id", Integer.class),
              rs.getString("name"),
              rs.getString("phone_number"),
              rs.getString("login_email"),
              rs.getString("password"),
              roleType,
              rs.getString("business_num"),
              rs.getString("business_name")
          );
          return Optional.of(businessMan);

        }
      } else { //ADMIN , WAREHOUSE_MANAGER
        sql += "user.id = ?";
        pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();
        User user = new User(
            rs.getObject("user_id", Integer.class),
            rs.getString("name"),
            rs.getString("phone_number"),
            rs.getString("login_email"),
            rs.getString("password"),
            roleType
        );
        return Optional.of(user);
      }


    }catch (SQLException e){
      throw e;
    }
    finally {
      close(pstmt, rs);

    }
    return Optional.empty();
  }*/


  /**
   * 권한 별로 조회하기
   */

  //어차피 user의 id랑 사업자의 id랑 똑같을 거 아니야 ??비식별 관계니까
  public List<User> findAllByRoleType(RoleType roleType , Connection con) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("select ").append("* ").append("from ").append("user u ");
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<User> users = new ArrayList<>();
    try {
      if (roleType == BUSINESS_MAN) {
        sql.append("join ").append("business_man b").append("and u.id = b.id");
        pstmt = con.prepareStatement(sql.toString());
        rs = pstmt.executeQuery();
        while (rs.next()) {
          BusinessMan businessMan = new BusinessMan(rs.getInt("id"), rs.getString("name")
              , rs.getString("phone_number"), rs.getString("login_email"), rs.getString("password"),
              , roleType, rs.getString("business_num"), rs.getString("business_name")
          );
          users.add(businessMan);
        }
      } else if (roleType == DELIVERY_MAN) {
        sql.append("join ").append("delivery_man d").append("and u.id = d.id");
        pstmt = con.prepareStatement(sql.toString());
        rs = pstmt.executeQuery();
        while (rs.next()) {
          DeliveryMan deliveryMan = new DeliveryMan(rs.getInt("id"), rs.getString("name")
              , rs.getString("phone_number"), rs.getString("login_email"), rs.getString("password"),
              roleType, rs.getString("delivery_man_num"), rs.getString("car_num")
          );
          users.add(deliveryMan);
        }
      } else {
        pstmt = con.prepareStatement(sql.toString());
        rs = pstmt.executeQuery();
        while (rs.next()) {
          User user = new User(rs.getInt("id"), rs.getString("name")
              , rs.getString("phone_number"), rs.getString("login_email"),
              rs.getString("password"), roleType);
          if (user instanceof Admin)continue;
          users.add(user);
        }
      }

      return users;
    }catch (SQLException e){
      throw e;
    }finally {
      close(pstmt , rs);
    }
  }


  public List<User> findAll(Connection con) throws SQLException {
    StringBuilder superTableSql = new StringBuilder("select * from user u");
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<User> users = new ArrayList<>();
    try{
      pstmt = con.prepareStatement(superTableSql.toString());
      rs = pstmt.executeQuery();
      while (rs.next()){
        User user = new User(rs.getInt("id") , rs.getString("name") ,  rs.getString("phone_number") ,
            rs.getString("login_email") , rs.getString("password") , RoleType.valueOf(rs.getString("role_type")));
        if (user.getRoleType() == BUSINESS_MAN){
          BusinessMan businessMan = new BusinessMan(user.getId() , user.getName() , user.getPhoneNumber(),
              user.getLoginEmail() , user.getPassword(), user.getRoleType() ,
              rs.getString("business_num") , rs.getString("business_name"));
          users.add(businessMan);
        } else if (user.getRoleType() == DELIVERY_MAN ) {
          DeliveryMan deliveryMan = new DeliveryMan(user.getId() , user.getName() , user.getPhoneNumber(),
              user.getLoginEmail() , user.getPassword(), user.getRoleType() ,
              rs.getString("delivery_man_num") , rs.getString("car_num"));
          users.add(deliveryMan);
        }
        else{
          users.add(user);
        }
      }
    }catch (SQLException e){
      throw e;
    }finally {
      close(pstmt , rs);
    }
    return users;
  }


//서비스에서 dto를 domain으로 변경하고 주입해줌 , 트랜잭션 서비스에서 시작해야하므로 User객체 생성 서비스에서 해줌!
  public void update(User user,  Connection con) throws SQLException {
    String sql = "update user set name = ? , phone_number = ?  where id = ?";
    PreparedStatement firstPstmt = null;
    PreparedStatement secondPstmt = null;

    try {
      firstPstmt = con.prepareStatement(sql);
      firstPstmt.setString(1 , user.getName());
      firstPstmt.setString(2, user.getPhoneNumber());
      firstPstmt.setInt(3 , user.getId());
      firstPstmt.executeUpdate();
      if (user instanceof BusinessMan businessMan){
        String businessManSql = "update business_man set business_num = ? , business_name = ? where id = ?";
        secondPstmt = con.prepareStatement(businessManSql);
        secondPstmt.setString(1 , businessMan.getBusinessNum());
        secondPstmt.setString(2 , businessMan.getBusinessName());
        secondPstmt.setInt(3 , businessMan.getId());
        secondPstmt.executeUpdate();
      }
      else if (user instanceof DeliveryMan deliveryMan){
        String businessManSql = "update delivery_man set delivery_man_num = ? , car_num = ? where id = ?";
        secondPstmt = con.prepareStatement(businessManSql);
        secondPstmt.setString(1 , deliveryMan.getDeliveryManNum());
        secondPstmt.setString(2 , deliveryMan.getCarNum());
        secondPstmt.setInt(3 , deliveryMan.getId());
        secondPstmt.executeUpdate();
      }


    }catch (SQLException e){
      throw e;

    }finally {
      close(firstPstmt , null);
    }
  }


  public void delete(User user , Connection con) throws SQLException{
    StringBuilder sql = new StringBuilder("delete from user where id = ?");
    sql.replace(12 , 16 , "business_man");
    sql.replace(12 , 16 , "delivery_man");

    PreparedStatement pstmt = null;


    try {
      //이 단계에서 이미 창고 관리자 , admin 삭제됨
      pstmt = con.prepareStatement(sql.toString());
      pstmt.setInt(1 , user.getId());
      pstmt.executeUpdate();

      if (user instanceof BusinessMan){
        sql.replace(12 , 16 , "business_man");
        pstmt = con.prepareStatement(sql.toString());
        pstmt.setInt(1 , user.getId());
        pstmt.executeUpdate();
      }
      else if (user instanceof  DeliveryMan){
        sql.replace(12, 16, "delivery_man");
        pstmt = con.prepareStatement(sql.toString());
        pstmt.setInt(1 , user.getId());
        pstmt.executeUpdate();
      }
    } catch (SQLException ex) {
      throw ex;
    } finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, null);
    }
  }



  private void close(Statement stmt, ResultSet rs) {

    if (rs != null) {
      try {
        rs.close(); //ResultSet 닫기
      } catch (SQLException e) {
        System.out.println("error = " + e.getMessage());
      }
    }

    if (stmt != null) {
      try {
        stmt.close(); //PreparedStatement 닫기
      } catch (SQLException e) {
        System.out.println("error = " + e.getMessage());
      }
    }
    //이렇게 로직을 구성하면 쿼리 날리는 요청 stmt 닫을때 예외 터져도 결국 catch로 잡으니 con도 정상적으로 닫힘
    //안 닫으면 커넥션 계속 유지가 되니 메모리 터짐 ㅇㅇ

    // 이미 닫을때 예외가 터지는 거니 특별히 할 수 있는거 없음 , 로그로 찍는거밖에.
  }
  //단건 수정 , 삭제 : executeQuery()에 sql 지정 x ,
  public void remove(Connection con, int bno) {
    String sql = "delete from Board where bno=?";
    PreparedStatement pstmt = null;

    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, bno);

      pstmt.executeUpdate();

    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    } finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, null);
    }
  }







}
