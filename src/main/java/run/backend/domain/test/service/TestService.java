package run.backend.domain.test.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String testCoverage(int number) {
        if (number == 1) {
            System.out.println("first test");
        } else if (number == 2) {
            System.out.println("second test");
        } else {
            System.out.println("third test");
        }
        return "success";
    }
}
