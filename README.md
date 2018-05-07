# ReactJS_Album_Back_End

## Issues
- `Spring Data JPA`에서 새로 바뀐 버전을 적용할 수 있습니다.
- `Spring Web MVC`에서 Asynchronous 방식에 대하여 CRUD 작업을 진행할 수 있습니다.
- `ReactJS`에서 `Dropzone`을 이용하여 BLOB 파일을 받아서 파일 업로드를 진행할 수 있습니다.
- REST API에서 이미지 데이터에 대해 동기적으로 보여질 수 있는 방법에 대해서 진행할 수 있습니다.
- `Spring Web Asynchronous`에 대해 자세하게 공부를 하면서 이를 적용할 수 있도록 합니다.

## Relational Database

RDBMS는 `MySQL`를 이용하였습니다. 각 객체 내부의 멤버 변수의 역할은 아래의 Domain 클래스를 참고하셔도 좋습니다.

![react_album_er](/src/docs/react_album_er.png "react_album_er")

> - [Album 클래스 참조](https://github.com/tails5555/ReactJS_Album_Back_End/blob/master/src/main/java/net/kang/domain/Album.java)
> - [Photo 클래스 참조](https://github.com/tails5555/ReactJS_Album_Back_End/blob/master/src/main/java/net/kang/domain/Photo.java)

## Maven pom.xml
`pom.xml` 를 기반으로 Maven Dependency를 구성하여 Update Maven은 필수입니다

```
<dependencies>
	<!-- 1. Spring Data JPA -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-jpa</artifactId>
	</dependency>
	<!-- 2. Spring Web MVC Starter -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<!-- 3. MySQL JDBC Connector -->
	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<scope>runtime</scope>
	</dependency>
	<!-- 4. Lombok Project -->
	<!-- Lombok은 각 인스턴스들에 대해서 getter, setter, toString, equals, hashCode 등의 구현을 자동으로 해 주는 프로젝트이다. -->
	<dependency>
		<groupId>org.projectlombok</groupId>
		<artifactId>lombok</artifactId>
		<optional>true</optional>
	</dependency>
	<!-- 5. Tomcat Starter -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-tomcat</artifactId>
		<scope>provided</scope>
	</dependency>
	<!-- 6. Spring Test Starter -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>
```
