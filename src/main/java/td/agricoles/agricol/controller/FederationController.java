package td.agricoles.agricol.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import td.agricoles.agricol.dto.request.*;
import td.agricoles.agricol.dto.response.*;
import td.agricoles.agricol.Services.CollectiveService;
import td.agricoles.agricol.Services.MemberService;
import td.agricoles.agricol.exception.BadRequestException;
import td.agricoles.agricol.exception.NotFoundException;
import td.agricoles.agricol.repository.CollectiveRepository;
import td.agricoles.agricol.repository.MemberRepository;

import java.sql.SQLException;
import java.time.LocalDate;
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

    @PatchMapping("/collectivities/{id}/identifiers")
    public ResponseEntity<?> assignIdentifiers(
            @PathVariable String id,
            @RequestBody CollectiveIdentifiersAssignment assignment) {
        try {
            Collectivity updated = collectiveService.assignIdentifiers(id, assignment.getNumber(), assignment.getName());
            return ResponseEntity.ok(updated);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<?> getMembershipFees(@PathVariable String id) {
        try {
            List<MembershipFee> fees = CollectiveRepository.findMembershipFeesByCollectiveId(id);
            return ResponseEntity.ok(fees);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<?> createMembershipFees(@PathVariable String id,
                                                  @RequestBody List<CreateMembershipFee> fees) {
        try {
            if (!CollectiveRepository.exists(id)) {
                throw new NotFoundException("Collectivity not found");
            }
            // Validation: frequency valide, amount > 0
            for (CreateMembershipFee fee : fees) {
                if (fee.getAmount() <= 0) {
                    throw new BadRequestException("Amount must be positive");
                }
            }
            List<MembershipFee> created = CollectiveRepository.saveMembershipFees(id, fees);
            return ResponseEntity.ok(created);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/collectivities/{id}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable String id,
                                             @RequestParam LocalDate from,
                                             @RequestParam LocalDate to) {
        try {
            if (from.isAfter(to)) {
                throw new BadRequestException("'from' date must be before 'to' date");
            }
            List<CollectivityTransaction> transactions =
                    CollectiveRepository.findTransactionsByCollectiveIdAndPeriod(id, from, to);
            return ResponseEntity.ok(transactions);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/members/{id}/payments")
    public ResponseEntity<?> createPayments(@PathVariable String id,
                                            @RequestBody List<CreateMemberPayment> payments) {
        try {
            // Vérifier que le membre existe
            if (MemberRepository.findById(id) == null) {
                throw new NotFoundException("Member not found");
            }
            List<MemberPayment> created = MemberRepository.savePayments(id, payments);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SQLException e) {
            return new ResponseEntity<>("Database error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/collectivities/{id}")
    public ResponseEntity<?> getCollectivity(@PathVariable String id) {
        try {
            Collectivity coll = collectiveService.getCollectivityById(id);
            return ResponseEntity.ok(coll);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/collectivities/{id}/financialAccounts")
    public ResponseEntity<?> getFinancialAccounts(@PathVariable String id,
                                                  @RequestParam LocalDate at) {
        try {
            List<FinancialAccount> accounts = collectiveService.getFinancialAccounts(id, at);
            return ResponseEntity.ok(accounts);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
