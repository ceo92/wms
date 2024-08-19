package dao;

import static domain.RoleType.ADMIN;
import static domain.RoleType.BUSINESS_MAN;
import static domain.RoleType.DELIVERY_MAN;

import domain.BusinessMan;
import domain.DeliveryMan;
import domain.Region;
import domain.RoleType;
import domain.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    String superTableSql = "insert into user(name , phone_number , login_email , password , role_type , password_question , password_answer) values(? , ? , ? , ? , ?,?,?)";
    String subTableSql = null;
    PreparedStatement superTablePstmt = null;
    PreparedStatement subTablePstmt = null;

    superTablePstmt = con.prepareStatement(superTableSql , Statement.RETURN_GENERATED_KEYS);
    superTablePstmt.setString(1, user.getName()); //인덱스 1로 해서 1번필드에는 memberid 지정
    superTablePstmt.setString(2, user.getPhoneNumber());
    superTablePstmt.setString(3, user.getLoginEmail());
    superTablePstmt.setString(4, user.getPassword());
    superTablePstmt.setString(5, user.getRoleType().name());
    superTablePstmt.setString(6, user.getPasswordQuestion());
    superTablePstmt.setString(7, user.getPasswordAnswer());
    superTablePstmt.executeUpdate();
    ResultSet rs = superTablePstmt.getGeneratedKeys(); //곧바로 가져옴
    int generatedId = 0;
    if (rs.next()){
      generatedId = rs.getInt(1); //첫번째 할당된 PK
    }
    //WAREHOUSE_MANAGER일 때는 추가적으로
    if (user instanceof DeliveryMan deliveryMan){
      subTableSql = "insert into delivery_man values(? ,? , ? , ?)";
      subTablePstmt = con.prepareStatement(subTableSql);

      subTablePstmt.setInt(1 , generatedId); //슈퍼타입테이블의 Generated된 PK 값 할당
      subTablePstmt.setString(2 , deliveryMan.getDeliveryManNum());
      subTablePstmt.setString(3 , deliveryMan.getCarNum());
      subTablePstmt.setInt(4 , deliveryMan.getRegion().getId()); //region과 연관관계
      subTablePstmt.executeUpdate();
    }
    else if (user instanceof BusinessMan businessMan){
      subTableSql = "insert into business_man values(? , ? , ?)";
      subTablePstmt = con.prepareStatement(subTableSql); //이렇게 하면 User과 식별관계인 BusinessMan테이블에 User의 PK 값이 자동으로 삽입됨

      subTablePstmt.setInt(1 , generatedId); //슈퍼타입테이블의  Generated된 PK 값 할당
      subTablePstmt.setString(2 , businessMan.getBusinessNum());
      subTablePstmt.setString(3 , businessMan.getBusinessName());
      subTablePstmt.executeUpdate();
    }

    //커넥션 연결 => 쿼리 요청 역순으로 close
    close(subTablePstmt, null);
    close(superTablePstmt, rs);
    return generatedId;
  }


  public Optional<User> findById(Integer id , Connection con){
    StringBuilder sql = new StringBuilder("select * from user where id = ?");
    PreparedStatement superTablePstmt = null , subTablePstmt = null;
    ResultSet superTableRs = null , subTableRs = null;
    try {
      User user = null;
      superTablePstmt = con.prepareStatement(sql.toString());
      superTablePstmt.setInt(1 , id);
      superTableRs = superTablePstmt.executeQuery();
      if (superTableRs.next()) {
        user = new User(superTableRs.getString("name") , superTableRs.getString("phone_number")
        , superTableRs.getString("login_email") , superTableRs.getString("password") ,
            RoleType.valueOf(superTableRs.getString("role_type")) ,  superTableRs.getString("password_question")
        , superTableRs.getString("password_answer"));
      }
      if (user.getRoleType() == BUSINESS_MAN){
        sql.replace(14 , 18 , "business_man");
        subTablePstmt = con.prepareStatement(sql.toString());
        subTablePstmt.setInt(1, id);
        subTableRs = subTablePstmt.executeQuery();
        if (subTableRs.next()) {
          BusinessMan businessMan = new BusinessMan(id , user.getName() , user.getPhoneNumber(), user.getLoginEmail() ,
              user.getPassword(), user.getRoleType() ,user.getPasswordQuestion() , user.getPasswordAnswer(),
              subTableRs.getString("business_num") ,subTableRs.getString("business_name"));
          return Optional.ofNullable(businessMan);
        }
      } else if (user.getRoleType() == DELIVERY_MAN){
        sql.delete(0 , sql.length());
        sql.append("select * from delivery_man d join region r on d.region_id = r.id where d.id = ?");
        subTablePstmt = con.prepareStatement(sql.toString());
        subTablePstmt.setInt(1, id);
        subTableRs = subTablePstmt.executeQuery();
        if (subTableRs.next()) {
          DeliveryMan deliveryMan = new DeliveryMan(id , user.getName() , user.getPhoneNumber(), user.getLoginEmail() ,
              user.getPassword(), user.getRoleType() ,user.getPasswordQuestion() , user.getPasswordAnswer(),
              subTableRs.getString("delivery_man_num") ,subTableRs.getString("car_num") ,
              new Region(subTableRs.getInt("r.id") , subTableRs.getString("code") , subTableRs.getString("name") , subTableRs.getObject("parent_id",  Integer.class)));
          return Optional.ofNullable(deliveryMan);
        }
      }else{
        return Optional.ofNullable(user);
      }
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      close(subTablePstmt , subTableRs);
      close(superTablePstmt , superTableRs);
    }
    return Optional.empty();
    //BusinessMan , DeliveryMan도 아니면 ADMIN 혹은 WAREHOUSE_MANAGER이므로 해당 데이터를 던져주면 됨 ㅇㅇ 이게 사실은 else문이 되게 됨
  }

  /**
   * 권한 별로 조회하기
   */

  //어차피 user의 id랑 사업자의 id랑 똑같을 거 아니야 ??비식별 관계니까
  public List<User> findAllByRoleType(RoleType roleType , Connection con) throws SQLException {
    StringBuilder sql = new StringBuilder("select * from user u ");
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<User> users = new ArrayList<>();
    try {
      if (roleType == BUSINESS_MAN) {
        sql.append("join business_man b on u.id = b.id");
        sql.append("where u.role_type = 'BUSINESS_MAN'");
        pstmt = con.prepareStatement(sql.toString());
        pstmt.setString(1 , roleType.name().toLowerCase());
        rs = pstmt.executeQuery();
        while (rs.next()) {
          BusinessMan businessMan = new BusinessMan(rs.getInt("u.id"), rs.getString("u.name")
              , rs.getString("phone_number"), rs.getString("login_email"), rs.getString("password") ,
              roleType , rs.getString("password_question") , rs.getString("password_answer")
              , rs.getString("business_num"), rs.getString("business_name"));
          users.add(businessMan);
        }
      } else if (roleType == DELIVERY_MAN) {
        sql.append("join delivery_man d on u.id = d.id");
        sql.append("join region r on d.id = r.id");
        sql.append("where u.role_type = 'DELIVERY_MAN'");
        pstmt = con.prepareStatement(sql.toString());
        rs = pstmt.executeQuery();
        while (rs.next()) {
          Region region = new Region(rs.getInt("r.id"),
              rs.getString("r.code"),
              rs.getString("r.name"),
              rs.getObject("r.parent_id", Integer.class));
          DeliveryMan deliveryMan = new DeliveryMan(rs.getInt("u.id"), rs.getString("u.name")
              , rs.getString("phone_number"), rs.getString("login_email"), rs.getString("password") ,
              roleType , rs.getString("password_question") , rs.getString("password_answer")
              , rs.getString("delivery_man_num"), rs.getString("car_num") , region);
          users.add(deliveryMan);
        }
      } else {
        pstmt = con.prepareStatement(sql.toString());
        rs = pstmt.executeQuery();
        while (rs.next()) {
          User user = new User(rs.getInt("u.id"), rs.getString("u.name"),
              rs.getString("phone_number"), rs.getString("login_email"),
              rs.getString("password"), roleType , rs.getString("password_question")
              , rs.getString("password_answer"));
          if (user.getRoleType() == ADMIN) continue;
          users.add(user);
        }
      }
      return users;
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      close(pstmt , rs);
    }
  }


  public List<User> findAll(Connection con) throws SQLException {
    StringBuilder sql = new StringBuilder("select * from user");
    PreparedStatement superTablePstmt = null, subTablePstmt = null;
    ResultSet superTableRs = null, subTableRs = null;
    List<User> users = new ArrayList<>();
    try{
      superTablePstmt = con.prepareStatement(sql.toString());
      superTableRs = superTablePstmt.executeQuery();
      while (superTableRs.next()){
        User user = new User(superTableRs.getInt("id") , superTableRs.getString("name") ,  superTableRs.getString("phone_number") ,
            superTableRs.getString("login_email") , superTableRs.getString("password") , RoleType.valueOf(superTableRs.getString("role_type")) , superTableRs.getString("password_question") , superTableRs.getString("password_answer"));
        if (user.getRoleType() == BUSINESS_MAN){
          sql.replace(14, 18, "business_man");
          subTablePstmt = con.prepareStatement(sql.toString());
          subTableRs = subTablePstmt.executeQuery();
          while (subTableRs.next()) {
            BusinessMan businessMan = new BusinessMan(user.getId(), user.getName(),
                user.getPhoneNumber(),
                user.getLoginEmail(), user.getPassword(), user.getRoleType(),
                user.getPasswordQuestion(), user.getPasswordAnswer(),
                subTableRs.getString("business_num"), subTableRs.getString("business_name"));
            users.add(businessMan);
          }
        } else if (user.getRoleType() == DELIVERY_MAN ) {
          sql.replace(14, 18, "delivery_man");
          subTablePstmt = con.prepareStatement(sql.toString());
          subTableRs = subTablePstmt.executeQuery();
          while (subTableRs.next()) {
            DeliveryMan deliveryMan = new DeliveryMan(user.getId(), user.getName(),
                user.getPhoneNumber(),
                user.getLoginEmail(), user.getPassword(), user.getRoleType(),
                user.getPasswordQuestion(), user.getPasswordAnswer(),
                subTableRs.getString("delivery_man_num"), subTableRs.getString("car_num")
                , new Region(subTableRs.getInt("id"), subTableRs.getString("code"),
                subTableRs.getString("name"), subTableRs.getObject("parent_id", Integer.class)));
            users.add(deliveryMan);
          }
        }
        else{
          users.add(user);
        }
      }
    }catch (SQLException e){
      throw e;
    }finally {
      close(subTablePstmt , subTableRs);
      close(superTablePstmt , superTableRs);
    }
    return users;
  }


  /**
   * 변경 : 기본정보
   */

//서비스에서 dto를 domain으로 변경하고 주입해줌 , 트랜잭션 서비스에서 시작해야하므로 User객체 생성 서비스에서 해줌!
  public void updateBasicInformation(User user,  Connection con) throws SQLException {
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
      throw new RuntimeException(e);

    }finally {
      close(secondPstmt , null);
      close(firstPstmt , null);
    }
  }

  /**
   * 변경 : 비밀번호
   */

  public void updatePassword(User user ,  Connection con) throws SQLException {
    String sql = "update user set password = ? where id = ?";
    PreparedStatement pstmt = null;

    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1 , user.getPassword());
      pstmt.setInt(2 , user.getId());
      pstmt.executeUpdate();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }finally {
      close(pstmt , null);
    }
  }


  public void delete(User user , Connection con) throws SQLException{
    StringBuilder sql = new StringBuilder("delete from user where id = ?");
    PreparedStatement superTablePstmt = null , subTablePstmt = null;
    superTablePstmt = con.prepareStatement(sql.toString());
    superTablePstmt.setInt(1 , user.getId());
    superTablePstmt.executeUpdate();
    try {
      if (user instanceof BusinessMan){
        sql.replace(12 , 16 , "business_man");
        subTablePstmt = con.prepareStatement(sql.toString());
        subTablePstmt.setInt(1 , user.getId());
        subTablePstmt.executeUpdate();
      }
      else if (user instanceof  DeliveryMan){
        sql.replace(12, 16, "delivery_man");
        subTablePstmt = con.prepareStatement(sql.toString());
        subTablePstmt.setInt(1 , user.getId());
        subTablePstmt.executeUpdate();
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    } finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(subTablePstmt, null);
      close(superTablePstmt, null);
    }
  }

  private void close(Statement stmt, ResultSet rs) {

    if (rs != null) {
      try {
        rs.close(); //ResultSet 닫기
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

    if (stmt != null) {
      try {
        stmt.close(); //PreparedStatement 닫기
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    //이렇게 로직을 구성하면 쿼리 날리는 요청 stmt 닫을때 예외 터져도 결국 catch로 잡으니 con도 정상적으로 닫힘
    //안 닫으면 커넥션 계속 유지가 되니 메모리 터짐 ㅇㅇ

    // 이미 닫을때 예외가 터지는 거니 특별히 할 수 있는거 없음 , 로그로 찍는거밖에.
  }
  //단건 수정 , 삭제 : executeQuery()에 sql 지정 x ,








}
