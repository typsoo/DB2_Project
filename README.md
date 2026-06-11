<div align="center">
  
  <br><br><br>
#  Bazy Danych
## ScootShare: Scooter Sharing Web Platform

<br><br><br>

**Autorzy:**
Aliaksei Rusinovich
Ilya Paliashchuk 

<br><br><br>
</div>

---

## 1. Wykorzystane Technologie

Projekt został zaprojektowany i wdrożony jako nowoczesna aplikacja dwuwarstwowa (rozdzielony backend i frontend).

### Backend

- **Java 25**
- **Spring Boot 4.0.5**
  - **Spring Web**
  - **Spring Data JPA (Hibernate)**:
- **PostgreSQL**:
- **Hibernate Spatial & JTS Core 1.19.0**:
- **Lombok**:

### Frontend

- **React 19**
- **Vite 8.0.4**
- **TypeScript**
- **Tailwind CSS 4.0**
- **React Router Dom 7.17**
- **@vis.gl/react-google-maps**

---

## 2. Opis Modelu i Schematu Bazy Danych

Baza danych składa się z 6 kluczowych tabel realizujących pełen proces biznesowy
![Description](db_scheme.png)

### Szczegółowa specyfikacja tabel i relacji

**1. users** - Przechowuje dane kont użytkowników.

- `id` (int): unikalny identyfikator użytkownika
- `email` (varchar): adres e-mail
- `password_hash` (varchar): zaszyfrowane hasło
- `first_name` (varchar): imię
- `last_name` (varchar): nazwisko
- `created_at` (timestamp): data utworzenia konta
- `role` (role Enum): rola użytkownika (wartości: `USER`, `ADMIN`)

**2. wallets** - Służy do przechowywania salda konta użytkownika do opłacania przejazdów.

- `id` (int): unikalny identyfikator portfela
- `user_id` (int): identyfikator właściciela (użytkownika)
- `balance` (numeric): aktualne saldo
- `updated_at` (timestamp): data ostatniej zmiany salda

**3. transactions** - Przechowuje historię operacji finansowych na portfelach.

- `id` (int): unikalny identyfikator transakcji
- `wallet_id` (int): identyfikator powiązanego portfela
- `ride_id` (int): identyfikator powiązanego przejazdu
- `amount` (numeric): kwota transakcji
- `type` (transaction_type Enum): typ transakcji (wartości: `DEPOSIT`, `RIDE_PAYMENT`, `REFUND`)
- `created_at` (timestamp): data wykonania transakcji

**4. reservations** - Zarządza tymczasowymi rezerwacjami hulajnóg.

- `id` (int): unikalny identyfikator rezerwacji
- `user_id` (int): identyfikator rezerwującego użytkownika
- `scooter_id` (int): identyfikator zarezerwowanej hulajnogi
- `reserved_at` (timestamp): czas dokonania rezerwacji
- `expires_at` (timestamp): czas wygaśnięcia rezerwacji
- `status` (reservation_status Enum): aktualny stan rezerwacji (wartości: `ACTIVE`, `COMPLETED`, `CANCELLED`, `EXPIRED`)

**5. rides** - Rejestruje informacje o przejazdach.

- `id` (int): unikalny identyfikator przejazdu
- `user_id` (int): identyfikator użytkownika
- `scooter_id` (int): identyfikator wypożyczonej hulajnogi
- `start_time` (timestamp): czas rozpoczęcia
- `end_time` (timestamp): czas zakończenia
- `distance` (float): przebyty dystans
- `total_cost` (numeric): całkowity koszt przejazdu

**6. scooters** - Przechowuje fizyczny stan i lokalizację hulajnóg.

- `id` (int): unikalny identyfikator hulajnogi
- `serial_number` (varchar): numer seryjny urządzenia
- `charge_level` (int): procent naładowania baterii
- `status` (scooter_status Enum): status operacyjny (wartości: `AVAILABLE`, `IN_USE`, `RESERVED`, `MAINTENANCE`)
- `latitude` (float): szerokość geograficzna
- `longitude` (float): długość geograficzna

---

### Endpoints i operacje CRUD dla tabeli scooters

W celu umożliwienia dostępu do funkcji administracyjnych, zaimplementowano sprawdzanie ról użytkowników (`USER`, `ADMIN`). Autoryzacja bazuje na weryfikacji roli na podstawie identyfikatora przesłanego w nagłówku `X-User-Id`.

Funkcja weryfikująca uprawnienia w kodzie wygląda następująco:
```java
public void verifyAdmin(Integer userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    if (user.getRole() != org.internetstore.scootersrentapplication.entity.enums.Role.ADMIN) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Admin role required.");
    }
}
```

Poniżej przedstawiono zestawienie operacji CRUD udostępnionych dla zasobu hulajnóg.

#### 1. Dodawanie hulajnogi

- **Endpoint:** `POST /api/scooters`
- **Fragment kodu:**
  ```java
  @PostMapping
  public ResponseEntity<ScooterResponseDto> createScooter(
          @RequestHeader("X-User-Id") Integer adminId,
          @Valid @RequestBody ScooterCreateDto dto) {
      userService.verifyAdmin(adminId);
      Scooter createdScooter = scooterService.createScooter(dto);
      return new ResponseEntity<>(mapToDto(createdScooter), HttpStatus.CREATED);
  }
  ```
- **Funkcja w Repozytorium (dziedziczona z `JpaRepository`):**
  ```java
  <S extends Scooter> S save(S entity);
  ```
- **Zapytanie Hibernate SQL:**
  ```sql
  INSERT INTO scooters (serial_number, charge_level, status, latitude, longitude)
  VALUES (?, ?, ?, ?, ?);
  ```
- **Opis:** Tworzy nowy wpis hulajnogi w bazie danych. Zapisuje dane z obiektu transferowego wywołując wbudowaną w Spring Data JPA metodę `save()`.

#### 2. Pobieranie danych hulajnogi 

- **Endpoint:** `GET /api/scooters/{id}`
- **Fragment kodu :**
  ```java
  @GetMapping("/{id}")
  public ResponseEntity<ScooterResponseDto> getScooterById(@PathVariable Integer id) {
      Scooter scooter = scooterService.getScooterById(id);
      return ResponseEntity.ok(mapToDto(scooter));
  }
  ```
- **Funkcja w Repozytorium (dziedziczona z `JpaRepository`):**
  ```java
  Optional<Scooter> findById(Integer id);
  ```
- **Zapytanie Hibernate SQL:**
  ```sql
  SELECT id, serial_number, charge_level, status, latitude, longitude
  FROM scooters
  WHERE id = ?;
  ```
- **Opis:** Pobiera szczegółowe informacje o konkretnej hulajnodze na podstawie jej unikalnego identyfikatora (`id`). Wykorzystuje standardową metodę `findById()` z repozytorium JPA.

#### 3. Aktualizacja lokalizacji 

- **Endpoint:** `PUT /api/scooters/{id}/location`
- **Fragment kodu:**

  ```java
  @PutMapping("/{id}/location")
  public ResponseEntity<Void> updateLocation(
          @PathVariable Integer id,
          @Valid @RequestBody ScooterLocationUpdateDto dto) {
      locationService.updateLocation(id, dto.latitude(), dto.longitude());
      return ResponseEntity.ok().build();
  }
  ```

- **Zapytanie Hibernate SQL (PostGIS):**
  ```sql
  UPDATE scooters
  SET location = ST_GeomFromText(?, 4326)
  WHERE id = ?;
  ```
- **Opis:** Aktualizuje fizyczną pozycję hulajnogi na mapie. W serwisie encja jest pobierana, jej właściwość przestrzenna jest nadpisywana, a Hibernate automatycznie generuje i wykonuje polecenie `UPDATE` przy zamykaniu transakcji.

#### 4. Usuwanie hulajnogi 

- **Endpoint:** `DELETE /api/scooters/{id}`
- **Fragment kodu:**
  ```java
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteScooter(@PathVariable Integer id) {
      scooterService.deleteScooter(id);
      return ResponseEntity.noContent().build();
  }
  ```
- **Funkcja w Repozytorium (dziedziczona z `JpaRepository`):**
  ```java
  void deleteById(Integer id);
  ```
- **Zapytanie Hibernate SQL:**
  ```sql
  DELETE FROM scooters
  WHERE id = ?;
  ```
- **Opis:** Trwale usuwa rekord hulajnogi z bazy danych w oparciu o przekazane `id`. Pod maską wywoływana jest metoda `deleteById()` dostarczana przez interfejs `JpaRepository`. Jest wykorzystywana dla testowania, w produkcyjnej wersji bedzię się zmieniał status hulajnogi.

#### 5. Pobieranie hulajnóg w obszarze

- **Endpoint:** `GET /api/scooters/area?minLat=...&minLon=...&maxLat=...&maxLon=...`
- **Fragment kodu:**
  ```java
  @GetMapping("/area")
  public ResponseEntity<List<ScooterResponseDto>> getScootersInArea(
          @RequestParam double minLat, @RequestParam double minLon,
          @RequestParam double maxLat, @RequestParam double maxLon) {
      Polygon boundingBox = locationService.createBoundingBox(minLat, minLon, maxLat, maxLon);
      List<Scooter> scooters = locationService.findAvailableInArea(boundingBox);
      // ...
  }
  ```
- **Funkcja w Repozytorium (`ScooterRepository`):**
  ```java
  @Query("SELECT s FROM Scooter s WHERE s.status = :status AND within(s.location, :bbox) = true")
  List<Scooter> findAvailableInArea(@Param("status") ScooterStatus status, @Param("bbox") Polygon bbox);
  ```
- **Zapytanie Hibernate SQL:**
  ```sql
  SELECT id, serial_number, charge_level, status, location
  FROM scooters
  WHERE status = ? AND ST_Within(location, ?) = true;
  ```
- **Opis:** Zwraca listę hulajnóg o statusie "dostępna", które znajdują się w określonym na mapie prostokąci. Zamiast skanować wszystkie wpisy w pamięci, wykorzystuje funkcję przestrzenną `within` obsługiwaną bezpośrednio na poziomie bazy danych PostGIS.

---

## 3. Realizacja Operacji Bazodanowych i Dyskusja Zastosowanych Metod

### A. Transakcja Rezerwacji i Blokowanie Pesymistyczne

Proces rezerwacji sprzętu jest operacją krytyczną, która musi zostać w pełni zabezpieczona przed wyścigami. Poniżej znajduje się struktura i zasada działania tego mechanizmu.

#### Utworzenie rezerwacji

- **Fragment kodu:**

  ```java
  @PostMapping
  public ResponseEntity<ReservationResponseDto> createReservation(
          @RequestHeader("X-User-Id") Integer userId,
          @Valid @RequestBody ReservationCreateRequestDto request) {
      ReservationResponseDto response = reservationService.reserveScooter(userId, request.scooterId());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  ```

- **Funkcja z blokadą pesymistyczną:**

  ```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Scooter s WHERE s.id = :id")
  Optional<Scooter> findByIdWithLock(@Param("id") Integer id);
  ```

- **Zapytanie Hibernate SQL:**

  ```sql
  BEGIN;

  -- Baza danych zakłada wyłączną blokadę (FOR UPDATE) na odczytywany rekord
  SELECT id, serial_number, charge_level, status, latitude, longitude
  FROM scooters
  WHERE id = ?
  FOR UPDATE;

  -- Po sprawdzeniu stanu, hulajnoga zostaje zaktualizowana
  UPDATE scooters SET status = 'RESERVED' WHERE id = ?;

  -- Rejestracja nowego zdarzenia rezerwacji
  INSERT INTO reservations (user_id, scooter_id, reserved_at, expires_at, status)
  VALUES (?, ?, ?, ?, 'ACTIVE');

  COMMIT;
  ```

- **Opis działania i zapobieganie kolizjom:**
  1. **Atomowość (`@Transactional` w serwisie):** Cały proces zmiany statusu hulajnogi na "zarezerwowana" oraz dodanie wpisu w tabeli `reservations` wykonywany jest w jednej transakcji. Jeśli jakikolwiek krok zawiedzie, baza wykonuje całkowity `ROLLBACK`, zachowując integralność.
  2. **Blokowanie wyścigów (`@Lock` i `FOR UPDATE`):** To kluczowy element zapobiegający sytuacji, w której dwie osoby klikają "Rezerwuj" w tej samej milisekundzie.
     - Zastosowanie metody `findByIdWithLock` generuje na bazie PostgreSQL klauzulę `FOR UPDATE`.
     - Kiedy Użytkownik A próbuje zarezerwować hulajnogę, baza blokuje ten jeden konkretny wiersz.
     - Jeżeli Użytkownik B spróbuje zarezerwować tę samą hulajnogę zanim transakcja A się zakończy, silnik PostgreSQL sprzętowo "uśpi" wątek B i każe mu czekać.
     - Po zakończeniu transakcji A (status hulajnogi zmienia się na `RESERVED`), blokada na wierszu jest zwalniana.
     - Wątek B się "budzi", wykonuje się jego `SELECT`, lecz widzi on już zaktualizowany status (`RESERVED`). W logice aplikacji następuje weryfikacja i natychmiastowe wyrzucenie wyjątku `The scooter is already taken`. Gwarantuje to brak duplikacji rezerwacji dla tej samej hulajnogi.

---

### B. Transakcja Rozliczenia Portfela i Zakończenia Przejazdu

Proces kończenia przejazdu wiąże się z automatycznym pobraniem środków z portfela użytkownika. Podobnie jak w przypadku rezerwacji, operacja ta wymaga zabezpieczenia przed wyścigami, by zapobiec np. problemowi podwójnego wydatkowania, w którym użytkownik mógłby wykorzystać to samo saldo do dwóch równoległych operacji płatniczych.

#### Zakończenie przejazdu i pobranie opłaty

- **Fragment kodu:**
  ```java
  @PostMapping("/end")
  public ResponseEntity<?> endRide(@Valid @RequestBody RideEndRequestDto request) {
      try {
          return ResponseEntity.ok(rideService.endRide(request));
      } catch (RuntimeException e) {
          return ResponseEntity.badRequest().body(e.getMessage());
      }
  }
  ```
- **Funkcja w Repozytorium (`WalletRepository` z blokadą pesymistyczną):**
  ```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
  Optional<Wallet> findByUserIdWithLock(Integer userId);
  ```
- **Zapytanie Hibernate SQL:**
  ```sql
  BEGIN;
  -- Blokada rekordu portfela do końca transakcji
  SELECT id, user_id, balance, updated_at
  FROM wallets
  WHERE user_id = ?
  FOR UPDATE;
  -- Pobranie odpowiedniej kwoty z salda użytkownika
  UPDATE wallets SET balance = ? WHERE id = ?;
  -- Zapisanie historii płatności (TransactionType.RIDE_PAYMENT)
  INSERT INTO transactions (wallet_id, ride_id, amount, type, created_at)
  VALUES (?, ?, ?, 'RIDE_PAYMENT', ?);
  -- Zwolnienie hulajnogi dla innych i zapisanie rekordu Ride
  UPDATE scooters SET status = 'AVAILABLE' WHERE id = ?;
  UPDATE rides SET end_time = ?, distance = ?, total_cost = ? WHERE id = ?;
  COMMIT;
  ```
- **Opis działania i zapobieganie kolizjom:**
  1. **Atomowość (`@Transactional`):** Podobnie jak przy rezerwacjach, obliczenie kosztu, zaktualizowanie rekordu przejazdu (`rides`), ściągnięcie pieniędzy z portfela (`wallets`), zapis w historii (`transactions`) oraz zwolnienie hulajnogi (`scooters`) objęte są jedną dużą transakcją SQL. Błąd na którymkolwiek z etapów wycofa wszystkie te operacje.
  2. **Zabezpieczenie środków (`FOR UPDATE` na portfelu):**
     - Wywołanie metody `findByUserIdWithLock` wymusza użycie blokady `FOR UPDATE` na konkretnym wierszu w tabeli portfeli użytkowników.
     - Jeśli w tym samym momencie użytkownik chciałby zainicjować inną płatność z tego samego salda, to żądanie będzie musiało zaczekać na poziomie bazy danych aż do zwolnienia blokady.
     - Chroni to przed problemem odczytu nieaktualnego salda przez nakładające się transakcje. Saldo odczytane w pamięci serwera celem wyliczenia nowej kwoty jest zablokowane dla innych zapisów, co gwarantuje pełną konsystencję księgową.


---

## 4. System Raportowania 

Zamiast pobierania pełnych tabel do pamięci operacyjnej Javy i filtrowania ich za pomocą strumieni, system generuje zaawansowane raporty biznesowe wykonując natywne zapytania SQL, co optymalizuje czas wykonania i redukuje zużycie pamięci.

### Raport Popularności Hulajnóg
Raport agreguje dystans, czas użycia i liczbę przejazdów per hulajnoga używając złączenia (`JOIN`) oraz funkcji agregujących bazodanowych.

**Kod Java:**
```java
@Query(value = "SELECT s.id as scooterId, s.serial_number as serialNumber, " +
               "COUNT(r.id) as totalRides, " +
               "COALESCE(SUM(r.distance), 0) as totalDistance, " +
               "COALESCE(SUM(EXTRACT(EPOCH FROM (r.end_time - r.start_time))/60), 0) as totalTime " +
               "FROM rides r JOIN scooters s ON r.scooter_id = s.id " +
               "WHERE r.end_time IS NOT NULL " +
               "GROUP BY s.id, s.serial_number", nativeQuery = true)
List<ScooterPopularityProjection> getScooterPopularity();
```

**Generowany SQL:**
```sql
SELECT s.id as scooterId, 
       s.serial_number as serialNumber, 
       COUNT(r.id) as totalRides, 
       COALESCE(SUM(r.distance), 0) as totalDistance, 
       COALESCE(SUM(EXTRACT(EPOCH FROM (r.end_time - r.start_time))/60), 0) as totalTime 
FROM rides r 
JOIN scooters s ON r.scooter_id = s.id 
WHERE r.end_time IS NOT NULL 
GROUP BY s.id, s.serial_number;
```

**Opis działania zapytania:**
Zapytanie łączy tabele przejazdów (`rides`) i hulajnóg (`scooters`) używając operacji `JOIN`. Dzięki `GROUP BY` wyniki są spłaszczane tak, aby każdy wiersz reprezentował unikalną hulajnogę. Funkcje agregujące wykonują obliczenia na zgrupowanych wierszach: `COUNT` zlicza wszystkie przejazdy, a `SUM` sumuje łączny dystans. Co więcej, funkcja bazy danych `EXTRACT(EPOCH FROM ...)` pozwala na obliczenie dokładnego czasu trwania każdego przejazdu w minutach na poziomie samej bazy, bez konieczności przesyłania wszystkich dat do aplikacji Javy. Funkcja `COALESCE` służy jako mechanizm obronny zwracający wartość `0` w sytuacji wystąpienia wartości `NULL`.

### Raport Średniego Czasu Przejazdów Użytkowników
Zestawienie oblicza średni czas spędzony na hulajnodze dla każdego użytkownika.

**Kod Java:**
```java
@Query(value = "SELECT u.id as userId, u.first_name as firstName, u.last_name as lastName, " +
               "COUNT(r.id) as totalRides, " +
               "COALESCE(AVG(EXTRACT(EPOCH FROM (r.end_time - r.start_time))/60), 0) as averageDurationMinutes " +
               "FROM rides r JOIN users u ON r.user_id = u.id " +
               "WHERE r.end_time IS NOT NULL " +
               "GROUP BY u.id, u.first_name, u.last_name", nativeQuery = true)
List<AverageRideDurationProjection> getAverageRideDuration();
```

**Generowany SQL:**
```sql
SELECT u.id as userId, 
       u.first_name as firstName, 
       u.last_name as lastName, 
       COUNT(r.id) as totalRides, 
       COALESCE(AVG(EXTRACT(EPOCH FROM (r.end_time - r.start_time))/60), 0) as averageDurationMinutes 
FROM rides r 
JOIN users u ON r.user_id = u.id 
WHERE r.end_time IS NOT NULL 
GROUP BY u.id, u.first_name, u.last_name;
```

**Opis działania zapytania:**
To zapytanie zestawia przejazdy z tabelą użytkowników (`users`), grupując dane dla każdego klientu. Głównym mechanizmem agregującym jest tu matematyczna funkcja średniej arytmetycznej `AVG()`. Oblicza ona przeciętną długość trwania przejazdów danego użytkownika w minutach na bazie dokładnej różnicy czasowej. 

---

## 5. Instrukcja Uruchomienia Projektu

Poniżej przedstawiono kroki niezbędne do lokalnego uruchomienia aplikacji na systemie operacyjnym.

### Wymagania wstępne

- **Java SDK 25** (np. OpenJDK 25, Temurin 25)
- **Node.js** (wersja v18 lub nowsza) z menedżerem pakietów `npm`
- **PostgreSQL** (wersja 14 lub nowsza) z zainstalowanym rozszerzeniem przestrzennym **PostGIS**

---

### Krok 1: Przygotowanie Bazy Danych PostgreSQL

1. Uruchom serwer PostgreSQL i zaloguj się do konsoli SQL.
2. Utwórz nową bazę danych:
   ```sql
   CREATE DATABASE scooters_db;
   ```
3. Połącz się z utworzoną bazą danych i **koniecznie włącz rozszerzenie przestrzenne PostGIS**:
   ```sql
   \c scooters_db;
   CREATE EXTENSION IF NOT EXISTS postgis;
   ```

---

### Krok 2: Konfiguracja i Uruchomienie Backend (Java Spring Boot)

1. Przejdź do katalogu backendu:
   ```bash
   cd scooters-rent-application
   ```
2. Stwórz plik konfiguracyjny `.env` w katalogu głównym projektu backendowego. Plik powinien zawierać poniższe dane:
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/scooters_db
   DB_USER=nazwa_twojego_uzytkownika_postgres
   DB_PASSWORD=haslo_twojego_uzytkownika_postgres
   ```
3. Uruchom aplikację backendową za pomocą wrappera Gradle:
   - **Linux / macOS**:
     ```bash
     chmod +x gradlew
     ./gradlew bootRun
     ```
   - **Windows (PowerShell/CMD)**:
     ```cmd
     gradlew.bat bootRun
     ```
4. Aplikacja backendowa uruchomi się domyślnie na porcie `8080` (`http://localhost:8080`). Hibernate automatycznie wygeneruje wymagane tabele w bazie danych PostgreSQL.

---

### Krok 3: Konfiguracja i Uruchomienie Frontend (React)

1. Otwórz nowy terminal i przejdź do katalogu frontendu:
   ```bash
   cd front
   ```
2. Zainstaluj niezbędne biblioteki i zależności:
   ```bash
   npm install
   ```
3. Upewnij się, że plik `.env` w folderze `front` zawiera prawidłowy klucz Google Maps API:
   ```env
   VITE_GOOGLE_MAPS_API_KEY=YOUR_GOOGLE_CLOUD_KEY
   ```
4. Uruchom serwer deweloperski Vite:
   ```bash
   npm run dev
   ```
5. Aplikacja frontendowa będzie dostępna w przeglądarce pod adresem: `http://localhost:5173`.

---
