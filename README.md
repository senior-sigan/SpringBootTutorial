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
