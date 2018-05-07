# ReactJS_Album_Back_End

## Issues
- Spring Data JPA에서 새로 바뀐 버전을 적용해서 
- Spring Web MVC에서 Asynchronous 방식에 대하여 CRUD 작업을 진행할 수 있습니다.
- ReactJS에서 `Dropzone`을 이용하여 BLOB 파일을 받아서 파일 업로드를 진행할 수 있습니다.
- REST API에서 이미지 데이터에 대해 동기적으로 보여질 수 있는 방법에 대해서 진행할 수 있습니다.
- Spring Web Asynchronous에 대해 자세하게 공부를 하면서 이를 적용할 수 있도록 합니다.

## Relational Database

RDBMS는 `MySQL`를 이용하였습니다. 각 객체 내부의 멤버 변수의 역할은 아래의 Domain 클래스를 참고하셔도 좋습니다.

![react_album_er](/src/docs/react_album_er.png "react_album_er")

> - [Album 클래스 참조](https://github.com/tails5555/ReactJS_Album_Back_End/blob/master/src/main/java/net/kang/domain/Album.java)
> - [Photo 클래스 참조](https://github.com/tails5555/ReactJS_Album_Back_End/blob/master/src/main/java/net/kang/domain/Photo.java)
