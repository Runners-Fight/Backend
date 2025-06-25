package run.backend.domain.test.controller;

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
    @DisplayName("number가 1일 때, 첫 번째 분기를 통과하고 'test'를 반환한다")
    void testCoverage_whenNumberIs1() {
        int number = 1;

        String result = testService.test(number);

        assertEquals("test", result, "결과 메시지는 'test'여야 합니다.");
    }
}