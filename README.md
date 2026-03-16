# 🗺️ Spring 외부 API 통신 3대장 마스터 로드맵
### Chapter 1. 통신의 근본, RestTemplate (동기식 클래식 무기)
가장 오랫동안 쓰여 온 표준 기술로, HTTP 통신의 기본 뼈대(Header, Body, Method)를 직접 조립하는 방법을 배웁니다.
학습 내용: exchange()와 getForObject()의 차이, HttpHeaders와 HttpEntity를 활용한 요청 객체 수동 조립, 동기(Blocking) 통신의 흐름 이해.    
실무 예시: 옥션 프로젝트에서 "실시간 환율 정보"나 "공공 API(주소 검색 등)" 같은 단순한 외부 데이터를 가져올 때, 코드가 위에서 아래로 직관적으로 흐르도록 RestTemplate으로 통신해 보기.

### Chapter 2. 패러다임의 전환, WebClient (비동기식 레이저건)
예전에 겪었던 그 '개고생'의 정체, 스레드를 멈추지 않고(Non-blocking) 던져두는 리액티브 프로그래밍의 맛을 다시 봅니다.
학습 내용: Mono와 Flux의 기본 개념, retrieve()와 exchangeToMono()의 차이, 외부 서버가 느릴 때 내 서버 스레드가 같이 멈추는 현상(Thread Lock) 방지.    
실무 예시: 경매 낙찰이 완료된 후 "SMS 문자 발송 API"나 "이메일 전송 API"를 호출할 때. 외부 메일 서버가 응답이 느려도, 내 서버는 WebClient로 비동기 요청만 툭 던져놓고 바로 다음 로직(DB 업데이트 등)을 처리하러 가기.

### hapter 3. 선언적 통신의 마법, OpenFeign (MSA 실무 최애 무기)
지저분한 통신 세팅 코드를 싹 걷어내고, 인터페이스 하나로 통신을 자동화하는 현대적인 방식을 배웁니다.
학습 내용: @FeignClient 선언 방법, Spring MVC 어노테이션(@GetMapping, @RequestBody 등)을 그대로 재사용하는 인터페이스 매핑 로직, 내부 비즈니스 로직과 외부 통신 로직의 완벽한 분리(결합도 낮추기). 
실무 예시: 토스 페이먼츠 결제 API 연동 시. 복잡한 통신 코드 없이 TossPaymentClient라는 인터페이스만 딱 만들어두고, 컨트롤러에서 서비스 메서드 호출하듯이 아주 우아하고 깔끔하게 결제 승인 요청 보내기.

### Chapter 4. 3대장의 예외 처리 통합 (방패와 창의 결합)
앞서 만든 [글로벌 예외 처리 방패]를 통신 3대장과 완벽하게 연결하여, 외부 API가 터졌을 때 내 서버를 지키는 방법을 배웁니다.      
학습 내용: ResponseErrorHandler(RestTemplate), onStatus()(WebClient), ErrorDecoder(OpenFeign)를 활용한 각 무기별 커스텀 에러 가로채기.       
실무 예시: 토스 서버가 죽어서 500 에러를 뱉었을 때, OpenFeign의 ErrorDecoder가 그걸 낚아채서 우리가 만든 CustomException(TOSS_SERVER_ERROR)으로 변환해 던지고, 글로벌 핸들러가 받아 MDC 로그(TraceID)와 함께 예쁘게 포장하기.

### Chapter 5. 모의 서버(Mock Server)로 한계 돌파하기
외부 서버가 뻗는 상황을 내가 마음대로 조작해서, 내 롤백 로직이 진짜로 작동하는지 검증하는 방식을 배웁니다.   
학습 내용: WireMock 또는 Spring의 @RestClientTest를 활용한 가짜(Mock) 외부 API 서버 띄우기.     
실무 예시: 진짜 토스 API를 찌르는 대신 가짜 토스 서버를 띄워두고 무조건 "잔액 부족(400)" 에러를 뱉게 조작한 뒤, 내 OrderService의 @Transactional이 정상적으로 작동해서 DB에 주문 내역을 롤백하는지 테스트 코드로 증명해 내기.