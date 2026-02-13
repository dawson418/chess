# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ82n8u4I8kqepoHwIBBkkQqQUqvn0kAouQoKVQdkDHnc7Sq9SC4y1BQcDiSu3aA1UCn3E101S1c0oS0KHxgeIY4CR+L2zCBtX3V3uz0RqM833++4wp51GDYtF4tS6rB5uHqyo3Z4wV4DTlLer7EGxqPtCAAa3QTeWl19lCr8GQ5nq4ScTmG9amSt7LeWbfiHe7aDn-fQHFMXl8AWg7EZMRFcAAotI4AoYAAZCBZQrFcyCmsF5ptLq9AzqfJoKeKuZrX7-BwVxPoKFa1iMv4oP++h-Ds0KPJWGqGig9QIDe4oYtet4KGA6IzNgYCkoiRoBo6prBjAVDAMgWjotatozn+MAATsDo0uRLrCjAYoStaMpygqDZKtmJFUmRQYhha6IZtGi4JkmzopsKaYwDJWbEYYpS5ghzxYeKGSqGWmBgfCSE1C8ExCZ8MDNsCC5xsuPY2X2UIDqZlT3hgY4TlOlmMfMznzmMi6OaugXrpwW7eH4-heCg6AxHEiRxQlWG+FgnnMGZtYNNIx6Xse7THt0PQfqoX7DCFXboG5oE6QWVUrsZ9WPpqKEwBuFCfgUMZxgAkmgABmEBEW1xrifyyJHBANAyb17bVWgbG8kGnH1NIKDcOiqlxupY1aZUJm1GlkYGUZJlDk+LzAdUg4HcOJRgLU45OCMVwblFO7+Cinr+Ng4qdleaIwAA4kqGiZa11A5SDhUlfYSqVQ5i21dpsK1o1NUXWZSIwMgORg3MqjzfEA3DaNyHjexEmUdRSC0Sgc2Y0tiYTYplSutx4qeouMCQE1GlUytk0wIyYCE2oGLLU6ApKfUINMjAwB48DECDTAIA+FAKKkAjRMiZpaP5rUEuqJeaJnQg5YtfdV11iMyx62oCyNOMTt9dILsAIzhMEgQgps8RIHRVnzIs3zJKAnbWo24cgk7ABySrhxcN3Q+5D2ji9wx1o74Muw0btKh73u+-7yyB8HVqh3sEcIFHMeznHedzEncwpzAnTvZFnjRQEHAAOxuE4KBODEx7BHAJ4AGzwFJhgSzARQjmU1a3c+rQdPDiPTMjK5TonSpp7cRtwi8zNrIfczwej8Jr8h9ShpaJOhZfSpt1yBtCzL9RUTR4ZxhfotaWHE5ZcwlLzfmNVBakWpiLJ+6IJYYivp-BSssOZcTTAKFBSsBTKzQCgTYHUMBWBgOKUW4MVY5C-vdI6CCUAS0ttbW+l114vBbigEu9QfZ+xgMfO69xMreTHjnB2Yx3ae24WXPhZge7bhipYTaaEiGxCQAkMAii9QQCIQAKQgOQxe-hI4gE7MvR6UNzKNCaMyN8PQnZIwWvvUY2B66KKgHACAaEoBvzmB7fhd8Hi33PnvdA-5XGUA8V4nxnDpA33zBYpEAArfRaAgErjCcANxkToDRI9hTP0okZBswon-emACoxpPQCA1aYCeI8zjHzFGMCxJwPpDAZJ4okEuMyREzxOSKG+OkNU5MGD6gqW6W4xpMAUSEjUOiT0TsaGn2eB0tATDmosNtmwus-ihxCJgC9N6sjNy9y+l4TJiU1HJSgBcxAYZYDAGwC4wgeQChL0htlZ8eUCpFRKsYVGh16ovGMHExC98CntQ4JtJkKAMTGHyf6Fpws2kgG4HgaQAAhOFOhhnsyFPUDIMwZpqCVnqIw2gYCDW8DMGANgUBEEGqgT0-yYHLLqFCraKB1nY0+ddVGgiV5PQOROI5H0gA
