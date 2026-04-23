# Gestão de Logs

Aprende a usar os logs para debugar a tua aplicação e como controlar o que aparece no terminal.

---

## 1. Como usar Logs no Java
Usamos a biblioteca `@Slf4j` do Lombok. É a forma mais limpa de fazer logs.

**Exemplo:**
```java
@Slf4j
@Service
public class MeuServico {

    public void processar() {
        log.info("A iniciar o processamento...");
        
        try {
            // ... algo
        } catch (Exception e) {
            log.error("Ocorreu um erro grave: {}", e.getMessage());
        }
    }
}
```

---

## 2. Níveis de Log
- `log.error()`: Para erros que impedem o funcionamento.
- `log.warn()`: Para avisos (algo correu mal mas a app continua).
- `log.info()`: Mensagens informativas (padrão).
- `log.debug()`: Detalhes técnicos para desenvolvimento.

---

## 3. Ligar e Desligar Logs (Sem mexer no código)
Podes mudar o que vês no terminal alterando apenas um ficheiro de configuração.

1. Abre: `src/main/resources/application.properties`.
2. Altera a linha:
   ```properties
   logging.level.co.ao.base=INFO
   ```

### Valores possíveis:
| Nível | Descrição |
| :--- | :--- |
| **`OFF`** | Desliga todos os logs do projeto. Limpa o terminal. |
| **`ERROR`** | Mostra apenas quando algo explode. |
| **`INFO`** | (Recomendado) Mostra o fluxo normal e chamadas da API. |
| **`DEBUG`** | Mostra tudo (payloads das APIs, detalhes técnicos). |

---

## 4. Vantagem do Hot-Reload
Sempre que alterares o nível de log no ficheiro `application.properties` e gravares, a aplicação reinicia sozinha e aplica a mudança em segundos. Não precisas de parar e começar de novo.
