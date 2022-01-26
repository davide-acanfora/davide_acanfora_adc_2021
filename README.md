# Davide Acanfora, Sudoku Game, 14/02/2022
[MD5](https://www.md5hashgenerator.com) della stringa "davideacanfora-18": **d4beadad4d43597fbcaeb8f0c9d17251**

Progetto: **Sudoku Game**

Studente: **Davide Acanfora** (matr. 0522500870)

# Problema
Creazione di un'applicazione **P2P** basata sul gioco del *Sudoku* che permetta ai giocatori di creare e partecipare alle partite, di inserire i numeri sulla propria matrice di gioco e di tenere traccia dei punti dei partecipanti. Il tutto dovrà essere memorizzato su una DHT grazie all'impiego della libreria [TomP2P](https://tomp2p.net). Inoltre, per l'implementazione dei metodi fondamentali dell'applicazione, si dovrà seguire l'interfaccia dedicata [SudokuGame.java](https://github.com/davide-acanfora/davide_acanfora_adc_2021/blob/master/src/main/java/it/davideacanfora/sudoku/SudokuGame.java).

# Soluzione
TODO

# Testing
La fase di testing viene eseguita in automatico ogni volta che il progetto viene compilato tramite Maven grazie all'utilizzo di **JUnit 5** e del plugin **Surefire**. È possibile, inoltre, eseguire esplicitamente i test lanciando nella directory del progetto il comando:
```shell
mvn test
```
La classe responsabile ad implementare i test è [SudokuGameImplTest](https://github.com/davide-acanfora/davide_acanfora_adc_2021/blob/master/src/test/java/it/davideacanfora/sudoku/SudokuGameImplTest.java), nella quale è presente una prima fase di *setup* (metodo con annotazione *@BeforeAll*) dove vengono istanziati tre peer di cui uno master. Ogni test (metodi con annotazione *@Test*) andrà a verificare il corretto funzionamento di una particolare funzionalità della classe *SudokuGameImpl*. Nel dettaglio, i test case elaborati sono:

| Nome | Obiettivo |
|:---------|:-----|
| testCaseGenerateNewSudoku | Creazione di una nuova partita |
| testCaseJoinGame | Ingresso in una partita esistente |
| testCaseJoinInexistentGame | Ingresso in una partita inesistente non permesso |
| testCaseJoinDifferentPlayerSameNickname | L'ingresso di due giocatori nella stessa partita con lo stesso nickname non è permesso |
| testCaseJoinSamePlayerDifferentNickname | L'ingresso dello stesso giocatore più volte nella stessa partita con nickname divero non è permesso |
| testCaseGetSudoku | Si può ottenere la propria matrice di gioco solo dalle partite a cui si sta partecipando |

Inoltre, alla fine di ogni singolo test ci sarà una fase di "pulizia" che consiste nel far abbandonare tutte le partite a cui i peer hanno eventualmente partecipato (annotazione *@AfterEach*), mentre alla fine di tutti i test permettiamo ai peer di abbandonare la rete tramite annuncio e conseguente shutdown del peer stesso (annotazione *@AfterAll*).

# Usage & Docker
TODO
