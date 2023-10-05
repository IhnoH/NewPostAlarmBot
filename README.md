# NewPostAlarmBot

- 어떤 사이트에 특정 게시글이 등록되었는지 확인하는 것에 대해 불편을 느껴 시작하게된 프로젝트입니다.
- 여러 게시글이 등록되어 있는 게시판 주소를 입력하면 해당 게시판의 새 글에 대한 알림을 텔레그램 봇으로 받을 수 있습니다. 또한 알림 중지와 알림 받고 있는 주소 리스트를 볼 수 있는 기능이 있습니다.
- 단순한 새 게시글뿐만 아니라 제목에 특정 단어를 포함한 게시글이 등록되면 알림을 받을 수 있습니다.
- 게시판에서 가장 자주 반복되는 Class를 이용해서 파싱하여 게시글을 인식하고 주기적으로 갱신해서 새 글이 있는지 확인하여 알림을 제공합니다.

# Use
![start](https://github.com/IhnoH/NewPostAlarmBot/assets/26521439/10890fe3-ff7b-4571-bff2-355d8fe60930)
Type '/start' and Copy and paste the URL address you want to be notified

# Development Environment
- Spring Boot
- PostgreSQL
- CloudType
