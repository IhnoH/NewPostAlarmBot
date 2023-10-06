# NewPostAlarmBot

- 어떤 사이트에 특정 게시글이 등록되었는지 확인하는 것에 대해 불편을 느껴 시작하게된 프로젝트입니다.
- 게시판에서 가장 자주 반복되는 Class를 이용해서 파싱하여 게시글을 인식하고 주기적으로 갱신해서 새 글이 있는지 확인하여 알림을 제공합니다.

# Use
![start](https://github.com/IhnoH/NewPostAlarmBot/assets/26521439/10890fe3-ff7b-4571-bff2-355d8fe60930)
- '/start'를 입력하고 알림받기를 원하는 주소를 복사, 붙여넣기
- 혹은 'URL keyword' 형식으로 입력하면 키워드를 포함하는 게시글만 알림받을 수 있다.

![주소목록](https://github.com/IhnoH/NewPostAlarmBot/assets/26521439/fc263639-28c4-4e88-bc39-39aca237289b)
- 알림받고 있는 주소 목록을 누르면 입력된 주소 목록이 나온다
- 각 주소를 누르면 키워드를 포함하는 게시글 알림을 받을 수 있도록 키워드 추가 및 삭제가 가능하고 알림 중지도 여기서 가능하다

- 그 외 URL 선택중지, 일괄중지 메뉴가 있다

# Development Environment
- Spring Boot
- Telegram Bot
- PostgreSQL
- CloudType
