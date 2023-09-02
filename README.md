# wanted-pre-onboarding-backend
<br></br>
##  과제 제출 필수 사항
    - 지원자의 성명
    - 애플리케이션의 실행 방법 (엔드포인트 호출 방법 포함)
    - 데이터베이스 테이블 구조
    - 구현한 API의 동작을 촬영한 데모 영상 링크
    - 구현 방법 및 이유에 대한 간략한 설명
    - API 명세(request/response 포함)
<br></br>
## 지원자의 성명: 김정훈
#### 애플리케이션의 실행 방법 (엔드포인트 호출 방법 포함)
    git clone https://github.com/hufs0529/wanted-pre-onboarding-backend
    sudo docker compose up -d
    엔드포인트: http://13.125.148.149:8080
  <img width="1225" alt="docker compose" src="https://github.com/hufs0529/wanted-pre-onboarding-backend/assets/81501114/a0f6bfdd-39ca-4488-857b-0bc85ac2d623">
  <img width="497" alt="aws" src="https://github.com/hufs0529/wanted-pre-onboarding-backend/assets/81501114/6feea40f-ee79-4e52-bebb-c1d3290b14bb">


#### 데이터베이스 테이블 구조
  <img width="592" alt="erd" src="https://github.com/hufs0529/wanted-pre-onboarding-backend/assets/81501114/0160262b-3132-4a6e-be9a-8cc58c1366a5">

#### 구현한 API의 동작을 촬영한 데모 영상 링크
  https://www.youtube.com/watch?v=PkVYtBitNv4

#### 구현 방법 및 이유에 대한 간략한 설명
- **과제 1. 사용자 회원가입 엔드포인트**
    - entity: List<Post>변수를 추가해서 Member가 업로드한 게시글 리스트 생성
    - MemberRequestDto: 회원가입시 email, password not null 설정 및 이메일양식 + 비밀번호 최소 자리수 설정
    - MemberWriterInfo: post의 게시물 검색시 작성자의 비밀번호가 노출되지 않도록 email만 반환
    - controller: POST로 입력받으며 @Valid를 통해 유효성 검사 및 유효성 검사 실패시 catch 구문으로 다수의 오류 표출 ,@RequestBody로 이메일과 비밀번호 입력
    - service: 유효성, 중복 예외를 포함하고 있으며 중복된 메일 가입시 중복 예외처리

- **과제 2. 사용자 로그인 엔드포인트**
    - MemberRequestDto: 회원가입과 마찬가지로 유효성 체크
    - TokenDto: String message, String token으로 이루어져 있으며 로그인 성공시 jwt token을 token에, 로그인 실패시 실패 메시지를 message로 반환
    - controller: POST로 입력받으며 MemberRequestDto양식으로 입력 안될시에 예외처리, 로그인 성공시 TokenDto가 반환되며 반환된 token을 게시물 업로드, 업데이트, 삭제시 bearer token에 입력
    - service: 로그인시 SecurityContextHolder에 인증정보 입력
      
- **과제 3. 새로운 게시글을 생성하는 엔드포인트**
    - entity: confirmWriter함수를 포함하여서 게시글 생성시 글쓴이 저장
    - controller: POST로 입력받으며 title(제목), content(내용)정보를 입력받으며 유효하지 않을 경우 MethodArgumentNotValidException반환
    - service: checkWriter 함수가 존재하며 SecurityContextHolder에 저장된 인증정보와 header에 입력되는 token정보를 비교해서 일치할시 로그인한 유저의 email반환. 
      
- **과제 4. 게시글 목록을 조회하는 엔드포인트**
    - PostInfoDto: 게시물정보(title, content), WriterInfoDto(글쓴이 이메일)을 노출시켜준다
    - controller: RequestParam으로 정렬 기준, 노출될 페이지 수, 한페이지에 노출될 게시물 수를 설정할수 있으며 default로 생성날짜 기준 정렬, 1페이지 노출, 5게시물 노출로 설정되어 있다. 또다른 매개변수인 PostSearchCondition(title, content) 입력으로 custom된 repository로 검색 요청을 한다. 또한 Pageable 변수 요청으로 CustomPostRepositoryImpl에서 페이지 관련 설정을 할수 있다
    - CustomPostRepositoryImpl: controller로부터 받은 정보를 토대로 typedQuery를 이용해서 PostInfoDto(게시물정보+글쓴이 이메일)과 전체 페이지 수, 현재 페이지, 전체 게시물 수, 현재 페이지에 나타난 게시물 수를 반환해준다 
- **과제 5. 특정 게시글을 조회하는 엔드포인트**
    - controller: @PathVariable을 통해 url뒤에 id를 입력하는 것으로 요청할 수 있다
    - service: 게시물이 존재하지 않을시 PostNotFoundException예외처리를 한다
- **과제 6. 특정 게시글을 수정하는 엔드포인트**
    - controller: 역시 @PathVariable을 사용한다
    - service: checkWriter을 통해 로그인된 사용자가 맞을시 수정할 수 있으며 로그인된 사용자가 없을시 UserLoginException예외처리를 반환한다
- **과제 7. 특정 게시글을 삭제하는 엔드포인트**
    - controller: 역시 @PathVariable을 사용한다
    - service: checkWriter을 통해 로그인된 사용자가 맞을시 삭제할 수 있으며 로그인된 사용자가 없을시 UserLoginException예외처리를 반환한다
 
#### API 명세(request/response 포함)
    [Member 회원가입] POST /member/signUp
    1. 회원가입 정상 실행 -> http://13.125.148.149:8080/member/signUp
    @Request Body
    {
        "email":"example@example.com",
        "password":"abcd1234!"
    }

    # Response
    {
    "email": "example@example.com",
    "password": "abcd1234!"
    }

    2. 회원가입 이메일 미입력 -> http://13.125.148.149:8080/member/signUp
    @Request Body
    {
        "password":"abcd1234!"
    }

    # Response
     "defaultMessage": "이메일을 입력해주세요"
    
    3. 회원가입 패스워드 미입력 -> http://13.125.148.149:8080/member/signUp
    @Request Body
    {
        "email":"example@example.com",
    }

    # Response
    "defaultMessage": "비밀번호를 입력해주세요"
    
    4. 회원가입 이메일 유효성 검사 실패 -> http://13.125.148.149:8080/member/signUp
    @Request Body
    {
        "email":"exampleexample.com",
        "password":"abcd1234!"
    }

    # Response
    "defaultMessage": "유효한 이메일 주소를 입력해주세요"
    
    5. 회원가입 패스워드 유효성 검사 실패 -> http://13.125.148.149:8080/member/signUp
    @Request Body
    {
        "email":"example@example.com",
        "password":"abc"
    }

    # Response
     "defaultMessage": "비밀번호는 최소 8자 이상이어야 합니다."
    
    6.이미 존재하는 이메일 -> http://13.125.148.149:8080/member/signUp
    @Request Body
    {
        "email":"example@example.com",
        "password":"abcd1234!"
    }

    # Response
    {
    "error": "Email already exists"
    }
<br></br>
    
    [Member 로그인]
    1. 로그인 후 token 발급 POST /member/login -> http://13.125.148.149:8080/member/login
    @Request Body
    {
        "email":"example@example.com",
        "password":"abcd1234!"
    }

    # Response
    {
      "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJleGFtcGxlQGV4YW1wbGUuY29tIiwiYXV0aCI6IiIsImV4cCI6MTY5MTczMzM1N30.Z4UtNJPLY1qjMQ31wnWyVDiPHF34l_-rYese-    9WbS4Moh4d6rO4F_MATW0r12t2Yz8HNoYsIfuTMbmxSNVCSzQ",
      "message": ""
    }
      
    2. 로그인 이메일 미입력 -> http://13.125.148.149:8080/member/login
    @Request Body
    {
        "password":"abcd1234!"
    }

    # Response
     "defaultMessage": "이메일을 입력해주세요"
    
    3. 로그인 패스워드 미입력 -> http://13.125.148.149:8080/member/login
    @Request Body
    {
        "email":"example@example.com",
    }

    # Response
    "defaultMessage": "비밀번호를 입력해주세요"
    
    4. 로그인 이메일 유효성 검사 실패 -> http://13.125.148.149:8080/member/login
    @Request Body
    {
        "email":"exampleexample.com",
        "password":"abcd1234!"
    }

    # Response
    "defaultMessage": "유효한 이메일 주소를 입력해주세요"
    
    5. 로그인 패스워드 유효성 검사 실패 -> http://13.125.148.149:8080/member/login
    @Request Body
    {
        "email":"example@example.com",
        "password":"a"
    }

    # Response
    "defaultMessage": "비밀번호는 최소 8자 이상이어야 합니다."
    
<br></br>
    
    [Post 생성] POST /post/save
    1. 게시물 생성 -> http://13.125.148.149:8080/post/save
    (Auth -> Bearer Token)
    @Request Body
    {
        "title":"hi everyone",
        "content":"nice to meet u"
    }
    {
        "title":"hello everyone",
        "content":"great to meet u"
    }
    {
        "title":"good everybody",
        "content":"good to meet u guys"
    }
    {
        "title":"hi everyone",
        "content":"nice to meet u girs"
    }
    {
        "title":"bye everyone",
        "content":"byebye"
    }
    {
        "title":"goodbye everyone",
        "content":"phhh byebye"
    }
    {
        "title":"bulk bulk",
        "content":"bulk"
    }

    # Response
    Post created with ID: 1
    
    2. token미입력 POST /post/save -> http://13.125.148.149:8080/post/save
    @RequestBody
    {
        "title":"hi everyone",
        "content":"nice to meet u"
    }

    # Response
    {
    "timestamp": "2023-08-11T05:28:38.244+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/post/save"
    }
    
    [Post 목록을 조회] GET /post/search
    1. 제목, 내용으로 조회 -> http://13.125.148.149:8080/post/search?title=hi&content=nice
    @RequestParam
    title: hi
    content: meet u
    
    2. 제목으로 조회 -> http://13.125.148.149:8080/post/search?title=hi
    @RequestParam
    title:goodbye
    
    3. 내용으로 조회 -> http://13.125.148.149:8080/post/search?content=nice
    @RequestParam
    content: bulk

    # Response
    {
    "postList": [
        {
            "postId": 7,
            "title": "bulk bulk",
            "content": "bulk",
            "writerInfoDto": {
                "email": "example@example.com"
            }
        }
    ],
    "totalPageCount": 1,
    "curPageNum": 0,
    "totalPostCount": 1,
    "currentPagePostCount": 1
    }

    
<br></br>
    
    [Post 특정 게시물 조회]  GET post/get{postId}
    1. 게시물 id로 조회 -> http://13.125.148.149:8080/post/get/1
    @PathVariable

    # Response
    {
    "postId": 1,
    "title": "hi everyone",
    "content": "nice to meet u",
    "writerInfoDto": {
        "email": "example@example.com"
    }
    }
    
    2. 존재하지 않는 id 조회 -> http://13.125.148.149:8080/post/get/100

    # Response
    {
    "timestamp": "2023-08-11T05:30:25.990+00:00",
    "status": 404,
    "error": "Not Found",
    "path": "/post/get/100"
    }
     
    [Post 게시물 업데이트] POST /post/update{postId}
    1. 게시물 업데이트 -> http://13.125.148.149:8080/post/update/1
    @PathVariable
    @RequestBody
    (Auth -> Bearer Token)
    {
        "title":"hi everyone",
        "content":"nice to meet u"
    }

    # Response
    {
    "title": "hi everyone",
    "content": "nice to meet u"
    }
    
    2. 존재하지 않는 id 조회 -> http://13.125.148.149:8080/post/update/100
    {
        "title":"hi everyone",
        "content":"nice to meet u"
    }

    # Response
    {
    "timestamp": "2023-08-11T05:30:43.951+00:00",
    "status": 404,
    "error": "Not Found",
    "path": "/post/update/100"
    }

    
    3. token 미입력 -> http://13.125.148.149:8080/post/update/1
    {
        "title":"hi everyone",
        "content":"nice to meet u"
    }

    # Response
    {
    "timestamp": "2023-08-11T05:31:55.082+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/post/update/1"
    }

      
  <br></br>
    
    [Post 게시물 삭제] DELETE /post/delete{postId}
    @PathVariable
    (Auth -> Bearer Token)
    1. 게시물 삭제 -> http://13.125.148.149:8080/post/delete/1

    # Response
    1 id의 게시물이 지워졌습니다
    
    2. 존재하지 않는 id 조회 -> http://13.125.148.149:8080/post/delete/100

    # Response
    {
    "timestamp": "2023-08-11T05:32:21.935+00:00",
    "status": 404,
    "error": "Not Found",
    "path": "/post/delete/100"
    }
    
    3. token 미입력 -> http://13.125.148.149:8080/post/update/2

    # Response
    {
    "timestamp": "2023-08-11T05:33:35.420+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/post/delete/3"
    }
# kafka_hdfs
