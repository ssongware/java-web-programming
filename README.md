java-web-programming
====================

자바에서 자바 기반 웹 프로그래밍을 다룬다.

1. webapp/WEB-INF/web.xml에서 맵핑된 listener 실행
  - ServletContextLoader에서 RequestMapping의 initMapping()메서드 실행하고
    ServletContext에 DEFAULT_REQUEST_MAPPING 저장
  - QnaContextLoader가 qna.sql을 읽어서 DB에 기본 데이터들을 입력
 
 2. welcome-file인 index.jsp로 이동
 
 3. /list.next로 리다이렉션
 
 4. url pattern이 *.next이므로 FrontController로 이동해서 init()메서드 실행하고 rm 객체 생성
 
 5. service()메서드 실행돼서 ListController 불러오고 ListController.execute()메서드 실행
 
 6. viewName이 list.jsp가 되므로 해당 view로 forward