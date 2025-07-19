package run.backend.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;
import run.backend.domain.member.enums.Role;
import run.backend.global.common.BaseEntity;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

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

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profileImage;

    private boolean pushEnabled;

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateAge(int age) {
        this.age = age;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateImage(String imageName) {
        this.profileImage = imageName;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

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
        this.role = Role.NONE;
    }
}
