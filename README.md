# Loan Calculator

## Tecnologias utilizadas:
[![Icons](https://skillicons.dev/icons?i=react,ts,javascript,java,spring,docker&theme=light)](https://skillicons.dev)

## Descrição
Descrição do Projeto
Este projeto é uma API de cálculo de empréstimos, desenvolvida em React(TS) e Java com o framework Spring Boot.
A sua principal funcionalidade é simular e detalhar as prestações de um empréstimo, considerando diversas variáveis como valor, taxa de juros e datas.

A solução oferece um endpoint único e robusto que, ao receber os dados de um empréstimo, processa a lógica de cálculo complexa e retorna uma lista detalhada de prestações. Essa lista inclui informações cruciais para a análise financeira, como:
- Data de referência da prestação
-  devedor atualizado
- Valor consolidado da prestação (ex: "1/12")
- Total pago na prestação
- Valores de amortização, provisão, acúmulo e juros pagos

Para garantir a confiabilidade e a qualidade do código, o projeto utiliza um conjunto de testes unitários e de integração com as bibliotecas JUnit, Mockito e Instancio. Essa abordagem assegura que todas as regras de negócio e os caminhos de código sejam validados, proporcionando uma alta cobertura de testes e maior segurança nas operações.

A arquitetura do projeto é dividida em camadas, com o uso de records para DTOs (Data Transfer Objects), o que simplifica a imutabilidade dos dados e melhora a clareza do código.

---

## Configuração e Execução

### Com Docker Compose

Se você tiver o Docker Compose instalado, siga os passos abaixo para executar o projeto:

1. Execute o seguinte comando para iniciar os containers do backend e frontend:
   ```bash
   docker-compose up
   ```
2. Acesse a aplicação pelo seu navegador, que estará acessível em http://localhost:3000.

---

### Sem Docker Compose (Execução Manual)
Se preferir executar o projeto localmente, sem Docker Compose, siga os passos abaixo:

#### 1. Pré-requisitos:
- **NodeJs 20** (ou compatível)
- **Jdk 21** (ou compatível)

#### 2. Instalação das dependências
   ```bash
   cd frontend; npm install; cd ..
   ```

#### 3. Execução do backend:
- A partir da raiz do projeto:

Faça build do projeto com:
   ```bash
   cd backend; ./gradlew bootJar --no-daemon -x test; cd ..
   ```
Execute a aplicação com:
   ```bash
   java -jar .\backend\build\libs\backend-0.0.1-SNAPSHOT.jar
   ```

#### 4. Execução do frontend:
- A partir da raiz do projeto:
   ```bash
   cd frontend; npm start;
   ```

---

## Contribuição

Fique à vontade para contribuir com este projeto. Você pode enviar pull requests para correções de bugs, melhorias e novas funcionalidades.

