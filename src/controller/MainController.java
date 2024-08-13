package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpRequest;
import java.sql.SQLException;
import domain.User;
import dto.DeliveryManDto;
import dto.BusinessManDto;
import dto.WarehouseManagerDto;
import service.UserService;

public class MainController {


  private static final UserService userService = new UserService();

  public static void main(String[] args) throws IOException, SQLException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Welcome to Money Flow WMS");
    Integer loginUserId = null; //로그인 여부 판별용 id

    while (true) {
      /**
       * 로그인 여부에 따른 시작 로직(로그인)
       */
      if (loginUserId !=null){
        authenticate(loginUserId, br);
      }
      else{
        System.out.println("1. 로그인 2. 회원가입 3. Q&A 작성 , 번호로 입력");
        int guestInputValue = Integer.parseInt(br.readLine());
        if (guestInputValue == 1) {
          System.out.println("=".repeat(20) + "로그인 화면" + "=".repeat(20));
          System.out.print("이메일 입력 : ");
          String loginEmail = br.readLine();
          System.out.print("비밀번호 입력 : ");
          String password = br.readLine();
          User loginUser = userService.login(loginEmail, password); //authservice에 로그인



        } else if (guestInputValue == 2) {
          System.out.println("=".repeat(20) + "회원가입 화면" + "=".repeat(20));
          System.out.println();
          System.out.println("어느 권한의 회원으로 가입하시겠습니까?");
          System.out.println("1. 사업자 (BusinessMan)");
          System.out.println("2. 창고 관리자(WarehouseManager)");
          System.out.println("3. 배송 기사(DeliveryMan)");
          int inputRoleType = Integer.parseInt(br.readLine());
          switch (inputRoleType) {
            case 1:
              System.out.print("사업자 번호 입력 : ");
              String businessNum = br.readLine();
              System.out.print("상호명 입력 : ");
              String businessName = br.readLine();
              System.out.print("이름 입력 : ");
              String businessManName = br.readLine();
              System.out.print("핸드폰 번호 입력 : ");
              String businessManPhoneNumber = br.readLine();
              System.out.print("이메일 아이디 입력 : ");
              String businessManLoginEmail = br.readLine();
              System.out.print("비밀번호 입력 : ");
              String businessManPassword = br.readLine();
              System.out.print("비밀번호 재입력 ");
              String businessManRePassword = br.readLine();
              BusinessManDto businessManDto = new BusinessManDto(businessManName,
                  businessManPhoneNumber, businessManLoginEmail, businessManPassword,
                  businessManRePassword, businessNum, businessName);
              //사업자 id 리턴
              loginUserId = userService.businessManJoin(businessManDto);//회원가입 성공하면 다시 초기 화면으로 돌아가서 로그인 되게끔
              //사업자 id를 통해 조회
              break;

            case 2:
              System.out.print("이름 입력 : ");
              String whmName = br.readLine();
              System.out.print("핸드폰 번호 입력 : ");
              String whmPhoneNumber = br.readLine();
              System.out.print("이메일 아이디 입력 : ");
              String whmLoginEmail = br.readLine();
              System.out.print("비밀번호 입력 : ");
              String whmPassword = br.readLine();
              System.out.print("비밀번호 재입력 ");
              String whmRePassword = br.readLine();
              WarehouseManagerDto warehouseManagerDto = new WarehouseManagerDto(whmName,
                  whmPhoneNumber, whmLoginEmail, whmPassword, whmRePassword);
              loginUserId = userService.warehouseManagerJoin(warehouseManagerDto);
              break;

            case 3:
              System.out.println("배송기사 번호 입력 : ");
              String dmNum = br.readLine();
              System.out.println("차량 번호 입력 : ");
              String dmCarNum = br.readLine();
              System.out.print("이름 : ");
              String dmName = br.readLine();
              System.out.print("핸드폰 번호 : ");
              String dmPhoneNumber = br.readLine();
              System.out.print("이메일 아이디 : ");
              String dmLoginEmail = br.readLine();
              System.out.print("비밀번호 : ");
              String dmPassword = br.readLine();
              System.out.print("비밀번호 재입력 ");
              String dmRePassword = br.readLine();
              DeliveryManDto deliveryManDto = new DeliveryManDto(dmName, dmPhoneNumber,
                  dmLoginEmail, dmPassword, dmRePassword, dmNum, dmCarNum);
              loginUserId = userService.deliveryManJoin(deliveryManDto);
              break;

            default:
              System.out.println("잘못 입력하였습니다 처음부터 다시 입력해주세요"); //검증 로직
              break;
          }
        } else if (guestInputValue == 3) {
          //q&a 작성
          System.out.println("잘못된 입력입니다.");
        }
      }
    }


  }

  private static void authenticate(Integer loginId, BufferedReader br) throws IOException {
    User user = userService.findUser(loginId);
    switch (user.getRoleType()) {
      case ADMIN:
        System.out.println("어떤 시스템에 접속하시겠습니까?");
        System.out.println("1. 입고 관리");
        System.out.println("2. 출고 관리");
        System.out.println("3. 재고 관리");
        System.out.println("4. 고객센터");
        System.out.println("5. 재무 관리");
        System.out.println("6. 내 정보 조회");
        int adminNum = Integer.parseInt(br.readLine());


      case WAREHOUSE_MANAGER:
        System.out.println("어떤 시스템에 접속하시겠습니까?");
        System.out.println("1. 입고 관리");
        System.out.println("2. 출고 관리");
        System.out.println("3. 재고 관리");
        System.out.println("4. 고객센터");
        System.out.println("5. 재무 관리");
        System.out.println("6. 내 정보 조회");
        int whmNum = Integer.parseInt(br.readLine());

      case DELIVERY_MAN:
        System.out.println("어떤 시스템에 접속하시겠습니까?");
        System.out.println("1. 운송장 조회");
        System.out.println("2. 요금안내 조회");
        System.out.println("3. 창고별 검색 ");
        System.out.println("4. 공지사항 조회");
        System.out.println("5. 문의게시판 조회");
        System.out.println("6. 내 정보 조회");
        int dmNum = Integer.parseInt(br.readLine());

      case BUSINESS_MAN:
        System.out.println("어떤 시스템에 접속하시겠습니까?");
        System.out.println("1. 입고 관리");
        System.out.println("2. 출고 관리");
        System.out.println("3. 재고 관리");
        System.out.println("4. 고객센터");
        System.out.println("5. 재무 관리");
        int busNum = Integer.parseInt(br.readLine());

    }
  }
}
