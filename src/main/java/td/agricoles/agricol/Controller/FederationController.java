package td.agricoles.agricol.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import td.agricoles.agricol.DTOs.Collectivity;
import td.agricoles.agricol.DTOs.CreateCollectivity;
import td.agricoles.agricol.DTOs.Member;

import java.util.List;

public class FederationController {
    private final CollectiveService collectiveService;
    private final MemberService memberService;

    // Constructor injection → PAS de @Autowired
    public FederationController(CollectiveService collectiveService, MemberService memberService) {
        this.collectiveService = collectiveService;
        this.memberService = memberService;
    }

    @PostMapping("/collectivities")
    public ResponseEntity<List<Collectivity>> createCollectivities(
            @RequestBody List<CreateCollectivity> collectivities) {

        List<Collectivity> created = collectiveService.createCollectivities(collectivities);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/members")
    public ResponseEntity<List<Member>> createMembers(
            @RequestBody List<CreateMember> members) {

        List<Member> created = memberService.createMembers(members);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
