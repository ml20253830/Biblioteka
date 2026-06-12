# Sistem za upravljanje bibliotekom

REST API za evidenciju knjiga, članova i pozajmica, sa potpunom CI/CD automatizacijom.

## Tehnologije
- Java 17, Spring Boot, H2 baza podataka, Lombok
- JUnit5, Mockito, JaCoCo
- SonarQube (SonarCloud)
- GitHub Actions (CI/CD)
- Microsoft Azure Web App
- Open Library API

## Funkcionalnosti
- Evidencija knjiga sa automatskim uvozom podataka putem ISBN (Open Library API)
- Upravljanje članovima biblioteke sa validacijom
- Kreiranje i praćenje pozajmica
- Pregled aktivnih i prekoračenih pozajmica
- Dashboard sa statistikama

Pokrenuti aplikaciju na: http://localhost:8080
