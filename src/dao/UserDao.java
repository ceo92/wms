package dao;

import static domain.RoleType.BUSINESS_MAN;
import static domain.RoleType.DELIVERY_MAN;

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


  public Integer save(User user , Connection con) throws SQLException {
    //PreparedStatement는 각각의 쿼리에 지정해줘야됨
    String superTableSql = "insert into user(name , phone_number , login_email , password , dtype , role_type) values(? , ? , ? , ?, ?, ?)";
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
    switch (user.getRoleType()){
      case DELIVERY_MAN:
        DeliveryMan deliveryMan = (DeliveryMan) user;
        subTableSql = "insert into delivery_man values(? ,? , ?)";
        subTablePstmt = con.prepareStatement(subTableSql);

        subTablePstmt.setInt(1 , generatedId); //슈퍼타입테이블의 Generated된 PK 값 할당
        subTablePstmt.setString(2 , deliveryMan.getDeliveryManNum());
        subTablePstmt.setString(3 , deliveryMan.getCarNum());
        subTablePstmt.executeUpdate();
        break;

      case BUSINESS_MAN:
        BusinessMan businessMan = (BusinessMan) user;
        subTableSql = "insert into business_man values(? , ? , ?)";
        subTablePstmt = con.prepareStatement(subTableSql); //이렇게 하면 User과 식별관계인 BusinessMan테이블에 User의 PK 값이 자동으로 삽입됨

        subTablePstmt.setInt(1 , generatedId); //슈퍼타입테이블의  Generated된 PK 값 할당
        subTablePstmt.setString(2 , businessMan.getBusinessName());
        subTablePstmt.setString(3 , businessMan.getBusinessName());
        subTablePstmt.executeUpdate();
        break;

    }

    //커넥션 연결 => 쿼리 요청 역순으로 close
    close(subTablePstmt, null);
    close(superTablePstmt, rs);
    return generatedId;
  }


  public Optional<User> findById(Integer id , RoleType roleType , Connection con) throws SQLException {
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
  }


  public void update(Integer userId , DeliveryManUpdateDto deliveryManUpdateDto,  Connection con){
    String sql = "update  set title = ? , content = ? , writer = ? where bno = ?";
    PreparedStatement pstmt = null;
    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, boardUpdateDto.getBtitle());
      pstmt.setString(2, boardUpdateDto.getBcontent());
      pstmt.setString(3, boardUpdateDto.getBwriter());
      pstmt.setInt(4, boardUpdateDto.getBno());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw e;
    } finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, null);
    }
  }


  public Optional<User> findByLoginEmail(String loginEmail , Connection con){
    return store.values().stream().filter(user -> user.getLoginEmail().equals(loginEmail)).findFirst();
  }

  public List<User> findAll(){
    List<Board> list = new ArrayList<>();
    String sql = "select * from Board";

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = con.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while (rs.next()) {
        list.add(new Board(rs.getInt("bno"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("writer"),
            rs.getDate("date").toLocalDate()));
      }
      return list;
    } catch (SQLException e) {
      throw e;
    }finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, rs);
    }
  }

  public void removeUser(User user){
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


  public void save(Connection con, Board board) throws SQLException {
    String sql = "insert into Board(title , content , writer , date) values(? , ? , ? , ?)";
    PreparedStatement pstmt = null;

    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, board.getBtitle()); //인덱스 1로 해서 1번필드에는 memberid 지정
      pstmt.setString(2, board.getBcontent()); //인덱스 1로 해서 1번필드에는 memberid 지정
      pstmt.setString(3, board.getBwriter()); //인덱스 1로 해서 1번필드에는 memberid 지정
      pstmt.setTimestamp(4,
          Timestamp.valueOf(board.getBdate().atStartOfDay())); //인덱스 1로 해서 1번필드에는 memberid 지정
      pstmt.executeUpdate();

    } catch (SQLException e) {
      throw e;
    }finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, null);
    }
  }


  public Optional<Board> findByBno(Connection con , int bno) throws SQLException {
    String sql = "SELECT * FROM Board WHERE bno = ?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, bno);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        Board board = new Board(rs.getInt("bno"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("writer"),
            rs.getDate("date").toLocalDate());
        return Optional.of(board);
      } else {
        return Optional.empty();
      }

    } catch (SQLException e) {
      throw e;
    }finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, rs);
    }
  }

  public void update(Connection con, BoardUpdateDto boardUpdateDto) throws SQLException {
    String sql = "update Board set title = ? , content = ? , writer = ? where bno = ?";
    PreparedStatement pstmt = null;
    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, boardUpdateDto.getBtitle());
      pstmt.setString(2, boardUpdateDto.getBcontent());
      pstmt.setString(3, boardUpdateDto.getBwriter());
      pstmt.setInt(4, boardUpdateDto.getBno());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw e;
    } finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, null);
    }
  }


  public List<Board> findAll(Connection con) throws SQLException {
    List<Board> list = new ArrayList<>();
    String sql = "select * from Board";

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = con.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while (rs.next()) {
        list.add(new Board(rs.getInt("bno"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("writer"),
            rs.getDate("date").toLocalDate()));
      }
      return list;
    } catch (SQLException e) {
      throw e;
    }finally {
      //커넥션 연결 => 쿼리 요청 역순으로 close
      close(pstmt, rs);
    }
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
