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

## Шаг 2. Рендеринг шаблонов

Добавляем в `pom.xml` библиотеку [thymeleaf](http://www.thymeleaf.org/). К сведению эта библиотека не основана на jsp. Это шаблонизатор для HTML5 и XML.  [Чем отличается jsp от thymeleaf](https://spring.io/blog/2012/10/30/spring-mvc-from-jsp-and-tiles-to-thymeleaf)

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

Так как эта библиотека уже интегрирована в `spring-boot` нам не нужно создавать `bean` с конфигурацией для нее. 

По-умолчанию все шаблоны должны находиться в `src/main/resources/templates`. Поэтому создаем шаблон `index.html` по адресу `src/main/resources/templates/home`. А в контроллере `HomeController#index` убираем аннотацию `@ResponseBody` и возвращаем строку `"home/index"`. 

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8" />
    <title>SpringBootTutorial</title>
</head>
<body>
    <h1>Hello Spring Boot</h1>
</body>
</html>
```

```java
@Controller
public class HomeController {
    @RequestMapping(value = "/")
    public String index() {
        // Так как нет аннотации @ResponseBody, то spring будет искать шаблон по адресу home/index
        // Если шаблона не будет найдено, то вернется 404 ошибка
        return "home/index";
    }
}
```

Обратите внимание, что `thymeleaf` проверяет html на валидность, поэтому иногда можете получать 500 ошибку, если неправильно написали html: забыли закрыть тэг, например.

## Шаг 3. Добавим формочку

Добавим форму подписки на главную страницу. Для этого в методе `index` `HomeController` добавим аргумент  `Model` - это такой контейнер для объектов, которые нужно показать в шаблоне. Так же создадим form-object - просто джава класс, который представляет собой контейнер формы. 
 
```
 @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model) {
        // В модель добавим новый объект формы подписки
        model.addAttribute("subscription", new SubscriptionForm());
        return "home/index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String subscribe(@ModelAttribute SubscriptionForm form, final Model model) {
        // В запросе пришла заполненная форма. Отправим в модель этот объект и отрендерим ее на другом шаблоне.
        model.addAttribute("subscription", form);
        return "home/subscribed";
    }
```
 
В шаблоне добавим код для формы.

```html
<form action="#" th:action="@{/}" th:object="${subscription}" method="post">
            <input th:field="*{name}" name="name" placeholder="Your name" type="text" />
            <input th:field="*{email}" name="email" placeholder="Your email" type="text"/>
            <input type="submit" value="Subscribe" />
</form>
```

Добавим шаблон для вывода содержимого формы
```html
<h3>You successfully subscribed</h3>
<p th:text="'email: ' + ${subscriptionForm.email}" />
<p th:text="'name: ' + ${subscriptionForm.name}" />
<a href="/">Go to main page</a>
```

## Шаг 4. Валидация формы

Создадим сервис `SubscriptionFormValidator`

```java
@Service
public class SubscriptionFormValidator {
    @Autowired
    private CommonFieldValidator validator;

    private static final Logger LOG = Logger.getLogger(SubscriptionFormValidator.class);

    public HashMap<String, String> validate(final SubscriptionForm form) {
        LOG.info("SubscriptionFormValidator started for: " + form.toString());
        HashMap<String, String> errors = new HashMap<>();

        validator.isNotNullOrEmpty(form.getEmail(), errors, "email", "Поле не должно быть пустым");
        validator.isNotNullOrEmpty(form.getName(), errors, "name", "Поле не должно быть пустым");

        validator.isEmail(form.getEmail(), errors, "email", "Введите правильный email");

        validator.shorterThan(form.getEmail(), 255, errors, "email", "Поле должно быть кроче чем 255 символов");
        validator.shorterThan(form.getName(), 255, errors, "name", "Поле должно быть кроче чем 255 символов");

        for (Map.Entry<String, String> entry : errors.entrySet()) {
            LOG.info(String.format("Error found: Filed=%s, Error=%s",
                entry.getKey(), entry.getValue()));
        }

        return errors;
    }
}
```

А так же создадим вспомогательный класс `CommonFieldValidator`

```java
@Service
public class CommonFieldValidator {

    /** Email exists pattern */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE
    );
    /** Pattern for whitespaces */
    private static final String WHITESPACE_PATTERN = "\\s+";

    // Validate whether value is not null and empty or contains only spaces, otherwise reject it
    public void isNotNullOrEmpty(
        final String value,
        final Map<String, String> errors,
        final String field,
        final String key
    ) {
        if (!errors.containsKey(field)) {
            if (value == null) {
                errors.put(field, key);
            } else if (value.isEmpty()) {
                errors.put(field, key);
            } else if (value.matches(WHITESPACE_PATTERN)) {
                errors.put(field, key);
            }
        }
    }

    // Validate whether value is valid email, otherwise reject it
    public void isEmail(final String value, final Map<String, String> errors, final String field, final String key) {
        if (value != null && !errors.containsKey(field)) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(value);
            if (!matcher.find()) {
                errors.put(field, key);
            }
        }
    }

    // Validate, whether value is too long
    public void shorterThan(
        final String value,
        final Integer maxLength,
        final Map<String, String> errors,
        final String field,
        final String key
    ) {
        if (value != null && !errors.containsKey(field)) {
            if (value.length() > maxLength) {
                errors.put(field, key);
            }
        }
    }
}
```

Заметим, что создает экземпляры этих сервисов сам spring-boot так как мы указали над классами аннотацию `@Service`. А чтобы подключить зависимость - аннотацию `@Autowired`. Если бы мы не поставили аннотацию `@SpringBootApplication` над главным классом конфигурации, то нам бы пришлось вручную создавать бины и подключать зависимости через setter-методы. Но об этом чуть позже.

Далее в изменим контроллер следующим образом

```java
@Controller
public class HomeController {
    private static Logger LOG = Logger.getLogger(HomeController.class);

    @Autowired
    private SubscriptionFormValidator validator;

    // GET home/index

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String subscribe(@ModelAttribute SubscriptionForm form, final Model model) {
        final Map<String, String> errors = validator.validate(form);
        if (errors.size() != 0) {
            // Если есть ошибки в форме, то снова рендерим главную страницу
            model.addAttribute("subscription", form);
            model.addAttribute("errors", errors);
            LOG.info("Subscription form contains errors.");
            return "home/index";
        }
        // В запросе пришла заполненная форма. Отправим в модель этот объект и отрендерим ее на другом шаблоне.
        model.addAttribute("subscription", form);
        return "home/subscribed";
    }
}
```

И добавим в `index.html` блок для вывода ошибок.

```html
<div class="errors" th:if="${errors != null}">
	The form contains errors
	<ul>
	    <li th:each="error: ${errors}" th:text="${error.key} + ' ' + ${error.value}"></li>
	</ul>
</div>
```
