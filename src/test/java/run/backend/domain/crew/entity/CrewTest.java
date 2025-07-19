package run.backend.domain.crew.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Crew 도메인 테스트")
public class CrewTest {

    @Test
    @DisplayName("이름을 변경하면 name 필드가 변경된다.")
    void updateName() {

        // given
        Crew crew = new Crew();
        String newName = "크루 새로운 이름";

        // when
        crew.updateName(newName);

        // then
        assertThat(crew.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("설명을 변경하면 description 필드가 변경된다.")
    void updateDescription() {

        // given
        Crew crew = new Crew();
        String newDescription = "새로운 설명";

        // when
        crew.updateDescription(newDescription);

        // then
        assertThat(crew.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("사진을 변경하면 image 필드가 변경된다.")
    void updateImage() {

        // given
        Crew crew = new Crew();
        String newImage = "image123.png";

        // when
        crew.updateImage(newImage);

        // then
        assertThat(crew.getImage()).isEqualTo(newImage);
    }

    @Test
    @DisplayName("crew를 생성하면 invite-code가 자동으로 생성된다.")
    void generateInviteCode_whenCrewCreated() {

        // when
        Crew crew = Crew.builder()
                .name("테스트 크루")
                .description("설명")
                .image("image.png")
                .build();

        // then
        assertThat(crew.getInviteCode()).isNotNull();
        assertThat(crew.getInviteCode()).isNotBlank();
    }
}
