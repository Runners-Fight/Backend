# 🏃‍♂️ Runners Fight
### 러너들끼리의 뜨거운 경쟁을 위한 러닝 배틀 플랫폼

---

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)

> **러너들 간의 선의의 경쟁을 통해 성취감과 재미를 극대화하는 러닝 크루 배틀 시스템**  
> 이곳에서 승리는 기록을 넘어 **러너로서의 성장을 의미**합니다.

## 📖 개요

**Runners Fight**는 개인이 아닌 **러닝 크루**에 초점을 맞춘 혁신적인 러닝 플랫폼입니다.

### 🎯 핵심 아이디어
- **러닝 크루 vs 러닝 크루**: 크루 간의 경쟁을 중심으로 한 배틀 시스템
- **땅따먹기 시스템**: 크루가 달린 루트에 구역 확보 및 재획득 메커니즘 적용


### 💡 기획 배경

최근 **운동 부족**과 **건강**에 대한 관심이 증가하면서 러닝을 시작하는 사람들이 늘고 있습니다. 하지만 대부분의 러너들은 시간이 지나면서 **운동의 재미**나 **목표 달성**에 대한 동기를 잃어가곤 합니다.

이에 우리는 게이미피케이션을 통해 **러닝을 더 재미있게 만들 수 있는 방법**을 고민했습니다. 운동이 단순한 반복적인 활동이 아닌, **흥미롭고 도전적인 게임**이 되기를 바랐습니다.

## ⭐ 주요 기능

### 🏆 크루 배틀 시스템
- **크루 vs 크루** 경쟁을 통한 지속적인 동기부여
- **땅따먹기 시스템**으로 실시간 지도 기반 구역 점령
- **랭킹 시스템**: 월간 크루 랭킹 및 개인 기록 관리
- 크루원들의 기록이 합쳐져 크루 성과로 집계

### 👥 소셜 기능
- **크루 생성 및 가입**: 러닝 크루 생성, 검색, 가입 신청
- **친구 시스템**: 친구 추가 및 실시간 러닝 상태 확인
- **실시간 알림**: 크루 활동 및 배틀 상황 알림

### 📊 러닝 기록 관리
- **개인 러닝 기록**: GPS 기반 루트 추적 및 통계
- **크루 통합 기록**: 크루원들의 기록 통합 관리
- **시각화**: 지도 기반 루트 표시 및 성과 차트

## 🏗 시스템 아키텍처

### 📁 프로젝트 구조
```
Backend/
├── src/main/java/run/backend/
│   ├── BackendApplication.java
│   ├── domain/
│   │   ├── auth/           # 인증 & OAuth2
│   │   ├── member/         # 회원 관리
│   │   ├── crew/           # 크루 관리
│   │   ├── event/          # 러닝 이벤트
│   │   ├── running/        # 러닝 기록
│   │   ├── notification/   # 알림
│   │   ├── record/         # 기록 관리
│   │   └── file/           # 파일 업로드
│   └── global/
│       ├── config/         # 설정 파일
│       ├── security/       # 보안 설정
│       ├── oauth2/         # OAuth2 처리
│       ├── exception/      # 예외 처리
│       └── common/         # 공통 유틸리티
└── src/test/java/          # 테스트 코드
```

### 🛠 기술 스택

#### Backend
- **Framework**: Spring Boot 3.4.5
- **Language**: Java 17
- **Security**: Spring Security + JWT + OAuth2
- **Database**: MySQL + Spring Data JPA
- **Query**: QueryDSL
- **Documentation**: Swagger (SpringDoc OpenAPI)

#### Development Tools
- **Build Tool**: Gradle
- **Test**: JUnit 5, Mockito
- **Code Coverage**: JaCoCo

## 🎯 차별점

| 구분 | Runners Fight | 타 러닝 서비스     |
|------|---------------|--------------|
| **재미 요소** | 땅따먹기 시스템을 통한 실시간 크루 경쟁 | 개인 기록 기반의 경쟁 |
| **개인 성장** | 크루 단위 목표 달성으로 개인 성과가 크루 성과로 연결 | 개인 기록 향상 중심  |
| **지역성** | 실시간 지도 기반 땅따먹기, 지역 기반 러닝 커뮤니티 | 없음           |
| **동기 부여** | 크루 내 실시간 소통과 성과 공유 | 개인 기록 기반     |
| **협동심** | 크루 단위의 협력과 경쟁을 통한 공동 목표 달성 | 개인 성장 중심     |



---

**Runners Fight**와 함께 러닝의 새로운 재미를 경험해보세요! 🏃‍♂️🔥
