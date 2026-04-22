package td.agricoles.agricol.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import td.agricoles.agricol.dto.response.Collectivity;
import td.agricoles.agricol.dto.request.CreateCollectivity;
import td.agricoles.agricol.dto.request.CreateMember;
import td.agricoles.agricol.dto.response.Member;
import td.agricoles.agricol.Services.CollectiveService;
import td.agricoles.agricol.Services.MemberService;
import td.agricoles.agricol.exception.BadRequestException;
import td.agricoles.agricol.exception.NotFoundException;

import java.util.List;

@RestController
public class FederationController {

    private final CollectiveService collectiveService;
    private final MemberService memberService;

    public FederationController(CollectiveService collectiveService, MemberService memberService) {
        this.collectiveService = collectiveService;
        this.memberService = memberService;
    }

    @PostMapping("/collectivities")
    public ResponseEntity<?> createCollectivities(@RequestBody List<CreateCollectivity> collectivities) {
        try {
            List<Collectivity> created = collectiveService.createCollectivities(collectivities);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/members")
    public ResponseEntity<?> createMembers(@RequestBody List<CreateMember> members) {
        try {
            List<Member> created = memberService.createMembers(members);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
