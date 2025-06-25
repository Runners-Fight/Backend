package run.backend.domain.test.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestServiceTest {

    private TestService testService;

    @BeforeEach
    void setUp() {
        testService = new TestService();
    }

    @Test
    @DisplayName("number가 1일 때, 첫 번째 분기를 통과하고 'success'를 반환한다")
    void testCoverage_whenNumberIs1() {
        int number = 1;

        String result = testService.testCoverage(number);

        assertEquals("success", result, "결과 메시지는 'success'여야 합니다.");
    }

}