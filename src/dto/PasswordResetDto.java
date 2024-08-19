package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetDto {

  private String loginEmail;
  private String name;
  private String phoneNumber;
  private String passwordQuestion;
  private String passwordAnswer;


}
