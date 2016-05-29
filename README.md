# Создание проекта

Проект можно создать с помощью (Spring initializer)[https://start.spring.io/] в IDEA.

Для создания проекта вручную можно воспользоваться командой:

```
mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DgroupId=it.sevenbits\
  -DartifactId=springboot-tutorial
```

Далее надо проверить pom.xml и добавить нужные зависимости. Тогда минимально рабочий pom.xml должен выглядеть следующим образом:

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>it.sevenbits</groupId>
    <artifactId>spring-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.3.5.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

Проверить дерево зависимостей можно командой `mvn dependency:tree`.

# Hello World

Первым делом надо создать main класс для загрузки сервера. Создадим его по адресу `src/main/java/it/sevenbits/web/App.java` (наличие полного пакета строго обязательно).

```
@SpringBootApplication // специальная аннотация, помечающая данный класс как веб-сервер, конфигурационный, с автоматическим поиском других конфигов.
public class App extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        // можно выставить дополнительные конфигурации запуска здесь
        app.run(args);
    }
}
```

Далее создаем первый контроллер по адресу `src/main/java/it/sevenbits/web/controllers/HomeController.java`.

```
@Controller // stereotype аннотация, означающая что данный класс может обрабатывать входящие запросы.
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET) // описывает какие запросы, по какому адресу, какого формата данный обработчик может принимать
    @ResponseBody // возвращаемое функцией значение отдается клиенту как есть.
    public String getIndex() {
        return "Hello World";
    }
}
```

# Первый запуск

В IDEA можно просто нажать на запуск main-класса, это так же удобо для отладки(debug) приложения, или с помощью maven: `mvn spring-boot:run`. Приложение запустится по-умолчанию на `8080` порту. Примечательно то, что мы уже можем принимать и отвечать на запросы, хотя не было написано ни одного конфигурационного файла. В этом и состоит прелесть spring-boot.

Заходим на страницу [localhost:8080](http://localhost:8080). Или так: `curl :8080/`.

Для сборки приложения в jar файл и последующего развертывания необходимо подключить специальный плагин в `pom.xml`.

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

После этого команда `mvn package` соберет запускаемый jar файл: `java -jar target/spring-demo-1.0-SNAPSHOT.jar`. 

# Конфигурация

Конфигурационные файлы приложения могут находиться в каталоге `src/main/resources`.

Создадим файл `src/main/resources/application.yml`:

```
server:
  port: 9000
```

Кроме `yml` spring-boot поддерживает java `properties`.

Подробнее [тут](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html).

# Шаблонизаторы

Попробуем отдать первую html страницу. Можно, конечно, отдать ее статически, но обычно это делают использую шаблонизаторы. Они позволяют разбивать html на переиспользуемые блоки, передавать в html java-объекты и, даже, вызывать java-функции.

Предлагается использовать [jade4j](https://github.com/neuland/jade4j). Для этого подкючим зависимость в pom.xml

```
<dependency>
    <groupId>com.domingosuarez.boot</groupId>
    <artifactId>spring-boot-starter-jade4j</artifactId>
    <version>0.3.0</version>
</dependency>
```

И дополним `application.yml`:

```
spring:
  jade4j:
    caching: false
    prettyPrint: true
```

Создадим перый шаблон по адресу `src/main/resources/templates/layout.jade`. 

```
!!! 5
html
  head
    meta(charset='UTF-8')
    block head
      title Spring Boot Demo

      meta(name="viewport", content="width=device-width, initial-scale=1")
  body
    block content
```

Как видно, данный шаблон представляет собой обертку, общую для любой страницы. В некотором смысле этот шаблон представляет собой абстрактный класс, который будут доопределять другие шаблоны. Их тела будут вставляться в месте `block content`.

Тогда создадим `src/main/resources/templates/home/index.jade`

```
extends ../layout

block append content
  
  h1 Hello Spring Boot
  
  p This is Home/index page
```

Для того чтобы spring-boot стал отдавать jade-шаблоны не нужно делать ничего. Единственное, что надо сделать - это поменять `HomeController`.

```
@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getIndex() {
        return "home/index";
    }
}
```

Обратите внимание, по умолчанию `@Controller` в своих обработчиках возвращает путь до шаблона.

