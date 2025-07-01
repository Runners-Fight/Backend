package run.backend.domain.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.auth.entity.RefreshToken;
import run.backend.domain.member.entity.Member;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.member = :member")
    void deleteByMember(@Param("member") Member member);

}
