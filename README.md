Backend (Spring Boot)

  De obicei rulez backend-ul pe portul 8081, dar nu trebuie să schimbi nimic aici, e deja setat în application.properties.
  Tot ce trebuie să faci e să pornești aplicația din IDE.
  O să adaug și ce trebuie completat în Run Config ca să te conectezi la baza de date hostată – practic acolo bagi URL-ul bazei + user/parolă dacă e cazul.

Frontend (HTML/JS/etc.)

  Intră în folderul în care ai frontend-ul. Rulează comanda asta în terminal:
  
          python -m http.server 5500

  Asta va porni un server local pe portul 5500, adică poți deschide browserul și accesa frontend-ul la adresa:
  
          http://localhost:5500
