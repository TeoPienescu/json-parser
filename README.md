# GetJson — Framework HTTP minimalista em Kotlin

**GetJson** é um micro-framework para criar APIs HTTP com respostas em JSON usando apenas Kotlin puro (sem bibliotecas externas).

---

## Requisitos

- Java 17+
- Kotlin 1.9+
- Gradle

---

## Funcionalidades 

Representação em memória de todos os tipos JSON:
- Objetos (`JsonObject`)
- Arrays (`JsonArray`)
- Strings (`JsonString`)
- Números (`JsonNumber`)
- Booleanos (`JsonBoolean`)
- Nulos (`JsonNull`)

Manipulação funcional:
- Filtragem de objetos e arrays
- Mapeamento de arrays

Visitação (Visitor Pattern):
- Validação de objetos (chaves únicas)
- Verificação de homogeneidade de tipos em arrays

Serialização para formato JSON válido

Inferência automática de estruturas JSON a partir de:
- Objetos primitivos
- Enums
- Listas
- Mapas (`Map<String, Any?>`)
- Data classes Kotlin (via reflexão)

---

## Exemplo de Utilização

### 1. Criar JSON manualmente

```kotlin
val json = JsonObject(
    mapOf(
        "nome" to JsonString("João"),
        "idade" to JsonNumber(21),
        "ativo" to JsonBoolean(true),
        "disciplinas" to JsonArray(
            listOf(JsonString("PA"), JsonString("IA"), JsonString("TP"))
        )
    )
)
println(json.serialize())
// Resultado: {"nome":"João","idade":21,"ativo":true,"disciplinas":["PA","IA","TP"]}
```
### 2. Filtrar arrays
```kotlin
val filtrado = (json["disciplinas"] as JsonArray).filter {
    it is JsonString && it.value.startsWith("P")
}
// Resultado: ["PA", "TP"]
```
### 3. Mapeamento em arrays
```kotlin
val emMaiusculas = (json["disciplinas"] as JsonArray).map {
    if (it is JsonString) JsonString(it.value.uppercase()) else it
}
```

## Inferência via Kotlin

### 1. Exemplo de estrutura

```kotlin
data class Curso(
    val nome: String,
    val ects: Int,
    val avaliacao: List<Avaliacao>
)

data class Avaliacao(
    val nome: String,
    val peso: Double,
    val obrigatoria: Boolean,
    val tipo: TipoAvaliacao?
)

enum class TipoAvaliacao { TESTE, PROJETO, EXAME }
```

### 2. Conversão para JSON
```kotlin
val curso = Curso(
    "PA", 6, listOf(
        Avaliacao("quizzes", 0.2, false, null),
        Avaliacao("projeto", 0.8, true, TipoAvaliacao.PROJETO)
    )
)

val json = JsonModelConverter.toJsonValue(curso)
println(json.serialize())
```

### 3. Saída esperada
```json
{
  "nome": "PA",
  "ects": 6,
  "avaliacao": [
    {
      "nome": "quizzes",
      "peso": 0.2,
      "obrigatoria": false,
      "tipo": null
    },
    {
      "nome": "projeto",
      "peso": 0.8,
      "obrigatoria": true,
      "tipo": "PROJETO"
    }
  ]
}
```


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

## Correr testes
Todos os testes JUnit encontram-se em src/test/.
Para os correr:
```bash
./gradlew test
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
