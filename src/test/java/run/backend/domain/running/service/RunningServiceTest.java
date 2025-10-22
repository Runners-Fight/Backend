package run.backend.domain.running.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.running.dto.request.Coordinate;
import run.backend.domain.running.entity.Pixel;
import run.backend.domain.running.entity.PixelId;
import run.backend.domain.running.repository.PixelRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RunningServiceTest {

    @Autowired
    private PixelRepository pixelRepository;

    @Autowired
    private RunningService runningService;

    @Test
    void 비관적락_적용된_processRunningRoute_동시성_검증() throws Exception {
        // given
        PixelId id = new PixelId(467, 478);
        Pixel initial = new Pixel(id, 99L, LocalDateTime.of(2025, 10, 20, 14, 0));
        pixelRepository.save(initial);

        Coordinate coordA = new Coordinate(37.5, 126.9, LocalDateTime.of(2025, 10, 20, 15, 0));
        Coordinate coordB = new Coordinate(37.5, 126.9, LocalDateTime.of(2025, 10, 20, 16, 0));

        CountDownLatch latchReady = new CountDownLatch(2);
        CountDownLatch latchStart = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable taskA = () -> {
            latchReady.countDown();
            try { latchStart.await(); } catch (InterruptedException ignored) {}
            runningService.processRunningRoute(1L, List.of(coordA));
        };

        Runnable taskB = () -> {
            latchReady.countDown();
            try { latchStart.await(); } catch (InterruptedException ignored) {}
            runningService.processRunningRoute(2L, List.of(coordB));
        };

        executor.submit(taskA);
        executor.submit(taskB);

        latchReady.await();
        latchStart.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Pixel result = pixelRepository.findById(id).get();
        assertThat(result.getCrewId()).isEqualTo(2L);
        assertThat(result.getUpdatedAt()).isEqualTo(LocalDateTime.of(2025, 10, 20, 16, 0));
    }
}
