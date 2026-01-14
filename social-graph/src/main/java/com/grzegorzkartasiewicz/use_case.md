# Social Graph Module - Use Case Specification

Ten dokument definiuje logikę biznesową dla modułu **social-graph**.
Dokument podzielony jest na obsługę relacji **Symetrycznych** (Friends) oraz **Asymetrycznych** (
Follow).

## Aktorzy

* **Requester (Zgłaszający):** Użytkownik inicjujący akcję.
* **Target (Cel):** Użytkownik, na którym wykonywana jest akcja.

## Wspólne Invarianty (Business Rules)

1. **No Self-Interaction:** Użytkownik nie może wysłać zaproszenia do siebie, zaakceptować siebie,
   dodać siebie do znajomych ani obserwować siebie.
2. **Consistency (Friends):** Relacja znajomości jest symetryczna (jeśli A ma w znajomych B, to B
   musi mieć A).
3. **Consistency (Follow):** Relacja obserwowania jest asymetryczna (A może obserwować B, bez
   wzajemności).
4. **No Duplicates:** Nie można powielać istniejących relacji.

---

## CZĘŚĆ 1: RELACJE SYMETRYCZNE (ZNAJOMI)

### UC1: Send Friend Request (Wysłanie zaproszenia)

**Opis:** Użytkownik A wysyła zaproszenie do znajomych Użytkownikowi B. Automatycznie zaczyna go też
obserwować (jak na FB).

#### Dane Wejściowe

* `requesterId`: UUID użytkownika wysyłającego.
* `targetId`: UUID użytkownika odbierającego.

#### Warunki Wstępne (Pre-conditions)

* `requesterId` != `targetId`.
* `targetId` nie znajduje się na liście `friends` użytkownika requester.
* `targetId` nie znajduje się na liście `sentFriendRequests` użytkownika requester.
* `requesterId` nie znajduje się na liście `sentFriendRequests` użytkownika target.

#### Kroki (Main Flow)

1. System waliduje warunki wstępne.
2. System dodaje `targetId` do kolekcji `sentFriendRequests` w agregacie Requestera.
3. System dodaje `requesterId` do kolekcji `receivedFriendRequests` w agregacie Targeta.
4. **(Opcjonalnie)** System wywołuje logikę Follow (UC5) - Requester zaczyna obserwować Targeta.

#### Warunki Końcowe (Post-conditions)

* `requester.sentFriendRequests` zawiera `targetId`.
* `target.receivedFriendRequests` zawiera `requesterId`.

#### Scenariusze Błędów (Exceptions)

* `SelfInteractionException`: Jeśli `requesterId` == `targetId`.
* `RelationAlreadyExistsException`: Jeśli są już znajomymi.
* `RequestAlreadySentException`: Jeśli zaproszenie już istnieje.

---

### UC2: Accept Friend Request (Akceptacja zaproszenia)

**Opis:** Użytkownik B akceptuje otrzymane zaproszenie od Użytkownika A.

#### Dane Wejściowe

* `approverId`: UUID użytkownika akceptującego.
* `requesterId`: UUID użytkownika, który wysłał zaproszenie.

#### Warunki Wstępne (Pre-conditions)

* `requesterId` znajduje się na liście `receivedFriendRequests` użytkownika approver.

#### Kroki (Main Flow)

1. System usuwa `requesterId` z `receivedFriendRequests` u Approvera.
2. System usuwa `approverId` z `sentFriendRequests` u Requestera.
3. System dodaje `requesterId` do listy `friends` u Approvera.
4. System dodaje `approverId` do listy `friends` u Requestera.
5. **(Opcjonalnie)** Approver zaczyna obserwować Requestera (wzajemna obserwacja).

#### Warunki Końcowe (Post-conditions)

* Zaproszenia usunięte z obu stron.
* `approver.friends` zawiera `requesterId`.
* `requester.friends` zawiera `approverId`.

#### Scenariusze Błędów (Exceptions)

* `RequestNotExistsException`: Jeśli brak zaproszenia.

---

### UC3: Reject Friend Request (Odrzucenie zaproszenia)

**Opis:** Użytkownik B odrzuca zaproszenie od Użytkownika A.

#### Dane Wejściowe

* `rejectorId`: UUID użytkownika odrzucającego.
* `requesterId`: UUID użytkownika, który wysłał zaproszenie.

#### Kroki (Main Flow)

1. System usuwa `requesterId` z `receivedFriendRequests` u Rejectora.
2. System usuwa `rejectorId` z `sentFriendRequests` u Requestera.

---

### UC4: Remove Friend (Usunięcie znajomego / Unfriend)

**Opis:** Użytkownik A usuwa ze znajomych Użytkownika B. Zazwyczaj oznacza to też zaprzestanie
obserwowania (Unfollow).

#### Dane Wejściowe

* `initiatorId`: UUID użytkownika zrywającego relację.
* `friendId`: UUID usuwanego znajomego.

#### Kroki (Main Flow)

1. System usuwa `friendId` z listy `friends` użytkownika Initiator.
2. System usuwa `initiatorId` z listy `friends` użytkownika Friend.
3. **(Opcjonalnie)** System usuwa `friendId` z listy `following` u Initiatora.

#### Warunki Końcowe (Post-conditions)

* `initiator.friends` **NIE** zawiera `friendId`.
* `friend.friends` **NIE** zawiera `initiatorId`.

---

## CZĘŚĆ 2: RELACJE ASYMETRYCZNE (OBSERWOWANIE)

### UC5: Follow User (Zaobserwuj)

**Opis:** Użytkownik A zaczyna obserwować Użytkownika B (widzi jego posty publiczne), bez
konieczności bycia znajomym.

#### Dane Wejściowe

* `followerId`: UUID użytkownika, który chce obserwować.
* `followedId`: UUID użytkownika, który ma być obserwowany.

#### Warunki Wstępne (Pre-conditions)

* `followerId` != `followedId`.
* `followedId` nie znajduje się na liście `following` użytkownika `followerId`.

#### Kroki (Main Flow)

1. System dodaje `followedId` do listy `following` u Followera.
2. System dodaje `followerId` do listy `followers` u Followeda.

#### Warunki Końcowe (Post-conditions)

* `follower.following` zawiera `followedId`.
* `followed.followers` zawiera `followerId`.

#### Scenariusze Błędów (Exceptions)

* `AlreadyFollowingException`: Jeśli relacja już istnieje.

---

### UC6: Unfollow User (Przestań obserwować)

**Opis:** Użytkownik A przestaje obserwować Użytkownika B. Można przestać obserwować znajomego,
pozostając znajomym (np. gdy ktoś spamuje).

#### Dane Wejściowe

* `followerId`: UUID użytkownika, który chce przestać obserwować.
* `followedId`: UUID użytkownika, który jest obserwowany.

#### Kroki (Main Flow)

1. System usuwa `followedId` z listy `following` u Followera.
2. System usuwa `followerId` z listy `followers` u Followeda.

#### Warunki Końcowe (Post-conditions)

* `follower.following` **NIE** zawiera `followedId`.
* `followed.followers` **NIE** zawiera `followerId`.