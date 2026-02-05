# LockedIn Code Cheat Sheet 📚

A complete reference for every tool, library, and annotation used in the project.

---

# 📖 TOOL & LIBRARY DEFINITIONS

## What is **Lombok**?
Lombok is a Java library that **automatically generates boilerplate code** at compile time. Instead of writing getters, setters, constructors, and toString methods manually, you add annotations and Lombok generates them for you. This reduces code by 70-80% in data classes.

**Website:** https://projectlombok.org

---

## What is **Spring Boot**?
Spring Boot is a Java framework that makes it easy to create **stand-alone, production-ready applications**. It provides auto-configuration, embedded servers (Tomcat), and a convention-over-configuration approach. You don't need to write XML configs - just add annotations!

**Website:** https://spring.io/projects/spring-boot

---

## What is **Swagger UI / OpenAPI**?
Swagger UI is an **interactive API documentation tool**. It automatically generates a web page where you can see all your REST endpoints, their parameters, and even test them directly in the browser. OpenAPI is the specification format (JSON/YAML) that describes your API.

**Access:** http://localhost:8080/swagger-ui/index.html

**Website:** https://swagger.io

---

## What is **JPA (Java Persistence API)**?
JPA is a Java specification for **Object-Relational Mapping (ORM)**. It lets you work with database tables as Java objects. Instead of writing SQL queries, you work with entities and JPA translates operations to SQL. Hibernate is the most popular JPA implementation.

---

## What is **Hibernate**?
Hibernate is the **implementation of JPA**. It handles all the database operations behind the scenes - creating tables from entities, generating SQL queries, managing transactions, and handling relationships between tables.

---

## What is **H2 Database**?
H2 is a lightweight, **in-memory SQL database** written in Java. It's perfect for development and testing because it starts instantly and doesn't require installation. Data is stored in memory (lost on restart) or can be persisted to a file.

**Console:** http://localhost:8080/h2-console

---

## What is **JUnit 5**?
JUnit 5 is the standard **testing framework for Java**. It provides annotations like `@Test` to mark test methods, and assertion methods like `assertEquals()` to verify results. It's the foundation for all unit testing in Java applications.

**Website:** https://junit.org/junit5

---

## What is **Mockito**?
Mockito is a **mocking framework for unit tests**. It creates fake objects (mocks) that simulate real dependencies. This lets you test a class in isolation - for example, testing a Service without actually connecting to the database. You define what the mock should return using `when().thenReturn()`.

**Website:** https://site.mockito.org

---

## What is **MockMvc**?
MockMvc is a Spring testing utility for testing **REST controllers without starting a server**. It simulates HTTP requests (GET, POST, etc.) and lets you verify the response status, headers, and JSON body. Much faster than integration tests!

---

## What is **JaCoCo**?
JaCoCo (Java Code Coverage) is a tool that measures **code coverage** - what percentage of your code is executed by tests. It generates HTML reports showing which lines are tested (green) and which are not (red). Aim for 70-80% coverage.

**Report:** `target/site/jacoco/index.html`

---

## What is **Maven**?
Maven is a **build automation tool** and dependency manager for Java. It downloads libraries from repositories, compiles code, runs tests, and packages your app into a JAR file. Configuration is in `pom.xml`.

**Commands:** `mvn clean`, `mvn test`, `mvn spring-boot:run`

---

## What is **Vite**?
Vite is a **modern frontend build tool** for React/Vue/etc. It's extremely fast because it uses native ES modules. It provides hot module replacement (HMR) so changes appear instantly in the browser without full page refresh.

**Website:** https://vitejs.dev

---

## What is **Framer Motion**?
Framer Motion is a **React animation library**. It makes adding animations easy with the `<motion.div>` component. You can animate opacity, position, scale, and more with simple props like `initial`, `animate`, and `transition`.

**Website:** https://www.framer.com/motion

---

## What is **Groq**?
Groq is an **AI inference cloud platform** that runs large language models (like LLaMA) extremely fast. In this project, it powers the AI chatbot using the LLaMA 3.3 70B model. The API is OpenAI-compatible.

**Website:** https://groq.com

---

# 🏷️ 1. LOMBOK ANNOTATIONS

| Annotation | What It Does |
|------------|--------------|
| `@Data` | Generates getters, setters, `toString()`, `equals()`, `hashCode()` |
| `@Getter` | Generates getter methods for all fields |
| `@Setter` | Generates setter methods for all fields |
| `@NoArgsConstructor` | Generates empty constructor: `public ClassName() {}` |
| `@AllArgsConstructor` | Generates constructor with all fields |
| `@Builder` | Generates builder: `.builder().field(value).build()` |
| `@RequiredArgsConstructor` | Generates constructor for `final` fields |
| `@Slf4j` | Creates logger: `log.info()`, `log.error()` |

### Example:
```java
@Service
@RequiredArgsConstructor  // Injects dependencies
@Slf4j                    // Creates log variable
public class OrderService {
    private final OrderRepository orderRepository;
    
    public void process() {
        log.info("Processing order");
    }
}
```

---

# 🌱 2. SPRING ANNOTATIONS

| Annotation | Layer | Purpose |
|------------|-------|---------|
| `@SpringBootApplication` | Main | Entry point + auto-configuration |
| `@RestController` | Controller | REST API endpoints |
| `@Service` | Service | Business logic |
| `@Repository` | Repository | Data access |
| `@Configuration` | Config | Bean definitions |
| `@Bean` | Config | Creates managed object |
| `@Autowired` | Any | Dependency injection |

### HTTP Mappings:
| Annotation | HTTP Method |
|------------|-------------|
| `@GetMapping` | GET |
| `@PostMapping` | POST |
| `@PutMapping` | PUT |
| `@DeleteMapping` | DELETE |
| `@RequestMapping` | Base path |

### Request Parameters:
| Annotation | Extracts From |
|------------|---------------|
| `@PathVariable` | URL path: `/orders/{id}` → `id` |
| `@RequestBody` | JSON body → Object |
| `@RequestParam` | Query: `?name=value` → `name` |

---

# 🗄️ 3. JPA / HIBERNATE ANNOTATIONS

| Annotation | Purpose |
|------------|---------|
| `@Entity` | Maps class to database table |
| `@Table(name="...")` | Custom table name |
| `@Id` | Primary key |
| `@GeneratedValue` | Auto-increment |
| `@Column` | Column constraints |
| `@ManyToOne` | N:1 relationship |
| `@OneToMany` | 1:N relationship |
| `@JoinColumn` | Foreign key |
| `@PrePersist` | Before INSERT hook |
| `@PreUpdate` | Before UPDATE hook |
| `@Transactional` | Transaction boundary |

### JpaRepository Methods:
| Method | SQL Equivalent |
|--------|----------------|
| `findById(id)` | `SELECT * WHERE id = ?` |
| `findAll()` | `SELECT *` |
| `save(entity)` | `INSERT` or `UPDATE` |
| `deleteById(id)` | `DELETE WHERE id = ?` |
| `existsById(id)` | `SELECT COUNT(*) > 0` |

---

# ✅ 4. VALIDATION ANNOTATIONS

| Annotation | Validates |
|------------|-----------|
| `@Valid` | Triggers validation |
| `@NotNull` | Not null |
| `@NotBlank` | Not null/empty/whitespace |
| `@NotEmpty` | Not empty |
| `@DecimalMin("0.01")` | Minimum value |
| `@Size(min=1, max=10)` | String/List size |
| `@Pattern(regex)` | Regex match |

---

# 📖 5. SWAGGER ANNOTATIONS

| Annotation | Purpose |
|------------|---------|
| `@Tag` | Group endpoints |
| `@Operation` | Describe endpoint |
| `@ApiResponse` | Document responses |
| `@Schema` | Describe DTO field |

---

# 🧪 6. TESTING ANNOTATIONS

### JUnit 5:
| Annotation | When It Runs |
|------------|--------------|
| `@Test` | Marks a test method |
| `@BeforeEach` | Before each test |
| `@AfterEach` | After each test |
| `@BeforeAll` | Once before all tests |
| `@Disabled` | Skip this test |

### Mockito:
| Annotation | Purpose |
|------------|---------|
| `@ExtendWith(MockitoExtension.class)` | Enable Mockito |
| `@Mock` | Create fake object |
| `@InjectMocks` | Inject mocks into test subject |
| `@MockBean` | Mock in Spring context |

### Key Methods:
```java
// Mockito
when(mock.method()).thenReturn(value);
verify(mock).method();
any(), anyLong(), anyString()

// JUnit Assertions
assertEquals(expected, actual);
assertNotNull(value);
assertTrue(condition);
assertThrows(Exception.class, () -> code);
```

---

# ⚛️ 7. REACT HOOKS

| Hook | Purpose |
|------|---------|
| `useState` | Component state |
| `useEffect` | Side effects, API calls |
| `useParams` | URL parameters |
| `useNavigate` | Navigate programmatically |
| `useContext` | Global state |

---

# 📊 8. EXTERNAL APIS

| API | URL | Used For |
|-----|-----|----------|
| Yahoo Finance | `yahoofinance-api` Java library | Stock prices |
| Finnhub | `https://finnhub.io/api/v1` | Market data, news |
| MF API | `https://api.mfapi.in` | Mutual fund NAV |
| Groq | `https://api.groq.com` | AI chatbot |

---

# 🎯 QUICK REFERENCE

```
┌─────────────┬─────────────────────────────────────────┐
│ LAYER       │ KEY ANNOTATIONS                         │
├─────────────┼─────────────────────────────────────────┤
│ Controller  │ @RestController, @GetMapping, @Valid    │
│ Service     │ @Service, @Transactional, @Slf4j        │
│ Repository  │ @Repository, extends JpaRepository      │
│ Entity      │ @Entity, @Id, @ManyToOne, @PrePersist   │
│ DTO         │ @Data, @Builder, @NotNull, @NotBlank    │
│ Config      │ @Configuration, @Bean                   │
│ Test        │ @Test, @Mock, @InjectMocks, @WebMvcTest │
└─────────────┴─────────────────────────────────────────┘
```
