package td.agricoles.agricol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import td.agricoles.agricol.Services.CollectiveService;
import td.agricoles.agricol.Services.MemberService;
import td.agricoles.agricol.controller.FederationController;
import td.agricoles.agricol.repository.CollectiveRepository;
import td.agricoles.agricol.repository.MemberRepository;

@SpringBootApplication
public class AgricolApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgricolApplication.class, args);
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepository();
    }

    @Bean
    public CollectiveRepository collectiveRepository() {
        return new CollectiveRepository();
    }

    @Bean
    public CollectiveService collectiveService() {
        return new CollectiveService(collectiveRepository(), memberRepository());
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository(), collectiveRepository());
    }

    @Bean
    public FederationController federationController() {
        return new FederationController(collectiveService(), memberService());
    }

}
