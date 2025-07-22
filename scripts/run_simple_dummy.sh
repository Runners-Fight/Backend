#!/bin/bash

# 설정
CONTAINER_NAME="runners_db"
DB_NAME="runners"
DB_USER="root"
DB_PASSWORD="runners1234!"

echo "🚀 간단한 더미데이터 생성 시작..."

# Docker 컨테이너 상태 확인
if ! docker ps | grep -q $CONTAINER_NAME; then
    echo "❌ $CONTAINER_NAME 컨테이너가 실행되지 않았습니다."
    exit 1
fi

# 데이터베이스 연결 테스트
if ! docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD -e "USE $DB_NAME;" 2>/dev/null; then
    echo "❌ 데이터베이스 연결에 실패했습니다."
    exit 1
fi

echo "✅ 데이터베이스 연결 성공"

# SQL 실행
echo "📝 더미데이터 생성 중..."
docker exec -i $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME < scripts/simple_dummy_data.sql

if [ $? -eq 0 ]; then
    echo "✅ 더미데이터 생성 완료!"
else
    echo "❌ 더미데이터 생성 중 오류가 발생했습니다."
    exit 1
fi

# 생성된 데이터 확인
echo "📊 생성된 데이터 통계:"
docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "
SELECT 'Members' as Category, COUNT(*) as Total FROM members;
SELECT 'Crews' as Category, COUNT(*) as Total FROM crews;
SELECT 'JoinCrews' as Category, COUNT(*) as Total FROM join_crews;
"

echo "📝 크루별 멤버 수 확인:"
docker exec $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "
SELECT 
    c.name as crew_name,
    COUNT(jc.member_id) as member_count,
    COUNT(CASE WHEN jc.crew_role = 'MANAGER' THEN 1 END) as managers,
    COUNT(CASE WHEN jc.crew_role = 'LEADER' THEN 1 END) as leaders
FROM crews c
LEFT JOIN join_crews jc ON c.id = jc.crew_id
WHERE c.name LIKE '크루_%'
GROUP BY c.id, c.name
ORDER BY c.name;
"

echo "🎉 더미데이터 생성 완료!"
echo "💡 테스트용 ID 예시:"
echo "   크루1 멤버: ID 4-2003 범위"
echo "   크루2 멤버: ID 2004-4003 범위"
