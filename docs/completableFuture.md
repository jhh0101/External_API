# CompletableFuture(비동기 실행)
- supplyAsync: 시작 - 결과 리턴
- runAsync: 시작 - 결과 리턴 없음
- thenApply: 가공 - 받은 리턴 값으로 로직 실행 후 결과 리턴
- thenAccept: 소비 - 받은 리턴 값으로 로직 실행 후 결과 리턴 안함
- exceptionally: 복구 - 가공, 소비 실패 시 대체값 지정
- whenComplete: 마무리 - 성공이든 실패든 무조건 실행
- allOf / join: 대기 - 실행중인 로직을 하나로 묶음