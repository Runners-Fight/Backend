package run.backend.domain.running.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.running.dto.request.Coordinate;
import run.backend.domain.running.entity.Pixel;
import run.backend.domain.running.entity.PixelId;
import run.backend.domain.running.exception.RunningException;
import run.backend.domain.running.repository.PixelRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RunningService {

    public static final double SEOUL_AREA_NORTH = 37.715133;
    public static final double SEOUL_AREA_SOUTH = 37.413294;
    public static final double SEOUL_AREA_WEST = 126.734086;
    public static final double SEOUL_AREA_EAST = 127.269311;

    public static final int GRID_WIDTH = 939;
    public static final int GRID_HEIGHT = 671;

    private final PixelRepository pixelRepository;

    @Transactional
    public void processRunningRoute(Long crewId, List<Coordinate> coordinates) {
        // 1. 좌표를 픽셀로 변환하고 매핑 정보 저장 (순서 유지)
        Map<PixelId, Coordinate> pixelToCoordMap = new LinkedHashMap<>();
        for (Coordinate coord : coordinates) {
            int[] pixelPos = toPixel(coord.latitude(), coord.longitude());
            PixelId id = new PixelId(pixelPos[0], pixelPos[1]);
            
            // 같은 픽셀에 여러 좌표가 있으면 가장 최신 시간만 유지
            Coordinate existing = pixelToCoordMap.get(id);
            if (existing == null || coord.timestamp().isAfter(existing.timestamp())) {
                pixelToCoordMap.put(id, coord);
            }
        }

        // 2. 픽셀 ID를 정렬 (데드락 방지)
        List<PixelId> sortedPixelIds = pixelToCoordMap.keySet().stream()
                .sorted()
                .toList();

        // 3. 정렬된 순서로 락 획득 및 업데이트
        List<Pixel> pixelsToSave = new ArrayList<>();
        for (PixelId id : sortedPixelIds) {
            Coordinate coord = pixelToCoordMap.get(id);
            
            Pixel pixel = pixelRepository.findByIdWithLock(id)
                    .orElseGet(() -> new Pixel(id, crewId, coord.timestamp()));

            // 기존 Pixel의 updatedAt이 더 최신이면 skip
            if (pixel.getUpdatedAt() != null && pixel.getUpdatedAt().isAfter(coord.timestamp())) {
                continue;
            }

            pixel.updateCrew(crewId, coord.timestamp());
            pixelsToSave.add(pixel);
        }

        pixelRepository.saveAll(pixelsToSave);
    }

    // latitude : 위도(y), longitude : 경도(x)
    public int[] toPixel(double latitude, double longitude) {

        if (latitude < SEOUL_AREA_SOUTH || latitude > SEOUL_AREA_NORTH ||
                longitude < SEOUL_AREA_WEST || longitude > SEOUL_AREA_EAST) {
            throw new RunningException.InvalidCoordinate();
        }

        // 서울 범위 내 비율 계산
        double latRatio = (latitude - SEOUL_AREA_SOUTH) / (SEOUL_AREA_NORTH - SEOUL_AREA_SOUTH);
        double lonRatio = (longitude - SEOUL_AREA_WEST) / (SEOUL_AREA_EAST - SEOUL_AREA_WEST);

        // 픽셀 좌표 변환
        int x = (int) Math.round(lonRatio * GRID_WIDTH);
        int y = (int) Math.round((1 - latRatio) * GRID_HEIGHT);

        return new int[]{x, y};
    }
}
