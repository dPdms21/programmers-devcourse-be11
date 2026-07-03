# Feign Client로 날씨 공공데이터 가져오기 (기상청 초단기실황)

> **Feign Client**를 사용해 기상청 공공 API를 호출하고 서울의 실황 날씨인 기온, 습도, 강수량 등을 조회한다.
> Feign의 핵심은 HTTP 호출 코드를 직접 작성하지 않고 인터페이스로 선언하는 것이다. 실제 구현은 Spring이 생성한다.
> 이 API는 서비스키 처리 과정에서 오류가 발생하기 쉬우므로 인코딩과 디코딩 차이를 주의해야 한다.
>
> 각 Step의 힌트는 접혀 있다. 먼저 직접 구현하고 필요한 경우 힌트를 펼쳐 확인한다.

<details>
<summary>최종 완성 코드 보기</summary>

> 아래 코드는 Step 5까지 모두 반영한 완성본이다. 파일별로 나누어 작성하면 실행할 수 있다.
> DTO 6개는 각각 별도 파일로 작성하거나 하나의 패키지에 구성한다. Lombok의 `@Getter`, `@Setter`, `@ToString`, `@RequiredArgsConstructor`를 사용하려면 관련 설정이 필요하다.

**`build.gradle`**

```gradle
// Spring Initializr에서 OpenFeign을 추가하면
// Spring Cloud BOM도 함께 설정된다.
implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
implementation 'org.springframework.boot:spring-boot-starter-web'

compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

**`application.yml`**

```yaml
weather:
  api:
    key: 여기에_일반인증키_Decoding_값_입력
```

**`WeatherApplication.java`**

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
```

**`WeatherClient.java`**

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "weatherClient",
        url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0"
)
public interface WeatherClient {

    @GetMapping("/getUltraSrtNcst")
    WeatherResponse getUltraSrtNcst(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("numOfRows") int numOfRows,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("dataType") String dataType,
            @RequestParam("base_date") String baseDate,
            @RequestParam("base_time") String baseTime,
            @RequestParam("nx") int nx,
            @RequestParam("ny") int ny
    );
}
```

**응답 DTO 6종**

```java
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class WeatherResponse {
    private Response response;
}

@Getter
@Setter
@ToString
class Response {
    private Header header;
    private Body body;
}

@Getter
@Setter
@ToString
class Header {
    private String resultCode;
    private String resultMsg;
}

@Getter
@Setter
@ToString
class Body {
    private Items items;
    private int pageNo;
    private int numOfRows;
    private int totalCount;
}

@Getter
@Setter
@ToString
class Items {
    private List<Item> item;
}

@Getter
@Setter
@ToString
class Item {
    private String baseDate;
    private String baseTime;
    private String category;
    private int nx;
    private int ny;
    private String obsrValue;
}
```

> 하나의 파일에 작성한다면 `WeatherResponse`만 `public`으로 선언하고 나머지는 패키지 접근 수준으로 작성한다. 각 클래스를 별도 `.java` 파일로 분리한다면 모두 `public class`로 작성할 수 있다.

**`WeatherService.java`**

```java
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherClient weatherClient;

    @Value("${weather.api.key}")
    private String serviceKey;

    public List<Item> getCurrentWeather(int nx, int ny) {
        LocalDateTime now = LocalDateTime.now();

        if (now.getMinute() < 40) {
            now = now.minusHours(1);
        }

        String baseDate =
                now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String baseTime =
                now.format(DateTimeFormatter.ofPattern("HH")) + "00";

        WeatherResponse response =
                weatherClient.getUltraSrtNcst(
                        serviceKey,
                        10,
                        1,
                        "JSON",
                        baseDate,
                        baseTime,
                        nx,
                        ny
                );

        Header header =
                response.getResponse().getHeader();

        if (!"00".equals(header.getResultCode())) {
            throw new RuntimeException(
                    "기상청 API 오류: "
                            + header.getResultCode()
                            + " "
                            + header.getResultMsg()
            );
        }

        return response.getResponse()
                .getBody()
                .getItems()
                .getItem();
    }

    public List<String> getReadableWeather(int nx, int ny) {
        List<Item> items = getCurrentWeather(nx, ny);
        List<String> result = new ArrayList<>();

        for (Item item : items) {
            String value = item.getObsrValue();

            switch (item.getCategory()) {
                case "T1H" ->
                        result.add("기온: " + value + " ℃");
                case "REH" ->
                        result.add("습도: " + value + " %");
                case "RN1" ->
                        result.add("1시간 강수량: " + value + " mm");
                case "WSD" ->
                        result.add("풍속: " + value + " m/s");
                case "PTY" ->
                        result.add("강수형태: " + ptyText(value));
                default -> {
                }
            }
        }

        return result;
    }

    private String ptyText(String code) {
        return switch (code) {
            case "0" -> "없음";
            case "1" -> "비";
            case "2" -> "비/눈";
            case "3" -> "눈";
            case "5" -> "빗방울";
            case "6" -> "빗방울눈날림";
            case "7" -> "눈날림";
            default -> "알 수 없음(" + code + ")";
        };
    }
}
```

**`WeatherController.java`**

```java
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public List<String> weather() {
        return weatherService.getReadableWeather(60, 127);
    }
}
```

**실행 결과 예시**

```json
[
  "기온: 18.5 ℃",
  "습도: 62 %",
  "1시간 강수량: 0 mm",
  "강수형태: 없음",
  "풍속: 2.3 m/s"
]
```

실제 값은 호출 시각과 날씨에 따라 달라진다. 목록에 실황 데이터가 정상적으로 반환되면 성공이다.

</details>

---

## 0. 먼저 알아둘 점

* 이 과제는 실제 외부 API를 호출하므로 공공데이터포털에서 서비스키를 발급받아야 한다.
* 대상 API는 기상청의 **초단기실황조회**인 `getUltraSrtNcst`다. 현재 시각과 가까운 실제 관측값을 제공한다.
* 요청 주소는 다음과 같다.

```text
http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst
```

* 공공데이터 서비스키는 인코딩 키와 디코딩 키 두 종류로 제공된다. Feign은 쿼리 파라미터를 자동으로 인코딩하므로 **디코딩 키를 사용해야 한다.** 인코딩 키를 사용하면 서비스키가 다시 인코딩되어 인증에 실패할 수 있다.
* 기상청 API의 `nx`, `ny`는 위도와 경도가 아니라 기상청 격자 좌표다. 서울의 격자 좌표는 `nx=60`, `ny=127`이다.
* 초단기실황 데이터는 최근 발표 시각 기준으로 제공된다. 발표 시각을 잘못 요청하면 결과가 비어 있을 수 있다.

---

## 1. 무엇을 만드는가?

Feign Client 인터페이스를 선언하고 서비스에서 호출하여 서울의 실황 날씨를 조회하는 프로그램을 구현한다.

**결과 예시**

```text
기온(T1H): 18.5 ℃
습도(REH): 62 %
강수형태(PTY): 없음
1시간 강수량(RN1): 0 mm
풍속(WSD): 2.3 m/s
```

핵심은 HTTP 요청과 응답 처리 코드를 직접 작성하지 않는 것이다.

```java
weatherClient.getUltraSrtNcst(...);
```

위와 같이 인터페이스의 메서드를 호출하면 Spring이 실제 HTTP 통신을 수행한다.

---

## 2. 학습 목표

| 개념                            | 학습 위치  |
| ----------------------------- | ------ |
| 공공데이터 서비스키 발급과 인코딩 주의사항       | Step 0 |
| `@FeignClient` 인터페이스 선언       | Step 1 |
| 쿼리 파라미터를 `@RequestParam`으로 전달 | Step 1 |
| 중첩 JSON 응답을 DTO로 매핑           | Step 2 |
| 발표 시각 규칙에 맞춘 API 호출과 결과 코드 확인 | Step 3 |
| 실행 및 결과 확인                    | Step 4 |
| 기상 코드값을 사람이 읽을 수 있는 값으로 변환    | Step 5 |

---

## 3. 핵심 개념

### (1) Feign의 역할

기존에는 `RestTemplate` 등을 사용해 URL과 파라미터를 구성하고, HTTP 요청을 전송한 뒤 응답을 직접 변환해야 했다.

Feign은 이러한 과정을 선언형 인터페이스로 작성할 수 있게 한다.

```text
HTTP 요청 정보와 파라미터를 인터페이스로 선언한다.
    ↓
Spring이 해당 인터페이스의 구현체를 런타임에 생성한다.
    ↓
인터페이스 메서드를 호출하면 실제 HTTP 요청이 전송된다.
```

Feign Client는 실제 구현 클래스 없이 인터페이스 선언만으로 동작한다.

### (2) 쿼리 파라미터와 `@RequestParam`

다음 주소에서 `?` 뒤의 값은 쿼리 파라미터다.

```text
?serviceKey=...&nx=60&ny=127
```

Feign Client에서는 컨트롤러와 동일하게 `@RequestParam`을 사용해 쿼리 파라미터를 표현한다.

```java
@RequestParam("nx") int nx
```

컨트롤러에서는 요청 값을 전달받기 위해 사용하고, Feign Client에서는 외부 API로 요청 값을 전송하기 위해 사용한다.

### (3) 디코딩 서비스키 사용

| 구분        | 인코딩 키                       | 디코딩 키                       |
| --------- | --------------------------- | --------------------------- |
| 형태        | `%2B`, `%2F` 등이 포함됨         | `+`, `/`, `=` 등의 원래 문자가 포함됨 |
| Feign에 전달 | 이미 인코딩된 값을 다시 인코딩해 인증 실패 가능 | Feign이 한 번 인코딩하여 정상 전송      |

`application.yml`에는 반드시 디코딩 서비스키를 저장한다.

### (4) 중첩 JSON과 DTO 구조

기상청 API의 응답 구조는 다음과 같다.

```text
response
├── header
└── body
    └── items
        └── item[]
```

Java DTO도 JSON의 중첩 구조에 맞춰 작성한다.

```text
WeatherResponse
└── Response
    ├── Header
    └── Body
        └── Items
            └── List<Item>
```

DTO의 필드 이름은 JSON의 키 이름과 일치해야 한다.

```text
Feign = 인터페이스로 선언하고 구현은 Spring이 생성
서비스키 = 디코딩 키 사용
좌표 = 위도·경도가 아닌 격자 좌표
```

---

## 4. 파일 구조와 준비물

| 파일                       | 역할                           |
| ------------------------ | ---------------------------- |
| `WeatherClient.java`     | 외부 API 호출을 선언하는 Feign Client |
| `WeatherResponse` 외 DTO  | 중첩 JSON 응답을 저장               |
| `WeatherService.java`    | 발표 시각 계산, API 호출, 결과 코드 확인   |
| `WeatherController.java` | `/weather` 요청 처리             |
| `application.yml`        | 서비스키 설정                      |

**의존성**

Spring Initializr에서 OpenFeign을 추가하면 Spring Cloud BOM이 함께 설정된다.

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
```

수동으로 의존성을 추가한다면 Spring Cloud BOM 설정도 필요하다.

---

## 5. Step by Step

### Step 0. 서비스키 발급과 Feign 활성화

**할 일**

1. 공공데이터포털에서 기상청 단기예보 조회서비스 활용 신청을 진행한다.
2. 마이페이지의 오픈 API 인증키에서 일반 인증키의 Decoding 값을 복사한다.
3. `application.yml`에 서비스키를 설정한다.
4. 메인 애플리케이션 클래스에 `@EnableFeignClients`를 추가한다.

<details>
<summary>힌트 보기</summary>

```yaml
weather:
  api:
    key: 여기에_일반인증키_Decoding_값_입력
```

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
```

`@EnableFeignClients`는 `@FeignClient`가 선언된 인터페이스를 찾아 구현체를 생성할 수 있도록 Feign Client 스캔을 활성화한다.

서비스키는 승인 직후 API 서버에 반영되기까지 시간이 걸릴 수 있다. 인증 오류가 발생하면 일정 시간 후 다시 확인한다.

</details>

**확인**: 메인 애플리케이션 클래스에 `@EnableFeignClients`가 선언되어 있고, `application.yml`에 디코딩 서비스키가 설정되어 있어야 한다.

---

### Step 1. Feign Client 인터페이스 만들기 (`WeatherClient.java`)

**목표**: 외부 API 주소와 요청 파라미터를 인터페이스로 선언한다.

**할 일**

1. 인터페이스에 `@FeignClient`를 선언한다.
2. `name`과 API 기본 URL을 설정한다.
3. `@GetMapping("/getUltraSrtNcst")`으로 호출할 API 경로를 설정한다.
4. 필요한 쿼리 파라미터를 `@RequestParam`으로 선언한다.
5. API 명세에 맞춰 `base_date`, `base_time` 이름을 사용한다.

<details>
<summary>힌트 보기</summary>

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "weatherClient",
        url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0"
)
public interface WeatherClient {

    @GetMapping("/getUltraSrtNcst")
    WeatherResponse getUltraSrtNcst(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("numOfRows") int numOfRows,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("dataType") String dataType,
            @RequestParam("base_date") String baseDate,
            @RequestParam("base_time") String baseTime,
            @RequestParam("nx") int nx,
            @RequestParam("ny") int ny
    );
}
```

반환 타입을 `WeatherResponse`로 선언하면 Feign과 Jackson이 JSON 응답을 DTO로 변환한다.

DTO로 변환하려면 요청 파라미터 `dataType`에 `"JSON"`을 전달해야 한다.

</details>

**확인**: 인터페이스에 `@FeignClient`가 선언되어 있고, 요청 메서드에 필요한 8개의 `@RequestParam`이 포함되어 있어야 한다.

---

### Step 2. 응답 DTO 만들기 (`WeatherResponse` 등)

**목표**: API 응답 JSON 구조와 동일한 형태로 DTO를 작성한다.

응답 JSON은 다음과 같은 구조다.

```json
{
  "response": {
    "header": {
      "resultCode": "00",
      "resultMsg": "NORMAL_SERVICE"
    },
    "body": {
      "items": {
        "item": [
          {
            "baseDate": "20241008",
            "baseTime": "1400",
            "category": "T1H",
            "nx": 60,
            "ny": 127,
            "obsrValue": "18.5"
          }
        ]
      },
      "pageNo": 1,
      "numOfRows": 10,
      "totalCount": 8
    }
  }
}
```

**할 일**

`WeatherResponse → Response → Header/Body → Items → Item` 순서로 클래스를 작성한다.

<details>
<summary>힌트 보기</summary>

```java
@Getter
@Setter
@ToString
public class WeatherResponse {
    private Response response;
}

@Getter
@Setter
@ToString
public class Response {
    private Header header;
    private Body body;
}

@Getter
@Setter
@ToString
public class Header {
    private String resultCode;
    private String resultMsg;
}

@Getter
@Setter
@ToString
public class Body {
    private Items items;
    private int pageNo;
    private int numOfRows;
    private int totalCount;
}

@Getter
@Setter
@ToString
public class Items {
    private List<Item> item;
}

@Getter
@Setter
@ToString
public class Item {
    private String baseDate;
    private String baseTime;
    private String category;
    private int nx;
    private int ny;
    private String obsrValue;
}
```

필드 이름은 JSON 키와 동일해야 한다. Jackson은 동일한 이름의 JSON 값을 DTO 필드에 자동으로 매핑한다.

`item`은 배열이므로 `List<Item>`으로 선언한다.

</details>

**확인**: 응답 JSON의 중첩 구조에 맞춰 6개의 DTO가 작성되어 있어야 한다.

---

### Step 3. 발표 시각 계산과 API 호출 (`WeatherService.java`)

**목표**: 현재 시각에 맞는 `base_date`, `base_time`을 계산하고 API를 호출한 뒤 결과 코드가 정상인지 확인한다.

**할 일**

1. `@Value("${weather.api.key}")`로 서비스키를 주입받는다.
2. 현재 분이 40분 미만이면 현재 시각에서 한 시간을 뺀다.
3. `base_date`는 `yyyyMMdd`, `base_time`은 `HH00` 형식으로 만든다.
4. Feign Client를 호출한다.
5. 응답의 `resultCode`가 `"00"`인지 확인한다.
6. 정상 응답이면 `item` 목록을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherClient weatherClient;

    @Value("${weather.api.key}")
    private String serviceKey;

    public List<Item> getCurrentWeather(int nx, int ny) {
        LocalDateTime now = LocalDateTime.now();

        if (now.getMinute() < 40) {
            now = now.minusHours(1);
        }

        String baseDate =
                now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String baseTime =
                now.format(DateTimeFormatter.ofPattern("HH")) + "00";

        WeatherResponse response =
                weatherClient.getUltraSrtNcst(
                        serviceKey,
                        10,
                        1,
                        "JSON",
                        baseDate,
                        baseTime,
                        nx,
                        ny
                );

        Header header =
                response.getResponse().getHeader();

        if (!"00".equals(header.getResultCode())) {
            throw new RuntimeException(
                    "기상청 API 오류: "
                            + header.getResultCode()
                            + " "
                            + header.getResultMsg()
            );
        }

        return response.getResponse()
                .getBody()
                .getItems()
                .getItem();
    }
}
```

초단기실황은 매시각 40분 이후 제공된다. 현재 분이 40분 이전이라면 현재 시간의 자료가 아직 제공되지 않았을 수 있으므로 한 시간 전의 발표 시각을 요청한다.

`minusHours(1)`은 자정을 넘는 경우 날짜도 자동으로 전날로 변경한다.

</details>

**확인**: 서비스가 컴파일되고 Feign Client 호출 결과를 `List<Item>`으로 반환할 수 있어야 한다.

---

### Step 4. 실행하고 결과 확인하기 (`WeatherController.java`)

**목표**: `/weather` 요청으로 서울의 초단기실황 데이터를 조회한다.

**할 일**

1. `@RestController`를 선언한다.
2. `WeatherService`를 생성자 주입으로 전달받는다.
3. `GET /weather` 요청을 처리한다.
4. 서울 격자 좌표인 `60`, `127`을 전달해 서비스를 호출한다.

<details>
<summary>힌트 보기</summary>

```java
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public List<Item> weather() {
        return weatherService.getCurrentWeather(60, 127);
    }
}
```

`@RestController`는 반환된 객체를 JSON 응답으로 변환한다.

브라우저나 API 테스트 도구에서 다음 주소를 호출한다.

```text
http://localhost:8080/weather
```

</details>

**확인**: 응답에 여러 개의 `category`와 `obsrValue`가 포함된 JSON 배열이 반환되어야 한다.

---

### Step 5. 코드값을 사람이 읽을 수 있는 형태로 변환하기 (`WeatherService.java`)

**목표**: 기상청이 제공하는 `T1H`, `REH` 등의 코드값을 한글 의미와 단위가 포함된 문자열로 변환한다.

**할 일**

1. `category`에 따라 기온, 습도, 강수량, 풍속, 강수형태를 구분한다.
2. `PTY` 값도 기상청 코드에 맞춰 한글 의미로 변환한다.
3. 변환 결과를 문자열 목록으로 반환한다.

<details>
<summary>힌트 보기</summary>

```java
import java.util.ArrayList;
import java.util.List;

public List<String> getReadableWeather(int nx, int ny) {
    List<Item> items = getCurrentWeather(nx, ny);
    List<String> result = new ArrayList<>();

    for (Item item : items) {
        String category = item.getCategory();
        String value = item.getObsrValue();

        switch (category) {
            case "T1H" ->
                    result.add("기온: " + value + " ℃");
            case "REH" ->
                    result.add("습도: " + value + " %");
            case "RN1" ->
                    result.add("1시간 강수량: " + value + " mm");
            case "WSD" ->
                    result.add("풍속: " + value + " m/s");
            case "PTY" ->
                    result.add("강수형태: " + ptyText(value));
            default -> {
            }
        }
    }

    return result;
}

private String ptyText(String code) {
    return switch (code) {
        case "0" -> "없음";
        case "1" -> "비";
        case "2" -> "비/눈";
        case "3" -> "눈";
        case "5" -> "빗방울";
        case "6" -> "빗방울눈날림";
        case "7" -> "눈날림";
        default -> "알 수 없음(" + code + ")";
    };
}
```

컨트롤러에서 `getReadableWeather()`를 호출하면 사람이 읽을 수 있는 문자열 목록이 반환된다.

```java
@GetMapping("/weather")
public List<String> weather() {
    return weatherService.getReadableWeather(60, 127);
}
```

</details>

**확인**: `/weather` 응답이 다음과 같은 형태로 반환되어야 한다.

```text
기온: 18.5 ℃
습도: 62 %
강수형태: 없음
1시간 강수량: 0 mm
풍속: 2.3 m/s
```

---

## 자주 발생하는 오류

| 증상                                       | 원인과 해결 방법                                                                       |
| ---------------------------------------- | ------------------------------------------------------------------------------- |
| 인증 실패 또는 `SERVICE KEY IS NOT REGISTERED` | 인코딩 서비스키를 사용했을 가능성이 있다. 디코딩 서비스키로 교체한다. 활용 신청 직후라면 승인 정보가 반영될 때까지 기다린 뒤 다시 시도한다 |
| `resultCode 03` 또는 `NO_DATA`             | 발표 시각이 잘못되었을 수 있다. 현재 분이 40분 미만이면 한 시간 전 시각을 요청하는지 확인한다                         |
| JSON이 아닌 XML 또는 오류 문서 반환                 | `dataType`에 `"JSON"`을 전달했는지 확인한다                                                |
| DTO 필드가 모두 `null`                        | JSON 키와 DTO 필드 이름이 일치하는지 확인한다. `obsrValue` 등의 철자를 정확히 작성한다                      |
| 데이터 값이 예상과 다름                            | `nx`, `ny`는 위도와 경도가 아니다. 서울 격자 좌표인 `60`, `127`을 사용하는지 확인한다                      |

---

## 6. 학습 체크

* [ ] Feign이 인터페이스 선언을 기반으로 Spring이 구현체를 생성하는 방식임을 설명할 수 있다
* [ ] 쿼리 파라미터를 `@RequestParam`으로 표현할 수 있다
* [ ] 서비스키에 디코딩 키를 사용해야 하는 이유를 설명할 수 있다
* [ ] 중첩 JSON을 DTO로 매핑할 때 필드 이름이 JSON 키와 일치해야 함을 설명할 수 있다
* [ ] 초단기실황 발표 시각의 40분 규칙을 설명할 수 있다

## 7. 최종 완성 체크리스트

* [ ] `/weather` 호출 시 초단기실황 데이터가 JSON으로 반환된다
* [ ] `resultCode`가 `"00"`이고 여러 `category` 값이 포함되어 있다
* [ ] 코드값이 `기온: ... ℃`처럼 사람이 읽을 수 있는 형태로 변환된다
* [ ] 서비스키, 격자 좌표, 발표 시각과 관련된 오류를 확인할 수 있다
