# Microsserviço de Administração do Catálogo de Vídeos - Parte V

O microsserviço de Administração do Catálogo de Vídeos é a aplicação _backend_ responsável por gerenciar os vídeos, incluindo as categorias, os gêneros e os membros do elenco.

Dentro da dinâmica do sistema:

1. A aplicação _Backend Admin_ do Catálogo de Vídeos vai falar com o banco de dados, salvar os dados dos vídeos, dos gêneros, das categorias e membros do elenco;
2. A aplicação _Frontend Admin_ do Catálogo de Vídeos vai falar com a _API_ do _backend_ para realizar as ações de cadastro;
3. A aplicação _Encoder_ de Vídeos (_Golang_) vai acessar os vídeos que forem enviados via _Backend_ de Administração de Vídeos, fazer o _encoding_ e salvar os dados em um _bucket_ no _Google Cloud Storage_. Na seqüência, uma notificação é enviada via _RabbitMQ_ para a aplicação Admin do Catálogo de Vídeos atualizar o _status_ de processamento dos vídeos em sua base de dados.

Esta quinta parte contempla o desenvolvimento do _pipeline_ de _CI/CD_, autenticação com _Keycloak_ e observabilidade com _Elastic Stack_.

Estão envolvidas, nesta aplicação, tecnologias de:

- Backend

  - Java (JDK 17)
  - Spring Boot 3
  - Gradle (gerenciador de dependências)
  - Spring Data & JPA
  - MySQL
  - Flyway (gerenciamento do banco de dados)
  - RabbitMQ (sistema de mensageria)
  - GitHub Actions (CI/CD)
  - Keycloak (autenticação)
  - Elastic Stack (Elasticsearch/Logstash/Kibana & Filebeat) (observabilidade)
  - H2 (testes integrados de persistência)
  - JUnit Jupiter (testes unitários)
  - Mockito JUnit Jupiter (testes integrados)
  - Testcontainers MySQL (testes end-to-end)
  - Springdoc-openapi (documentação da API)
