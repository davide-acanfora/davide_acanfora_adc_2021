# Davide Acanfora, Sudoku Game, 14/02/2022
[MD5](https://www.md5hashgenerator.com) della stringa "davideacanfora-18": **d4beadad4d43597fbcaeb8f0c9d17251**

Progetto: **Sudoku Game**

Studente: **Davide Acanfora** (matr. 0522500870)

# Problema
Creazione di un'applicazione **P2P** basata sul gioco del *Sudoku* che permetta ai giocatori di creare e partecipare alle partite, di inserire i numeri sulla propria griglia di gioco e di tenere traccia dei punti di tutti i partecipanti. Il tutto dovrà essere memorizzato su una DHT grazie all'impiego della libreria [TomP2P](https://tomp2p.net). Inoltre, per l'implementazione dei principali metodi dell'applicazione, si dovrà rispettare l'interfaccia proposta [SudokuGame.java](https://github.com/davide-acanfora/davide_acanfora_adc_2021/blob/master/src/main/java/it/davideacanfora/sudoku/SudokuGame.java).

# Soluzione
In questa soluzione viene proposta un'applicazione interagibile da terminale (grazie alle librerie [args4j](https://github.com/kohsuke/args4j) e [text-io](https://github.com/beryx/text-io)) che permette ai giocatori di creare le proprie partite di Sudoku e di sfidare chiunque vi partecipi ad indovinare i valori di quante più celle possibili prima degli altri.

Alla creazione di una nuova partita, ai giocatori sarà presentata una griglia di partenza comune per tutti, ma la particolarità è che i loro progressi non saranno condivisi, né visibili agli altri: ognuno di loro avanzerà nel gioco inserendo i numeri nella propria griglia **personale**. In altre parole, ogni giocatore sarà ignaro dell'avanzamento degli altri partecipanti, ma potrà solo intuirne il loro progresso tramite, ad esempio, il loro punteggio in classifica che verrà aggiornato e segnalato tramite messaggi man mano che inseriranno un valore in una cella.

Inoltre, ogni giocatore è spinto ad indovinare il giusto valore di ogni cella prima degli altri: la DHT, infatti, terrà globalmente traccia delle celle nelle quali è stato inserito un valore corretto da uno qualsiasi dei partecipanti. Ciò implica che solo il giocatore che li immetterà per primo riceverà effettivamente punti, mentre non riceverà nulla se inserirà un numero corretto ma già indovinato da qualcuno o addirittura gli saranno sottratti dei punti se inserirà un valore errato in una cella.

La partita terminerà quando almeno un giocatore completa la propria griglia personale, rendendo difatti inutili le successive azioni da parte degli altri giocatori, impossibilitati a guadagnare ulteriori punti.

Per rendere possibile tutto ciò sono state individuate e sviluppate le seguenti **classi**:
| Classe | Descrizione |
|:---------:|:-----|
| App | Rappresenta la classe "entry point" dell'applicazione. È responsabile di fornire all'utente il terminale e i menù per poter interagire con l'applicazione e l'istanza del peer |
| GameState | È la classe le cui istanze saranno effettivamente ciò che verrà memorizzato all'interno della DHT. Rappresenta lo stato vero e proprio di una partita. Contiene, difatti, la matrice di gioco generata, la matrice iniziale e la soluzione e tiene traccia dei partecipanti (classe Player) e dei numeri già inseriti |
| JoinedGame | È una classe utilizzata *localmente* per rappresentare e tenere traccia delle partite a cui si sta partecipando con i rispettivi nickname utilizzati |
| MessageListener | È la classe che implementa il metodo *parseMessage* per ricevere i messaggi provenienti dagli altri peer della rete |
| Player | Rappresenta il singolo giocatore partecipante ad una partita. Al suo interno, infatti, sono memorizzati il suo nickname, lo stato della sua matrice, il suo *peerID* e il suo *PeerAddress* |
| SudokuGameImpl | È la classe che implementa i metodi dell'interfaccia *SudokuGame*. Rappresenta, in sostanza, il peer che sarà utilizzato per connettersi alla rete ed è responsabile di effettuare tutte le operazioni sulla DHT e di implementare le funzionalità del gioco del Sudoku |

Infine, la gestione delle dipendenze è resa possibile grazie a Maven, i cui dettagli sono disponibili nel file [pom.xml](https://github.com/davide-acanfora/davide_acanfora_adc_2021/blob/master/pom.xml).

# Testing
La fase di testing viene eseguita in automatico ogni volta che il progetto viene compilato tramite Maven grazie all'utilizzo di **JUnit 5** e del plugin **Surefire**. È possibile, inoltre, eseguire esplicitamente i test lanciando nella directory del progetto il comando:
```shell
mvn test
```
La classe responsabile ad implementare i test è [SudokuGameImplTest](https://github.com/davide-acanfora/davide_acanfora_adc_2021/blob/master/src/test/java/it/davideacanfora/sudoku/SudokuGameImplTest.java), nella quale è presente una prima fase di *setup* (metodo con annotazione *@BeforeAll*) dove vengono istanziati tre peer di cui uno master. Ogni test (metodi con annotazione *@Test*) andrà a verificare il corretto funzionamento di una particolare funzionalità della classe *SudokuGameImpl*. Nel dettaglio, i test case proposti sono:

| Nome | Obiettivo |
|:---------|:-----|
| testCaseGenerateNewSudoku | Creazione di una nuova partita |
| testCaseJoinGame | Ingresso in una partita esistente |
| testCaseJoinInexistentGame | L'ingresso in una partita inesistente non è permesso |
| testCaseJoinDifferentPlayerSameNickname | L'ingresso di due giocatori nella stessa partita con lo stesso nickname non è permesso |
| testCaseJoinSamePlayerDifferentNickname | L'ingresso nella partita dallo stesso giocatore con nickname diversi non è permesso |
| testCaseGetSudoku | Si può ottenere la propria griglia di gioco solo dalle partite a cui si sta partecipando |

Inoltre, alla fine di ogni singolo test ci sarà una fase di "pulizia" che consiste nel far abbandonare tutte le partite a cui i peer hanno eventualmente partecipato (annotazione *@AfterEach*), mentre alla fine di tutti i test permettiamo ai peer di abbandonare la rete tramite annuncio e conseguente shutdown del peer stesso (annotazione *@AfterAll*).

# Usage & Docker
TODO
