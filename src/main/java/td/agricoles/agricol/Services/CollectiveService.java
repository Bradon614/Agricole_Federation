package td.agricoles.agricol.Services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import td.agricoles.agricol.dto.request.CreateCollectivity;
import td.agricoles.agricol.dto.request.CreateCollectivityStructure;
import td.agricoles.agricol.dto.response.Collectivity;
import td.agricoles.agricol.dto.response.CollectivityStructure;
import td.agricoles.agricol.dto.response.Member;
import td.agricoles.agricol.exception.BadRequestException;
import td.agricoles.agricol.exception.NotFoundException;
import td.agricoles.agricol.repository.CollectiveRepository;
import td.agricoles.agricol.repository.MemberRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectiveService {

    private final CollectiveRepository collectiveRepository;
    private final MemberRepository memberRepository;

    public CollectiveService(CollectiveRepository collectiveRepository, MemberRepository memberRepository) {
        this.collectiveRepository = collectiveRepository;
        this.memberRepository = memberRepository;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivity> createList) {
        List<Collectivity> result = new ArrayList<>();
        for (CreateCollectivity create : createList) {
            validate(create);
            try {
                Collectivity saved = collectiveRepository.save(create);
                result.add(saved);
            } catch (SQLException e) {
                throw new RuntimeException("Database error while saving collectivity", e);
            }
        }
        return result;
    }

    private void validate(CreateCollectivity create) {
        if (!create.isFederationApproval()) {
            throw new BadRequestException("Federation approval required");
        }
        if (create.getLocation() == null || create.getLocation().isBlank()) {
            throw new BadRequestException("Location is required");
        }
        CreateCollectivityStructure struct = create.getStructure();
        if (struct == null ||
                struct.getPresident() == null || struct.getVicePresident() == null ||
                struct.getTreasurer() == null || struct.getSecretary() == null) {
            throw new BadRequestException("All four specific posts must be assigned");
        }

        List<String> memberIds = create.getMembers().stream().map(m -> m.getId()).toList();
        if (memberIds.size() < 10) {
            throw new BadRequestException("At least 10 members required");
        }

        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        int seniorCount = 0;
        for (String id : memberIds) {
            try {
                LocalDate adhesion = memberRepository.getAdhesionDate(id);
                if (!adhesion.isAfter(sixMonthsAgo)) {
                    seniorCount++;
                }
            } catch (SQLException | NotFoundException e) {
                throw new NotFoundException("Member not found: " + id);
            }
        }
        if (seniorCount < 5) {
            throw new BadRequestException("At least 5 members must have 6+ months seniority");
        }
    }

}
