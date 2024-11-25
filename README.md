<h1 align="center">Projeto Final POO</h1>

## Equipe
```
Nicolas Gustavo
Leonardo Morari
Carlos Henrique
Jesua Isaque
Jonas Soares
```

Aplicação MVC integrada à API do Spotify para inserção de artistas feita com base [neste repositório](https://github.com/maromo71/projetofinalpoo) referente ao trabalho final da disciplina de Programação Orientada a Objetos.

## Ferramentas Utilizadas
- [Java 17](https://docs.oracle.com/en/java/)
- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Doc Open API 3](https://springdoc.org)
- [H2 Database](https://www.h2database.com/html/main.html)
- [Lombok](https://projectlombok.org)
- [VSCode](https://code.visualstudio.com/)

# Como Executar
## Pré Requisitos
- Linux ou Mac: Instale o git via terminal | Windows: Instale o git via instalador | [Tutorial](https://git-scm.com/)
- Java instalado e configurado na versão 17
- Para Docker, certifique-se de que tenha o docker previamente instalado e configurado

## Localmente via Maven e JAR

1 - Clone o repositório
```
git clone https://github.com/nicolasgmp/Spotify_Projeto_POO.git
```

2 - Vá até a pasta principal do projeto
```
cd ./Spotify_Projeto_POO
```

3 - Builde o projeto
```
./mvnw clean package
```

4 - Rode
```
java -jar target/bands-api-0.0.1-SNAPSHOT.jar
```

## Localmente via Spring Boot

1 - Clone o repositório
```
git clone https://github.com/nicolasgmp/Spotify_Projeto_POO.git
```

2 - Vá até a pasta principal do projeto
```
cd ./Spotify_Projeto_POO
```

3 - Rode
```
mvn spring-boot:run
```

## Usando Docker

1 - Clone o repositório
```
git clone https://github.com/nicolasgmp/Spotify_Projeto_POO.git
```

2 - Builde a imagem
```
docker build -t nome-imagem . (Não esqueça deste ponto)
```

3 - Rode o container 
```
docker run --name nome -p 8080:8080 -d nome-imagem
```

A api estará acessível para todos os casos em localhost:8080/api/v1/users/login

A documentação das rotas pode ser vista em [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

# Telas da aplicação
