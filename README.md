# GetJson — Framework HTTP minimalista em Kotlin

**GetJson** é um micro-framework para criar APIs HTTP com respostas em JSON usando apenas Kotlin puro (sem bibliotecas externas).

---

## Requisitos

- Java 17+
- Kotlin 1.9+
- Gradle

---

## Como correr

1. Clonar o repositório:

```bash
git clone https://github.com/teu-username/json-parser.git
cd json-parser
```
2. Compilar e gerar o .jar:
```bash
./gradlew clean build
```
3. Correr a aplicação (inicia o servidor na porta 8080):
```bash
java -jar app/build/libs/app-1.0-SNAPSHOT.
```
## Endpoints disponíveis

| Método | URL                         | Parâmetros  | Descrição                           |
| ------ | --------------------------- | ----------- | ----------------------------------- |
| GET    | `/api/args?n=42&text=hello` | `n`, `text` | Devolve um objeto com os parâmetros |
| GET    | `/api/path/{value}`         | path param  | Devolve uma string com o valor      |
| GET    | `/api/course`               | —           | Devolve um objeto JSON complexo     |

## Exemplos de chamadas
#### Obter argumentos via query parameters
```
"http://localhost:8080/api/args?n=42&text=hello"
```
Resposta esperada (JSON):

{
  "n": 42,
  "text": "hello"
}

---
#### Obter valor via path parameter
```bash
"http://localhost:8080/api/path/algumValor"
```
Resposta esperada (JSON):

"valorTal"

---
#### Obter um objeto complexo
```
"http://localhost:8080/api/course
```
Resposta esperada (JSON):

{
  "name": "PA",
  "credits": 6,
  "mandatory": true,
  "type": "PROJECT",
  "evaluation": {
    "quizzes": 5,
    "project": 1
  }
}
