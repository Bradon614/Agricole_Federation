package td.agricoles.agricol.Services;

import org.springframework.stereotype.Service;
import td.agricoles.agricol.dto.request.CreateMember;
import td.agricoles.agricol.dto.response.Member;
import td.agricoles.agricol.exception.BadRequestException;
import td.agricoles.agricol.exception.NotFoundException;
import td.agricoles.agricol.repository.CollectiveRepository;
import td.agricoles.agricol.repository.MemberRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final CollectiveRepository collectiveRepository;

    public MemberService(MemberRepository memberRepository, CollectiveRepository collectiveRepository) {
        this.memberRepository = memberRepository;
        this.collectiveRepository = collectiveRepository;
    }

    public List<Member> createMembers(List<CreateMember> createList) {
        List<Member> result = new ArrayList<>();
        for (CreateMember create : createList) {
            validate(create);
            try {
                Member saved = memberRepository.save(create);
                result.add(saved);
            } catch (SQLException e) {
                throw new RuntimeException("Database error while saving member", e);
            }
        }
        return result;
    }

    private void validate(CreateMember create) {
        if (!create.isRegistrationFeePaid() || !create.isMembershipDuesPaid()) {
            throw new BadRequestException("Registration fee and membership dues must be paid");
        }

        List<String> referees = create.getReferees();
        if (referees == null || referees.size() < 2) {
            throw new BadRequestException("At least 2 referees required");
        }

        String targetCollId = create.getCollectivityIdentifier();
        try {
            if (!collectiveRepository.exists(targetCollId)) {
                throw new NotFoundException("Collectivity not found: " + targetCollId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error checking collectivity", e);
        }

        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);
        int targetCount = 0;
        int otherCount = 0;

        for (String refIdStr : referees) {
            try {
                Member refMember = memberRepository.findById(refIdStr);
                if (refMember == null) {
                    throw new NotFoundException("Referee not found: " + refIdStr);
                }
                LocalDate adhesion = memberRepository.getAdhesionDate(refIdStr);
                if (adhesion.isAfter(ninetyDaysAgo)) {
                    throw new BadRequestException("Referee " + refIdStr + " has less than 90 days seniority");
                }
                if (!memberRepository.isSeniorMember(refIdStr)) {
                    throw new BadRequestException("Referee " + refIdStr + " is not a senior member");
                }
                String refCollId = memberRepository.getCurrentCollectiveId(refIdStr);
                if (targetCollId.equals(refCollId)) {
                    targetCount++;
                } else {
                    otherCount++;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error validating referee", e);
            }
        }

        if (targetCount < otherCount) {
            throw new BadRequestException("Number of referees from target collective must be >= referees from other collectives");
        }
    }
}