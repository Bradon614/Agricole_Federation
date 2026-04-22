package td.agricoles.agricol.dto;

import td.agricoles.agricol.dto.enums.Gender;
import td.agricoles.agricol.dto.enums.MemberOccupation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInformation {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private Long phoneNumber;
    private String email;
    private MemberOccupation occupation;
}
