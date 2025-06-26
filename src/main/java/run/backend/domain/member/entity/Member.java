package run.backend.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.member.domain.Gender;
import run.backend.domain.member.domain.OAuthType;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int age;

    private String oauthId;

    @Enumerated(EnumType.STRING)
    private OAuthType oauthType;

    private String profileImage;

    private boolean pushEnabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Builder
    public Member(String username, String nickname, Gender gender, int age, String oauthId, OAuthType oauthType, String profileImage) {
        this.username = username;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.oauthId = oauthId;
        this.oauthType = oauthType;
        this.profileImage = profileImage;
        this.pushEnabled = true;
        this.createdAt = LocalDateTime.now();
    }
}
