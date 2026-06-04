# 버전 관리
- 내가 원하는 시점으로 이동할 수 있게 해주는 것
- 이를 도와주는 툴을 **버전 관리 시스템**

# Git, GitHub이란?
-  내가 원하는 시점마다 깃발을 꽂고, 깃발이 꽂힌 시점으로 자유롭게 이동할 수 있도록 해 주는 소스 코드 버전관리 시스템이 **Git**이다.
- Git을 관리하는 프로젝트를 올려둘 수 있는 대표적인 Git 호스팅 사이트 중 하나가 바로 **GitHub**이다.
- 블로그 -> 네이버, 티스토리, 벨로그,... => Git으로 관리하는 프로젝트를 올릴 수 있는 사이트도 GitHub뿐 아니라 GitLab, BitBucket 등 다양하다.

# 1. Github 가입하기
# 2. Git 설치하기
# 2-1. brew for mac

1. git init
2. git config --global user.email "user@gmail.com"
3. git config --global user.name "DongwoonKim"
4. git config --list
5. git add .
6. git commit -m "설명 추가"
7. git push
8. git clone : 그 문서를 **처음 한 부 통째로 복사해서** 내 책상에 가져오는 것 → 내 컴퓨터에 프로젝트 폴더가 **새로 생긴다.**
9. git pull : 이미 가져온 문서에, 그동안 **다른 사람이 추가·수정한 내용**만 받아서 내 사본에 반영하는 것 → 폴더는 그대로 있고, **내용만 최신으로 갱신**된다.
