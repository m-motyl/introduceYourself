# INTRODUCE YOURSELF - android mobile app

## COLLABOLATORS:
* Witold Gliwa
* Mateusz Motyl
* Patryk Zarzycki

## JIRA
https://letsintroduceurself.atlassian.net/jira/software/projects/IY/boards/1

## SPRINTS
~~**1. Konfiguracja środowiska (do 17.10)**~~
* Android Studio Arctic Fox 2020 3.1
* Pixel 4 API 30
* Android 11.0 (Google Play)
* PostgreSQL (+ pgAdmin 4)

~~**2. Baza danych (do 24.10)**~~
* Diagram bazy danych
* Instalacja pluginów Kotlin Exposed
* Utworzenie modeli DAO

**3. Rejestracja i logowanie (do 3.11)**
 * Backend
    * Walidacja wprowadzanych wartości
    * Odczyt/ zapis do bazy 
 * Frontend

**4. Strona główna (do 14.11)**
   * Wysuwane boczne menu z zakładkami:
     * Profil
     * Skaner
     * ...
     * Wyloguj
   * RecyclerView z profilami (prototyp)
   * Adapter do widoku

**5. Profil użytkownika (do 28.11)**
  * Edycja profilu
    * Zdjęcia
    * Treści tekstowe (posty)
    * Linki do stron
  * Backend
    * instalacja pluginu Dexter - obsługa uprawnień (wczytywanie zdjęć)
  * Frontend
    * relacyjny układ strony i stron tekstowych

**6. Kody QR (do 12.12)**
  * Tworzenie kodów QR
  * Skaner kodów QR

**7. Karta społeczności (do 22.12)**
  * Podzakładki:
    * Zaproszenia
    * Znajomi

**8. Wyszukiwarka (do 2.01)**
  * Paginacja wyników wyszukiwania

**9. System oceniania i ranking użytkowników (do 12.01)**
  * Ocena profilu wyszukanego/ znajomego
  * Pozycjonowanie użytkowników na stronie głównej

**10. Komunikator tekstowy (do 23.01)**
  * Rozszerzenie karty społeczności > znajomi

## PODZIAŁ PRACY

* **Witold Gliwa**
  * Baza danych
  * Diagram bazy danych
  * Utworzenie bd w PostgreSQL
  * Utworzenie modeli DAO
  * PostgreSQL
  * Zapis do bazy
  * Karta społeczności - backend
  * Wyszukiwarka
  * Walidacja i hashowanie hasła
  * Odczyt z bazy
  * Profilu użytkownika - backend
  * Wczytywanie kolejnych postów - paginacja/scrollowanie
  * System oceniania użytkowników
  * wyświetlanie likeów w karcie społeczności

* **Mateusz Motyl**
  * Konfiguracja środowiska
  * Instalacja pluginu Kotlin Exposed
  * Android Studio 
  * Emulator
  * Walidacja wprowadzanych wartości
  * Instalacja Dexter - obsługa uprawnień
  * Konwersja zdjęć do przechowywania w bazie danych
  * Walidacja wielopolowa
  * Walidacja użytkownika
  * Utworzenie modelu użytkownika
  * Edycja profilu użytkownika - backend
  * Walidacja danych osobowych
  * Po wyjściu z edycji profilu, na stronie głównej widnieją stare dane
  * Skaner kodów QR
  * Generowanie kodów QR
  * Instalacja pluginu QRGenerator:1.0.3
  * Przycisk "zobacz więcej" wyświetla się gdy nie ma żadnych elementów (postów)
  * Funkcja usuwania linków dostępna gdy nie ma żadnych linków
  * Widoczność przycisków paginacji w wyszukiwarce buguje
  * Pozycjonowanie kont na stronie głównej według rankingu
  * daty w wiadomościach
  * Dezaktywowanie automatyczniego przełączania na Themes/Night po włączeniu trybu oszczędzania baterii (link w opisie)	
  
* **Patryk Zarzycki**
  * Strona główna - GUI
  * Wysuwane boczne menu z zakładkami
  * RecyclerView z profilami
  * Adapter do RecyclerView z profilami
  * GUI - frontend
  * NavigationBar - przełączanie między aktywnościami
  * Profil użytkownika - frontend
  * Relacyjny układ strony i stron tekstowych
  * Edycja profilu użytkownika - frontend
  * Edycja profilu - treści tekstowe, opis użytkownika
  * Edycja profilu - pola danych osobowych i opisu użytkownika
  * RecyclerView do wyświetlania postów
  * RecyclerView do wyświetlania linów
  * Edycja oraz dodawanie postów - frontend
  * Edycja oraz dodawanie linków - frontend
  * Rozwijane listy linków i postów
  * Karta społeczności - frontend
  * Podgląd zaproszeń
  * Zakładka "znajomi" - frontend
  * Ustawienia aplikacji - frontend
  * Zmiana ikony aplikacji
  * Możliwość zmiany motywu aplikacji
  * Dobór kolorystyki - implementacja kilku motywów (w tym ciemnego)
  
