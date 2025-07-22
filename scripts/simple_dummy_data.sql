-- 간단한 더미데이터 생성 (크루 5개, 멤버 10,000명)
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

-- 1. 크루 5개 생성
INSERT INTO crews (name, description, image, invite_code, member_count, monthly_distance_total, monthly_time_total, monthly_score_total, created_at, updated_at) VALUES
('크루_001', '첫 번째 러닝 크루입니다', 'crew1.jpg', 'INVITE_001', 2000, 1000.50, 50000, 500.25, NOW(), NOW()),
('크루_002', '두 번째 러닝 크루입니다', 'crew2.jpg', 'INVITE_002', 2000, 1200.75, 60000, 600.50, NOW(), NOW()),
('크루_003', '세 번째 러닝 크루입니다', 'crew3.jpg', 'INVITE_003', 2000, 1100.25, 55000, 550.75, NOW(), NOW()),
('크루_004', '네 번째 러닝 크루입니다', 'crew4.jpg', 'INVITE_004', 2000, 1300.00, 65000, 650.00, NOW(), NOW()),
('크루_005', '다섯 번째 러닝 크루입니다', 'crew5.jpg', 'INVITE_005', 2000, 1150.80, 57500, 575.60, NOW(), NOW());

-- 2. 멤버 10,000명 생성
INSERT INTO members (username, nickname, gender, age, oauth_id, oauth_type, role, profile_image, push_enabled, created_at, updated_at)
SELECT 
    CONCAT('user_', LPAD(seq, 6, '0')) as username,
    CONCAT('유저_', seq) as nickname,
    CASE WHEN seq % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END as gender,
    18 + (seq % 42) as age,
    CONCAT('oauth_', seq) as oauth_id,
    'GOOGLE' as oauth_type,
    CASE 
        WHEN seq % 100 = 1 THEN 'MANAGER'
        WHEN seq % 200 = 1 THEN 'LEADER' 
        ELSE 'MEMBER' 
    END as role,
    CONCAT('profile_', (seq % 20) + 1, '.jpg') as profile_image,
    true as push_enabled,
    NOW() as created_at,
    NOW() as updated_at
FROM (
    SELECT (@row_number := @row_number + 1) AS seq
    FROM information_schema.columns c1
    CROSS JOIN information_schema.columns c2
    CROSS JOIN (SELECT @row_number := 0) r
    LIMIT 10000
) AS numbers;

-- 3. 크루 가입 관계 생성
-- 1~2000번: 1번 크루
-- 2001~4000번: 2번 크루  
-- 4001~6000번: 3번 크루
-- 6001~8000번: 4번 크루
-- 8001~10000번: 5번 크루

INSERT INTO join_crews (join_status, crew_role, joined_date, member_id, crew_id, created_at, updated_at)
SELECT 
    'APPROVED' as join_status,
    CASE 
        WHEN m.id % 100 = 1 THEN 'MANAGER'
        WHEN m.id % 200 = 1 THEN 'LEADER'
        ELSE 'MEMBER'
    END as crew_role,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 365) DAY) as joined_date,
    m.id as member_id,
    CASE 
        WHEN m.id <= (SELECT MIN(id) + 1999 FROM members WHERE username LIKE 'user_%') THEN (SELECT MIN(id) FROM crews WHERE name LIKE '크루_%') + 0
        WHEN m.id <= (SELECT MIN(id) + 3999 FROM members WHERE username LIKE 'user_%') THEN (SELECT MIN(id) FROM crews WHERE name LIKE '크루_%') + 1
        WHEN m.id <= (SELECT MIN(id) + 5999 FROM members WHERE username LIKE 'user_%') THEN (SELECT MIN(id) FROM crews WHERE name LIKE '크루_%') + 2
        WHEN m.id <= (SELECT MIN(id) + 7999 FROM members WHERE username LIKE 'user_%') THEN (SELECT MIN(id) FROM crews WHERE name LIKE '크루_%') + 3
        ELSE (SELECT MIN(id) FROM crews WHERE name LIKE '크루_%') + 4
    END as crew_id,
    NOW() as created_at,
    NOW() as updated_at
FROM members m
WHERE m.username LIKE 'user_%';

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
