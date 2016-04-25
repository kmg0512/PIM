# PIM
Personal Intelligent Manager

## 목적
일정 / SNS 등 개인 생활에 필요한 정보들을 통합 관리할 수 있는 어플리케이션을 개발한다.

## 기능
* 일정 관리  
캘린더에 일정을 추가 / 삭제할 수 있고 일이나 주 단위로 추가된 일정을 한 눈에 볼 수 있다
 *	일정 정보  
일정 별로 장소, 시간, 메모 등을 저장할 수 있다.
 *	스마트 일정 알림 기능  
일정이 있기 전에 다음 목적지까지 소모되는 시간을 계산하여, 스마트 알림을 줄 수 있다.
*	SNS 관리  
트위터나 페이스북 등 SNS의 새로운 글을 관리한다.
 * SNS의 글을 모아서 간편하게 볼 수 있다.
 * 같은 글이나 사진을 여러 SNS에 동시에 올릴 수 있다.
* 위젯  
안드로이드 배경화면에서 위젯을 지원하여 높은 접근성을 가질 수 있다.  
위젯은 일정과 SNS의 확인 기능만 갖고 있다.

## 구현 계획
* 사용 프로그래밍 언어 : JAVA
* 실행 환경 : Android Tablet (성우모바일 코넥티아 체리 9.7)
* 라이브러리 및 API  
 * Google Maps Directions API  
현재 위치와 다음 목적지까지의 경로를 파악하는데 사용한다.  
결과가 JSON / XML로 반환된다.
 * Twitter REST APIs  
트위터의 사용자 정보를 가져오고 보내는데 사용한다.
 * Facebook API  
페이스북의 사용자 정보를 가져오고 보내는데 사용한다.
 * GSON  
각종 데이터의 저장, 불러오기, 파싱 및 디버그를 편하게 하기 위해 JSON 라이브러리를 사용한다.
* 추가 구현 계획
 * 메일과 메시지  
SNS뿐만 아니라 메일 및 메시지도 보여주거나, 보낼 수 있게 한다.
 * 사용자 설정 SNS  
트위터와 페이스북만이 아니라, 다른 SNS의 연동기능을 지원한다.
 * 암호화  
SNS의 로그인에 필요한 암호를 암호화하여 자동 로그인 기능을 지원한다.
 * 건강관리  
건강을 잘 관리하는 것도 자기 관리 중 하나이다.  
식습관이나 운동량에 대한 팁을 주거나, 이를 규칙적으로 하게끔 유도한다.


## 차별점
* Google Now
 * 길찾기 기능을 이용한 출발시각을 계산하여 주지만 알림을 주진 않는다.
 * SNS 게시물 등을 확인할 수 없다.
* Flipboard
 * Flipboard는 SNS기능은 지원하지만 각 SNS별로 따로 지원하고 한 번에 볼 수 없다.
 * 일정을 추가하거나, 알림을 받을 수 없다.
 * 위젯으로 SNS를 지원하지 않는다.
## 예상되는 어려움
* Twitter, Facebook의 API를 이용할 때 문제가 생길 수 있다.
* 좋은 디자인을 구성하는데 어려움이 발생할 수 있다.