package run.backend.domain.running.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.running.dto.request.Coordinate;
import run.backend.domain.running.entity.Pixel;
import run.backend.domain.running.entity.PixelId;
import run.backend.domain.running.repository.PixelRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import(run.backend.config.TestSecurityConfig.class)
class RunningServiceTest {

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private PixelRepository pixelRepository;

    @Autowired
    private RunningService runningService;

    @BeforeEach
    void setUp() {
        pixelRepository.deleteAll();
    }

    @Test
    @DisplayName("비관적 락 - 동시 요청 시 순차 처리로 최신 데이터만 저장")
    void pessimisticLock_concurrency_test() throws Exception {
        // given
        int[] pixel = runningService.toPixel(37.5663, 126.9779);
        PixelId pixelId = new PixelId(pixel[0], pixel[1]);

        Pixel initial = new Pixel(pixelId, 99L, LocalDateTime.of(2025, 10, 20, 14, 0));
        pixelRepository.saveAndFlush(initial);

        Coordinate coordA = new Coordinate(37.5663, 126.9779, LocalDateTime.of(2025, 10, 20, 15, 0));
        Coordinate coordB = new Coordinate(37.5663, 126.9779, LocalDateTime.of(2025, 10, 20, 16, 0));

        // when
        CountDownLatch latchReady = new CountDownLatch(2);
        CountDownLatch latchStart = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable taskA = () -> {
            latchReady.countDown();
            try {
                latchStart.await();
                runningService.processRunningRoute(1L, List.of(coordA));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable taskB = () -> {
            latchReady.countDown();
            try {
                latchStart.await();
                runningService.processRunningRoute(2L, List.of(coordB));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executor.submit(taskA);
        executor.submit(taskB);

        latchReady.await();
        latchStart.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // then
        Pixel result = pixelRepository.findById(pixelId).orElseThrow();
        assertThat(result.getCrewId()).isEqualTo(2L);
        assertThat(result.getUpdatedAt()).isEqualTo(LocalDateTime.of(2025, 10, 20, 16, 0));
    }

    @Test
    @DisplayName("데드락 재현 - 교차 경로")
    void deadlock_reproduction_test() throws Exception {
        // given: 정반대 경로 (데드락 유발)
        List<Coordinate> forwardRoute = List.of(
                new Coordinate(37.5650, 126.9770, LocalDateTime.of(2025, 10, 20, 14, 0)),
                new Coordinate(37.5652, 126.9772, LocalDateTime.of(2025, 10, 20, 14, 1)),
                new Coordinate(37.5654, 126.9774, LocalDateTime.of(2025, 10, 20, 14, 2)),
                new Coordinate(37.5656, 126.9776, LocalDateTime.of(2025, 10, 20, 14, 3)),
                new Coordinate(37.5658, 126.9778, LocalDateTime.of(2025, 10, 20, 14, 4))
        );

        List<Coordinate> reverseRoute = List.of(
                new Coordinate(37.5658, 126.9778, LocalDateTime.of(2025, 10, 20, 15, 0)),
                new Coordinate(37.5656, 126.9776, LocalDateTime.of(2025, 10, 20, 15, 1)),
                new Coordinate(37.5654, 126.9774, LocalDateTime.of(2025, 10, 20, 15, 2)),
                new Coordinate(37.5652, 126.9772, LocalDateTime.of(2025, 10, 20, 15, 3)),
                new Coordinate(37.5650, 126.9770, LocalDateTime.of(2025, 10, 20, 15, 4))
        );

        for (Coordinate coord : forwardRoute) {
            int[] pixel = runningService.toPixel(coord.latitude(), coord.longitude());
            pixelRepository.saveAndFlush(new Pixel(new PixelId(pixel[0], pixel[1]), 99L, coord.timestamp()));
        }

        // when
        int attempts = 20;
        AtomicInteger deadlockCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);

        System.out.println("\n=== 데드락 재현 테스트 (" + attempts + "회 시도) ===");

        for (int i = 0; i < attempts; i++) {
            CountDownLatch latch = new CountDownLatch(2);
            CountDownLatch start = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(2);

            AtomicInteger errorCount = new AtomicInteger(0);

            executor.submit(() -> {
                latch.countDown();
                try {
                    start.await();
                    runningService.processRunningRoute(1L, forwardRoute);
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
            });

            executor.submit(() -> {
                latch.countDown();
                try {
                    start.await();
                    runningService.processRunningRoute(2L, reverseRoute);
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
            });

            latch.await();
            start.countDown();
            executor.shutdown();

            if (!executor.awaitTermination(10, TimeUnit.SECONDS) || errorCount.get() > 0) {
                deadlockCount.incrementAndGet();
                executor.shutdownNow();
            } else {
                successCount.incrementAndGet();
            }
        }

        // then
        System.out.println("성공: " + successCount.get() + "회");
        System.out.println("데드락/타임아웃: " + deadlockCount.get() + "회");
        System.out.println("데드락 발생률: " + String.format("%.1f%%", (deadlockCount.get() * 100.0 / attempts)));
        System.out.println("=".repeat(60) + "\n");

        if (deadlockCount.get() > 0) {
            System.out.println("🔴 데드락 발생! 정렬 로직이 필요합니다.");
        }
    }

    @Test
    @DisplayName("오래된 데이터는 업데이트하지 않음")
    void skipOlderData_test() {
        // given
        int[] pixel = runningService.toPixel(37.5663, 126.9779);
        PixelId pixelId = new PixelId(pixel[0], pixel[1]);

        Pixel existing = new Pixel(pixelId, 1L, LocalDateTime.of(2025, 10, 20, 16, 0));
        pixelRepository.saveAndFlush(existing);

        // when
        Coordinate olderCoord = new Coordinate(37.5663, 126.9779, LocalDateTime.of(2025, 10, 20, 15, 0));
        runningService.processRunningRoute(2L, List.of(olderCoord));

        // then
        Pixel result = pixelRepository.findById(pixelId).orElseThrow();
        assertThat(result.getCrewId()).isEqualTo(1L);  // 기존 crew 유지
        assertThat(result.getUpdatedAt()).isEqualTo(LocalDateTime.of(2025, 10, 20, 16, 0));
    }
}
