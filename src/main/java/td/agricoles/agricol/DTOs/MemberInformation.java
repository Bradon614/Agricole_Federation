package td.agricoles.agricol.DTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor

public class MemberInformation {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;        // String au lieu de int pour éviter les problèmes
    private String email;
    private MemberOccupation occupation;
}
