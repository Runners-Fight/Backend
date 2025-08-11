package run.backend.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;
import run.backend.domain.notification.dto.NotificationItem;
import run.backend.domain.notification.dto.NotificationResponse;
import run.backend.domain.notification.entity.Notification;
import run.backend.domain.notification.enums.MessageType;
import run.backend.domain.notification.exception.NotificationException.InvalidNotificationType;
import run.backend.domain.notification.exception.NotificationException.NotificationNotFound;
import run.backend.domain.notification.mapper.NotificationMapper;
import run.backend.domain.notification.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NotificationService")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService sut; // System Under Test

    private Member receiver;
    private Member sender;
    private Notification crewNotification;
    private Notification battleNotification;
    private Notification readNotification;
    private NotificationItem crewNotificationItem;
    private NotificationItem battleNotificationItem;
    private NotificationItem readNotificationItem;

    @BeforeEach
    void setUp() {
        receiver = createMemberWithId(1L, "수신자");
        sender = createMemberWithId(2L, "발신자");
        
        crewNotification = createNotification(MessageType.CREW, "크루 가입 요청이 왔습니다.", false);
        battleNotification = createNotification(MessageType.BATTLE, "대결 요청이 왔습니다.", false);
        readNotification = createNotification(MessageType.CREW, "읽은 알림입니다.", true);
        
        crewNotificationItem = new NotificationItem("1", "크루", "크루 가입 요청이 왔습니다.", "10분 전");
        battleNotificationItem = new NotificationItem("2", "대결", "대결 요청이 왔습니다.", "30분 전");
        readNotificationItem = new NotificationItem("3", "크루", "읽은 알림입니다.", "1시간 전");
    }

    @Nested
    @DisplayName("getNotifications 메서드는")
    class GetNotificationsTest {

        @Test
        @DisplayName("전체 타입으로 조회 시 모든 읽음/읽지 않음 알림을 반환한다")
        void shouldReturnAllNotificationsWhenTypeIsAll() {
            // given
            List<Notification> readNotifications = List.of(readNotification);
            List<Notification> unreadNotifications = List.of(crewNotification, battleNotification);
            List<NotificationItem> readItems = List.of(readNotificationItem);
            List<NotificationItem> unreadItems = List.of(crewNotificationItem, battleNotificationItem);

            given(notificationRepository.findReadNotificationsByMember(receiver))
                .willReturn(readNotifications);
            given(notificationRepository.findUnreadNotificationsByMember(receiver))
                .willReturn(unreadNotifications);
            given(notificationMapper.toNotificationItemList(readNotifications))
                .willReturn(readItems);
            given(notificationMapper.toNotificationItemList(unreadNotifications))
                .willReturn(unreadItems);

            // when
            NotificationResponse response = sut.getNotifications(receiver, "all");

            // then
            assertThat(response.read()).hasSize(1);
            assertThat(response.unread()).hasSize(2);
            assertThat(response.read()).containsExactly(readNotificationItem);
            assertThat(response.unread()).containsExactly(crewNotificationItem, battleNotificationItem);

            then(notificationRepository).should().findReadNotificationsByMember(receiver);
            then(notificationRepository).should().findUnreadNotificationsByMember(receiver);
            then(notificationMapper).should().toNotificationItemList(readNotifications);
            then(notificationMapper).should().toNotificationItemList(unreadNotifications);
        }

        @Test
        @DisplayName("크루 타입으로 조회 시 크루 관련 알림만 반환한다")
        void shouldReturnCrewNotificationsWhenTypeIsCrew() {
            // given
            List<Notification> readNotifications = List.of(readNotification);
            List<Notification> unreadNotifications = List.of(crewNotification);
            List<NotificationItem> readItems = List.of(readNotificationItem);
            List<NotificationItem> unreadItems = List.of(crewNotificationItem);

            given(notificationRepository.findReadNotificationsByMemberAndType(receiver, MessageType.CREW))
                .willReturn(readNotifications);
            given(notificationRepository.findUnreadNotificationsByMemberAndType(receiver, MessageType.CREW))
                .willReturn(unreadNotifications);
            given(notificationMapper.toNotificationItemList(readNotifications))
                .willReturn(readItems);
            given(notificationMapper.toNotificationItemList(unreadNotifications))
                .willReturn(unreadItems);

            // when
            NotificationResponse response = sut.getNotifications(receiver, "crew");

            // then
            assertThat(response.read()).hasSize(1);
            assertThat(response.unread()).hasSize(1);
            assertThat(response.read()).containsExactly(readNotificationItem);
            assertThat(response.unread()).containsExactly(crewNotificationItem);

            then(notificationRepository).should().findReadNotificationsByMemberAndType(receiver, MessageType.CREW);
            then(notificationRepository).should().findUnreadNotificationsByMemberAndType(receiver, MessageType.CREW);
        }

        @Test
        @DisplayName("대결 타입으로 조회 시 대결 관련 알림만 반환한다")
        void shouldReturnBattleNotificationsWhenTypeIsBattle() {
            // given
            List<Notification> readNotifications = List.of();
            List<Notification> unreadNotifications = List.of(battleNotification);
            List<NotificationItem> readItems = List.of();
            List<NotificationItem> unreadItems = List.of(battleNotificationItem);

            given(notificationRepository.findReadNotificationsByMemberAndType(receiver, MessageType.BATTLE))
                .willReturn(readNotifications);
            given(notificationRepository.findUnreadNotificationsByMemberAndType(receiver, MessageType.BATTLE))
                .willReturn(unreadNotifications);
            given(notificationMapper.toNotificationItemList(readNotifications))
                .willReturn(readItems);
            given(notificationMapper.toNotificationItemList(unreadNotifications))
                .willReturn(unreadItems);

            // when
            NotificationResponse response = sut.getNotifications(receiver, "battle");

            // then
            assertThat(response.read()).isEmpty();
            assertThat(response.unread()).hasSize(1);
            assertThat(response.unread()).containsExactly(battleNotificationItem);

            then(notificationRepository).should().findReadNotificationsByMemberAndType(receiver, MessageType.BATTLE);
            then(notificationRepository).should().findUnreadNotificationsByMemberAndType(receiver, MessageType.BATTLE);
        }

        @Test
        @DisplayName("대소문자 구분 없이 타입을 처리한다")
        void shouldHandleTypesCaseInsensitively() {
            // given
            List<Notification> readNotifications = List.of();
            List<Notification> unreadNotifications = List.of(crewNotification);
            List<NotificationItem> readItems = List.of();
            List<NotificationItem> unreadItems = List.of(crewNotificationItem);

            given(notificationRepository.findReadNotificationsByMemberAndType(receiver, MessageType.CREW))
                .willReturn(readNotifications);
            given(notificationRepository.findUnreadNotificationsByMemberAndType(receiver, MessageType.CREW))
                .willReturn(unreadNotifications);
            given(notificationMapper.toNotificationItemList(readNotifications))
                .willReturn(readItems);
            given(notificationMapper.toNotificationItemList(unreadNotifications))
                .willReturn(unreadItems);

            // when
            NotificationResponse response = sut.getNotifications(receiver, "CREW");

            // then
            assertThat(response.unread()).hasSize(1);
            then(notificationRepository).should().findReadNotificationsByMemberAndType(receiver, MessageType.CREW);
            then(notificationRepository).should().findUnreadNotificationsByMemberAndType(receiver, MessageType.CREW);
        }

        @Test
        @DisplayName("빈 결과를 올바르게 처리한다")
        void shouldHandleEmptyResultsCorrectly() {
            // given
            List<Notification> emptyNotifications = List.of();
            List<NotificationItem> emptyItems = List.of();

            given(notificationRepository.findReadNotificationsByMember(receiver))
                .willReturn(emptyNotifications);
            given(notificationRepository.findUnreadNotificationsByMember(receiver))
                .willReturn(emptyNotifications);
            given(notificationMapper.toNotificationItemList(emptyNotifications))
                .willReturn(emptyItems);

            // when
            NotificationResponse response = sut.getNotifications(receiver, "all");

            // then
            assertThat(response.read()).isEmpty();
            assertThat(response.unread()).isEmpty();
        }

        @Test
        @DisplayName("유효하지 않은 타입일 때 InvalidNotificationType 예외를 발생시킨다")
        void shouldThrowInvalidNotificationTypeWhenTypeIsInvalid() {
            // when & then
            assertThatThrownBy(() -> sut.getNotifications(receiver, "invalid"))
                .isInstanceOf(InvalidNotificationType.class);

            then(notificationRepository).shouldHaveNoInteractions();
            then(notificationMapper).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("markAsRead 메서드는")
    class MarkAsReadTest {

        @Test
        @DisplayName("알림을 읽음 상태로 변경한다")
        void shouldMarkNotificationAsRead() {
            // given
            given(notificationRepository.findByIdAndReceiver(1L, receiver))
                .willReturn(Optional.of(crewNotification));

            // when
            sut.markAsRead(1L, receiver);

            // then
            then(notificationRepository).should().findByIdAndReceiver(1L, receiver);
        }

        @Test
        @DisplayName("존재하지 않는 알림이거나 다른 사용자의 알림일 때 NotificationNotFound 예외를 발생시킨다")
        void shouldThrowNotificationNotFoundWhenNotificationDoesNotExistOrNotOwned() {
            // given
            given(notificationRepository.findByIdAndReceiver(999L, receiver))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.markAsRead(999L, receiver))
                .isInstanceOf(NotificationNotFound.class);
        }
    }

    @Nested
    @DisplayName("markAllAsRead 메서드는")
    class MarkAllAsReadTest {

        @Test
        @DisplayName("모든 읽지 않은 알림을 읽음 상태로 변경한다")
        void shouldMarkAllUnreadNotificationsAsRead() {
            // given
            List<Notification> unreadNotifications = List.of(crewNotification, battleNotification);
            
            given(notificationRepository.findUnreadNotificationsByMember(receiver))
                .willReturn(unreadNotifications);

            // when
            sut.markAllAsRead(receiver);

            // then
            then(notificationRepository).should().findUnreadNotificationsByMember(receiver);
        }

        @Test
        @DisplayName("읽지 않은 알림이 없을 때도 정상 처리된다")
        void shouldHandleEmptyUnreadNotifications() {
            // given
            List<Notification> emptyNotifications = List.of();
            
            given(notificationRepository.findUnreadNotificationsByMember(receiver))
                .willReturn(emptyNotifications);

            // when
            sut.markAllAsRead(receiver);

            // then
            then(notificationRepository).should().findUnreadNotificationsByMember(receiver);
        }
    }

    private Member createMemberWithId(Long id, String nickname) {
        Member member = Member.builder()
            .username("test_user")
            .nickname(nickname)
            .gender(Gender.MALE)
            .age(25)
            .oauthId("oauth_id_" + id)
            .oauthType(OAuthType.GOOGLE)
            .profileImage("profile.jpg")
            .build();

        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Notification createNotification(MessageType messageType, String message, boolean isRead) {
        Notification notification = Notification.builder()
            .message(message)
            .messageType(messageType)
            .targetId(1L)
            .sender(sender)
            .receiver(receiver)
            .build();

        ReflectionTestUtils.setField(notification, "id", System.currentTimeMillis());
        ReflectionTestUtils.setField(notification, "isRead", isRead);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now().minusMinutes(10));

        return notification;
    }
}
