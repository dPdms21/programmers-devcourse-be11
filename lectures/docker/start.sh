#!/bin/bash
# #!/bin/bash: 셔뱅이라고 함. 이 파일을 /bin/bash로 실행하라는 의미
# 실행 방법: ./start.sh

# -e: 명령어가 실패하면 스크립트 실행을 중단함
# data Compose 명령어가 실패했을 때 다음 단계인 service를 실행하지 않도록 설정
# 단, up -d 성공이 컨테이너 내부 서비스의 준비 완료를 의미하지는 않음
set -e

# 쉘 스크립트란: 터미널에 하나씩 치던 명령어를 파일에 모아 한 번에 실행하는 것

# 쉘의 위치를 이 스크립트가 있는 폴더로 이동
# $( ): 명령어 치환. 안의 명령어를 실행하고, 그 자리를 안의 명령어의 결과로 대체
# $0: 실행된 스크립트 경로 -> /d/dev/programmers-devcourse-be11/lectures/docker/start.sh
# dirname: 경로에서 디렉토리만 추출 -> /d/dev/programmers-devcourse-be11/lectures/docker
cd "$(dirname "$0")" # => cd "/d/dev/programmers-devcourse-be11/lectures/docker"

echo "[1/3] 데이터: msa-network 생성 + MySQL"
docker compose -f docker-compose.data.yml up -d

echo
echo "[2/3] 서비스: config -> auth/board/edge"
docker compose -f docker-compose.service.yml up -d --build

echo
echo "[3/3] 프론트: web"
docker compose -f docker-compose.front.yml up -d --build

# 실행 중인 컨테이너를 표로 출력
# - --format: 출력 형식을 직접 지정
# - "table": 맨 앞에 쓰면 머리글(Names, Status, Ports)을 붙이고 열을 맞춰줌
# - {{}}: Go 템플릿 문법. 컨테이너 정보를 꺼냄
# .Names 이름 / .Status 상태 / .Ports 포트 매핑
# -> docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo
echo "=== 실행 상태 ==="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
