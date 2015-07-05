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

## Шаг 5. Сохранение данных

Cоздадим пакет `core/domain` и в нем класс `Subscription`. По сути это форма, только с полем id. В данном примере это выглядит странно, что форма полностью совпала с моделью доменного уровня, но это потому что пример очень простой. Очень часто случается так, что одна форма содержит в себе очень много сущностей доменного слоя. Например, форма регистрации на мероприятие. В ней будет информация о пользователе, участнике, его профиле школьника или студента и так далее. Поэтому здесь приводится в качестве примера такая усложненная форма, чтобы в будущем не было вопросов как организовать структуру проекта.

```java
public class Subscription implements Serializable {
    private Long id;
    private String email;
    private String name;
	// setters and getters
}
```

Создаем пакет `core/repository` с классами `RepositoryException` и интерфейсом `SubscriptionRepository`.

```java
public interface SubscriptionRepository {
    void save(final Subscription subscription) throws RepositoryException;
    List<Subscription> findAll() throws RepositoryException;
}
```

Далее мы добавим другие базовые операции из CRUD. Но пока этого будет достаточно.

Мы создали интерфейс для репозитория, намекая, что можно по-разному хранить данные. В этом примере мы будем сохранять данные в памяти в обычный HashMap. Создаем класс `SubscriptionInMemoryRepository`

```java
@Service
@Qualifier(value = "subscriptionInMemoryRepository") // квалификатор понадобится потом, когда мы добавим еще одну имплементацию репозитория
public class SubscriptionInMemoryRepository implements SubscriptionRepository {
    private final static Logger LOG = Logger.getLogger(SubscriptionInMemoryRepository.class);

    private final Map<Long, Subscription> subscriptions;
    private final AtomicLong keySequence;

    public SubscriptionInMemoryRepository() {
        subscriptions = new HashMap<>();
        keySequence = new AtomicLong(1L);
    }

    @Override
    public void save(final Subscription subscription) throws RepositoryException {
        if (subscription == null) {
            LOG.error("Subscription is null");
            throw new RepositoryException("Subscription is null");
        }
        LOG.info("Start saving: " + subscription.toString());
        subscription.setId(keySequence.getAndIncrement());
        subscriptions.put(subscription.getId(), subscription);
        LOG.info("Saved: " + subscription.toString());
    }

    @Override
    public List<Subscription> findAll() {
        return new ArrayList<>(subscriptions.values());
    }
}
```

Так как не очень хорошо вызывать методы репозитория напрямую из контролеров, то создадим сервис `SubscriptionsService` на уровне `web`. Этот сервис будет сам вызвать необходимые методы репозитория и переоборачивать модели из уровня `core` в модели уровня `web`. Такой подход помогает достаточно просто переводить наборы или любые комбинации объектов между уровнями, так как очень часто на больших проектах невозможно представлять объекты с которыми мы работаем в веб-приложении с теми данными которые хранятся в базе и представляют бизнес логику.

```java
public class SubscriptionModel {
    private final Long id;
    private final String email;
    private final String name;

    public SubscriptionModel(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
	// Getters only
}
```

```java
@Service
public class SubscriptionsService {
    @Autowired
    private SubscriptionRepository repository;

    public void save(final SubscriptionForm form) throws ServiceException {
        final Subscription subscription = new Subscription();
        subscription.setEmail(form.getEmail());
        subscription.setName(form.getName());
        try {
            repository.save(subscription);
        } catch (Exception e) {
            throw new ServiceException("An error occurred while saving subscription: " + e.getMessage(), e);
        }
    }

    public List<SubscriptionModel> findAll() throws ServiceException {
        try {
            List<Subscription> subscriptions = repository.findAll();
            List<SubscriptionModel> models = new ArrayList<>(subscriptions.size());
            for (Subscription s: subscriptions) {
                models.add(new SubscriptionModel(s.getId(), s.getName(), s.getEmail()));
            }

            return models;
        } catch (Exception e) {
            throw new ServiceException("An error occurred while retrieving subscriptions: " + e.getMessage(), e);
        }
    }
}
```

В `HomeController` добавляем вызов методы сервиса и новый action `getSubcriptions`.

```java
@Controller
public class HomeController {
    // validator and logger
    @Autowired
    private SubscriptionsService service;

    // root action

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String subscribe(@ModelAttribute SubscriptionForm form, final Model model) throws ServiceException {
		// form validation
        service.save(form);
        model.addAttribute("subscription", form);
        return "home/subscribed";
    }

    @RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
    @ResponseBody
    public List<SubscriptionModel> getSubscriptions() throws ServiceException {
        return service.findAll();
    }
}	
```

## Шаг 6. Сохранение даных в БД.

В прошлом шаге мы сохраняли данные просто в памяти. Теперь добавим поддержку репозиторий для сохранения данных в БД, например postgresql, mysql, sqlite или [hsql](https://ru.wikipedia.org/wiki/HSQLDB). 

Для работы с БД будем использовать [MyBatis](https://mybatis.github.io/mybatis-3/index.html#). Данные будем хранить с использование Hsql, чтобы не настраивать сервер. Добавим необходимые зависимости в `pom.xml`


```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.2.3</version>
</dependency>

<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>1.2.2</version>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>3.2.1</version>
</dependency>
```

  Добавим новый пакет `core/mappers` в который мы сложим все мапперы для mybatis. Мапер переводит sql объекты из БД в наши java объекты. Создадим мапер для сохранения и получения `Subscription`.

```java
public interface SubscriptionMapper {

    @Select("SELECT id, name, email FROM subscriptions")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "email", property = "email"),
        @Result(column = "name", property = "name")
    })
    List<Subscription> findAll();

    @Insert("INSERT INTO subscriptions (email, name) VALUES (#{email}, #{name})")
    void save(final Subscription subscription);
}
```

Репозиторий будет выглядеть совсем просто - как будто мы вызываем методы мапера и все.

```java
@Repository
@Qualifier(value = "subscriptionPersistRepository")
public class SubscriptionPersistRepository implements SubscriptionRepository {
    private static Logger LOG = Logger.getLogger(SubscriptionPersistRepository.class);

    @Autowired
    private SubscriptionMapper mapper;


    @Override
    public void save(final Subscription subscription) throws RepositoryException {
        if (subscription == null) {
            throw new RepositoryException("Subscription is null");
        }
        try {
            mapper.save(subscription);
        } catch (Exception e) {
            throw new RepositoryException("An error occurred while saving subscription: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Subscription> findAll() throws RepositoryException {
        try {
            return mapper.findAll();
        } catch (Exception e) {
            throw new RepositoryException("An error occurred while retrieving subscriptions: " + e.getMessage(), e);
        }
    }
}
```

Необходимо сказать спрингу, где брать маперы. Так же для mybatis надо явно указать бин SqlSessionFactory, с помощью которого инициализируются маперы. Поэтому создадим кофигурационный файл в пакете `config`. Сразу же добавим настройку для Flyway - утилита для миграций, имеющая java-api который используется spring-ом.

```java
@Configuration
@MapperScan(basePackages = "it.sevenbits.springboottutorial.core.mappers")
public class DatabaseConfig {
    @Autowired
    private DataSource dataSource;

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }
}
```

По умолчанию `flyway` ищет файлы для миграций в каталоге `resources/db/migrations` и требует особый формат имени файла `V{version_number}__{migration name}.sql`. Создаем файл `db/migration/V2015.07.05.22.47__Create_subscriptions_table.sql`

```sql
CREATE TABLE subscriptions (
  id INTEGER NOT NULL IDENTITY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL
);
```

Не забудем в сервисе `SubscriptionsService` добавить квалификатор для нового репозитория.

```java
@Autowired
@Qualifier(value = "subscriptionPersistRepository")
private SubscriptionRepository repository;
```
