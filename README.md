# Spring Batch

### 환경

> Spring Boot 3.3.1
> 
> Spring Batch 5.X 
> 
> Spring Data JPA - MySQL
>
> JDBC API - MySQL
>
> Lombok
>
> Gradle-Groovy
>
> Java 17 ~

- 배치
    - 일정 시간 동안 대량의 데이터를 한 번에 처리하는 방식
    - 은행 이자 지급 등

- 배치 프레임워크를 사용하는 이유
    - 처리중 프로그램이 멈추는 상황을 대비한 안전 장치가 필요하기 때문
    - 10만개의 데이터 작업이 중단됐을때 작업 지점이 기록되어야 필요한 부분부터 다시 할 수 있음
    - 이미 처리한 서비스를 재 실행하는 불상사를 막기 위해 사용함



- 메타테이블
    - 배치에대한 모든 정보 저장 (어디까지 실행했는지 등, 중복 동작을 막을 수 있다)
    - JDBC로 관리 (속도가 빠름)
- 운영테이블
    - 운영 데이터 테이블

**배치 과정**

1. ItemReader
    1. 읽어오기
2. ItemProcessor
    1. 처리하기
3. ItemWriter
    1. 쓰기

**읽어오기와 쓰기의 테이블이 다를 수 있다**

ㄴ 우리 시스템에서는 동일할듯

**데이터를 빠르게처리 + 어디까지 했는지 파악 + 중복동작을 막기**

→ 기록이 가장 중요하다! 메타 데이터 테이블!

**읽기 과정에서 한번에 전체를 읽지 않는다**

→ 데이터 손실에 대비하여

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fmt5XM%2FbtrMvMVRYU2%2FGpTg0S08ycoTBkTziRkUO1%2Fimg.png)

- JobLauncher
    - 하나의 배치 작업 실행점
- Job
    - 하나의 배치 작업
- Step
    - Reader - Processor -Writer 작업을 정의
    - Job 1 : N Step
- JobRepository
    - 메타 데이터 기록

### metadata database

org.springframework.batch:spring-batch-core:version > spring-batch-core-version.jar > org.springframework.batch.core > schema-DB.sql 에 의해 생성됨

![](https://docs.spring.io/spring-batch/reference/_images/meta-data-erd.png)

## 배치 구성

### OldUsers

매달 1일 실행

- user
  - 조건 : modifiedAt 3년 전인 경우
  - isDeleted true 변경
  - 연결된 예매내역 삭제
    - 예매내역에 연결된 좌석 삭제
    - 예매내역에 연결된 결제 삭제
    - 예매내역에 연결된 리뷰 삭제
    - 예매내역에 연결된 사용 쿠폰 삭제

### OldUsersAlert

매달 1일 실행

- Alert
  - 조건 : modifiedAt 2년 전인 경우

![](https://postfiles.pstatic.net/MjAyNDExMTFfMjQ0/MDAxNzMxMjk1MTgwMjA4.R2-elCOBEGLrwQLGxU3E2AIRrgch8QcJldtRWKaek7Ig.nkod2MXxAbMWxxKNdXhKPZFz4jjQvKqQElgM_4iINuwg.JPEG/%ED%99%94%EB%A9%B4_%EC%BA%A1%EC%B2%98_2024-11-11_121622.jpg?type=w966)

### OldPerformances

매달 1일 실행

- performance
  - 조건 : endDate 10년 전인 경우
  - performance 삭제
  - 연결된 예매내역 삭제
    - 예매내역에 연결된 좌석 삭제
    - 예매내역에 연결된 결제 삭제
    - 예매내역에 연결된 리뷰 삭제
    - 예매내역에 연결된 사용 쿠폰 삭제
  - 연결된 회차 삭제
  - 연결된 출연자 삭제
  - 연결된 포스터 삭제

### TimeoutReservation

매일 자정 실행

- reservation
  - 조건 : 예매 날짜가 어제인 경우 AND 결제 정보 없음 AND PENDING 상태
  - 예매 취소 처리
  - 예매 좌석 삭제
  - 결제 정보 취소처리
  - 사용 쿠폰 삭제

### UpcomingReservationAlert

매일 오전 10시

- Alert
  - 조건 : reservation - round의 날짜가 내일인경우

~~아이디어~~

~~-> 알람 전송용 Dto를 추가로 만들자!~~

~~userName, performanceTitle, date, hallName~~
~~userId, performanceId, roundId, hallId도 있어야겠다~~

~~이 정보를 채우기 위해 reservation 먼저 조회해서 userId, RoundId, performanceId를 채움~~

~~user를 조회해서 userName를 채움~~

~~performance를 조회해서 performanceTitle, hallId를 채움~~

~~round를 조회해서 date를 채움~~

~~hall을 조회해서 hallName을 채움~~

~~마지막 step으로 알람을 보내면 되지 않을까?~~

-> JDBC JOIN 처리하면 됨!

![](https://postfiles.pstatic.net/MjAyNDExMTFfODEg/MDAxNzMxMjk1MTgwMjA4.XEKutIMKumOKYq0fk4-3g9I0r0eMN_wQCJpmmQvMowAg.mYq0bMdnuNM8vC12Iwu8460VO1J_O2A-iFhWfskz5ecg.JPEG/%ED%99%94%EB%A9%B4_%EC%BA%A1%EC%B2%98_2024-11-11_121651.jpg?type=w966)

### ReservationReviewAlert

매일 저녁 9시

- Alert
  - 조건 : reservation - round의 날짜가 오늘인경우

![](https://postfiles.pstatic.net/MjAyNDExMTFfMTE1/MDAxNzMxMjk1MTgwMjA4.REheajdBXp-ws97iU_dV45TMvMD9Ri500qZ9HCbrnmwg.KYRJk8etTKsie37Nt4TAc4QsuJV5jVtigvIn21ZcnFcg.JPEG/%ED%99%94%EB%A9%B4_%EC%BA%A1%EC%B2%98_2024-11-11_121710.jpg?type=w966)
