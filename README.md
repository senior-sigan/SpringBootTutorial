# Spring Boot Tutorial

В этом туториале вы научитесь как создать простое веб-приложение с использованием [Spring Boot](http://projects.spring.io/spring-boot/).

## Шаг 1. Hello spring boot

Создаем пустой мавен проект с помощью команды:
```sh
mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DgroupId=it.sevenbits\
  -DartifactId=springboot-tutorial
```

Добавляем в `pom.xml`  зависимости для spring-boot и плагин для сборки fat-jar.

Запускаем `mvn dependency:tree`, чтобы скачать все новые зависимости и вывести их в виде дерева.

Создаем главный конфигурационный класс `Application.java` и класс контроллера `Home.java`

Для запуска проекта выполняем ```mvn spring-boot:run```

Открываем [localhost:8000](http://localhost:8080/) 
