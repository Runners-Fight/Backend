package run.backend.domain.running.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import run.backend.domain.running.entity.Pixel;
import run.backend.domain.running.entity.PixelId;
import run.backend.domain.running.repository.PixelRepository;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RunningServiceTest {

    @Autowired
    private PixelRepository pixelRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void 동시성_테스트_A가_B를_덮어쓰는_상황_확인() throws InterruptedException {
        // given
        // 초기 데이터 셋팅 : 99L 크루가 14시에 해당 픽셀을 지남
        PixelId id = new PixelId(467, 478);
        Pixel initial = new Pixel(id, 99L, LocalDateTime.of(2025, 10, 20, 14, 0));
        pixelRepository.save(initial);

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latchReady = new CountDownLatch(2);  // 두 스레드가 조회까지 끝났음을 알리는 역할
        CountDownLatch latchStart = new CountDownLatch(1);  // 동시에 시작하라는 신호 알리는 역할

        Runnable taskA = () -> {
            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            tx.execute(status -> {
                Pixel pixel = pixelRepository.findById(id).get();
                latchReady.countDown();
                try {
                    latchStart.await();
                    Thread.sleep(300); // A가 늦게 commit되도록
                } catch (InterruptedException ignored) {}
                pixel.updateCrew(1L, LocalDateTime.of(2025, 10, 20, 15, 0));
                pixelRepository.save(pixel);
                return null;
            });
        };

        Runnable taskB = () -> {
            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            tx.execute(status -> {
                Pixel pixel = pixelRepository.findById(id).get();
                latchReady.countDown();
                try {
                    latchStart.await();
                } catch (InterruptedException ignored) {}
                pixel.updateCrew(2L, LocalDateTime.of(2025, 10, 20, 16, 0));
                pixelRepository.save(pixel);
                return null;
            });
        };

        executor.submit(taskA);
        executor.submit(taskB);

        latchReady.await();
        latchStart.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // then
        // B가 출력이 되어야 하는데 A가 더 마지막에 commit 했으므로 A로 덮어씀
        Pixel result = pixelRepository.findById(id).get();
        System.out.println("최종 crewId: " + result.getCrewId());
        System.out.println("최종 updatedAt: " + result.getUpdatedAt());
    }
}
