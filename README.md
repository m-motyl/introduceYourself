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
  * Diagram bazy danych
  * Utworzenie encji w PostgreSQL
  * Utworzenie modeli DAO/DSL
  * Odczyt / zapis danych z bazy danych
  * Hashowanie hasła
  * Backend użytkownika
  * Backend systemu znajomych
  * System oceniania użytkowników / postów
  * Obróbka danych z wielu tabel do modeli
  * Wyszukiwanie użytkowników
  * Popularni użytkownicy danego dnia
  * Paginacja
    * wyszukiwarki
    * znajomych / zaproszeń
    * postów użytkownika
    * wiadomości
    
    
  ![diagram](https://user-images.githubusercontent.com/58432170/214069054-129db0b6-bbef-4c1e-af75-585e0e4958e9.png)


* **Mateusz Motyl**
   * Konfiguracja środowiska
      * android studio
      * emulator
   * Instalacja dextera - plugin obsługi uprawnień do pamięci wewnętrznej telefonu
      * kodowanie/ rozkodowanie zdjęć z bazy danych
      * wgrywanie zdjęć z pamięci wewnętrznej telefonu
   * Tworzenie modeli klas (pakiet Models)
   * Logowanie i rejestracja
      * walidacje pól wejściowych (długości napisów, regexy: [imie, nazwisko, hasło, email], wymaganie zdjęcia)
      * tworzenie konta uzytkownika
   * Strona główna
      * adapter listy modeli do widoku
      * serializacja danych z adaptera do kolejnej aktywności (użytkownik z listy -> profil użytkownika, onClickListener)
      * dane aktualnie zalogowanego użytkownika w side barze strony głównej (i aktualizacja po edycji)
      * przełączanie między aktywnościami (zgodnie z cyklem życia aktywności); wylogowywanie - czyszczenie stosu programu
   * Wyszukiwarka
      * wyszukiwanie w zależności od wprowadzonych wartości (regex: wyszukiwanie po mailu; wyszukiwanie po imieniu, nazwisku)
      * serializacja (onClickListener)
      * toolbar z możliwością cofania
   * Karta społeczności
      * switch między kartami: znajomi, zaproszenia
      * adapter do wyświetlania listy zaproszeń
      * toolbar z możliwością cofania
      * przyjmowanie/ usuwanie zaproszenia do znajomych (onAcceptClickListner, onDeclineClickListener)
   * Skaner QR
      * pluginy QRgenerator, Code-Scanner; pluginy do obsługi kodów QR, skanowanie, tworzenie kodu
      * switch pomiędzy kartami: udostepnij kod, zeskanuj kod
      * tworzenie kodu z emailem użytkownika
      * toolbar z możliwością cofania
      * skanowanie kodu i serializacja do profilu użytkownika po wyszukaniu (+ dostęp do kamerki internetowej w emulatorze)
   * Wiadomości
      * adapter do wyświetlania wiadomości; pozycjonowanie wiadomości przy krawędziach ekranu w zależności od użytkownika, przesyłanie wiadomości, stronicowanie wiadomości (przycisk załaduj więcej), znaczniki czasu przy wiadomościach, daty w przerwach 15-minutowych między wiadomościami i w różnicach dni
      * toolabr z możliwością cofania i mailem użytkownika
      * scrollowanie przy wczytywaniu wiadomości/ przy wysłaniu nowej wiadomości
   * Profil użytkownika
      * wyświetlanie danych użytkownika w profilu
      * wyświetlanie postów (adapter)
      * wyświetlanie linków (adapter)
      * toolbar z możliwością cofania
      * otwieranie linków i przekierowywanie aplikacji
      * system oceniania; zaznaczanie/ odznaczanie łapek
      * dodawanie/ usuwanie znajomego
      * otwieranie (i serializacja do) direct message
   * Ustawienia
      * zmiana motywu i reakcja aplikacji na wybrany motyw
      * zmiana hasła (walidacja regex)
      * toolbar z możliwością cofania
   * Edycja profilu
      * edycja zdjęcia profilowego
      * edycja/ usuwanie zdjęcia w tle
      * edycja danych osobowych (walidacja pól tekstowych)
      * edycja opisu (walidacja pól tekstowych)
      * toolbar z możliwością cofania
      * dodawanie postów (walidacja pól tektsowych)
      * przełączanie między stroniami postów
      * rozwijanie list postów i linków
      * edycja/ usuwanie postów (walidacja pól tekstowych)
      * dodawanie/ usuwanie linków (regex)
  
  
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
  
