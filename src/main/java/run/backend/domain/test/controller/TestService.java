package run.backend.domain.test.controller;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String test(int number) {
        if (number == 1) {
            System.out.println("first");
        } else if (number == 2) {
            System.out.println("second");
        } else {
            System.out.println("third");
        }
        return "test";
    }
}
