#!/bin/bash
# 사용법
# ./stop.sh: 컨테이너만 종료하고 볼륨은 유지
# ./stop.sh -v: Compose에서 관리하는 볼륨까지 삭제 (MySQL 데이터, Board 업로드 데이터 포함)
set -e

cd "$(dirname "$0")"

VOLUMES=""

if [ "$1" = "-v" ]; then
    VOLUMES="-v"
fi

echo "[1/3] 프론트: web"
docker compose -f docker-compose.front.yml down $VOLUMES

echo "[2/3] 서비스: config / auth / board / edge"
docker compose -f docker-compose.service.yml down $VOLUMES

echo "[3/3] 데이터: MySQL + msa-network"
docker compose -f docker-compose.data.yml down $VOLUMES

echo
echo "=== 실행 상태 ==="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo
echo "=== 남은 볼륨 ==="
docker volume ls
