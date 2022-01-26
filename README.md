# Davide Acanfora, Sudoku Game, 14/02/2022
[MD5](https://www.md5hashgenerator.com) della stringa "davideacanfora-18": **d4beadad4d43597fbcaeb8f0c9d17251**

Progetto: **Sudoku Game**

Studente: **Davide Acanfora** (matr. 0522500870)

# Problema
TODO

# Soluzione
TODO

# Testing
La fase di testing viene eseguita in automatico ogni volta che il progetto viene compilato tramite Maven grazie all'utilizzo di **JUnit 5** e del plugin **Surefire**. È possibile, inoltre, eseguire esplicitamente i test lanciando nella directory del progetto il comando:
```shell
mvn test
```
La classe responsabile ad implementare i test è *SudokuGameImplTest*, nella quale è presente una prima fase di *setup* (annotazione *@BeforeAll*) dove vengono istanziati tre peer di cui uno master. Ogni test (annotazione *@Test*) andrà a verificare il corretto funzionamento di una particolare funzionalità della classe *SudokuGameImpl*. Nel dettaglio sono:

| Nome | Obiettivo |
|:---------|:-----|
| testCaseGenerateNewSudoku | Creazione di una nuova partita |
| testCaseJoinGame | Ingresso in una partita esistente |
| testCaseJoinInexistentGame | Ingresso in una partita inesistente non permesso |
| testCaseJoinDifferentPlayerSameNickname | Ingresso di due giocatori nella stessa partita con lo stesso nickname non permesso |
| testCaseJoinSamePlayerDifferentNickname | Ingresso dello stesso giocatore più volte nella stessa partita con nickname divero non permesso |
| testCaseGetSudoku | Ottenimento della propria matrice di gioco da una partita solo se vi si è precedentemente entrati |

...

# Usage & Docker
TODO
