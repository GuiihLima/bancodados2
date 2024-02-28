# Iniciando a aplicação Ubuntu e Postgres no Docker

1. Primeiro, certifique-se de que o Docker e o Docker Compose estão instalados em sua máquina. Se não estiverem, você pode baixá-los e instalá-los a partir dos seguintes links:
    - [Docker](https://www.docker.com/products/docker-desktop)
    - [Docker Compose](https://docs.docker.com/compose/install/)

2. Abra um terminal ou prompt de comando.

3. Navegue até o diretório onde o arquivo `docker-compose.yml` está localizado usando o comando `cd`. Por exemplo:
    ```
    cd /E:/Workspace/git/guiih/banco de dados 2/
    ```

4. Uma vez no diretório correto, execute o seguinte comando para iniciar a aplicação:
    ```
    docker-compose up --build
    ```

5. Se você quiser parar a aplicação, use o seguinte comando:
    ```
    docker-compose down
    ```

- #### Lembre-se de substituir o caminho do diretório no passo 3 pelo caminho onde o seu arquivo `docker-compose.yml` está localizado.

6. Após iniciado a aplicação (conteiner) do ubuntu e do postgres, você pode acessar o terminal do ubuntu utilizando o comando:
    ```
    docker exec -it ubuntu bash
    ```

7. Se você quiser sair do terminal ubuntu, use o seguinte comando:
    ```
    exit
    ```

- #### Agora que está no terminal ubuntu, poderá usar quaisquer comandos básicos linux. Porém alguns comandos podem não funcionar pois a imagem ubuntu executada no Docker é uma versão menor e com menos recursos habilitados.

# Adicionando mais ferramentas para usar no Ubuntu

1. Para adicionar mais ferramentas para serem utilizadas no Ubuntu, você deve navegar até o arquivo `dockerfile`
    - Há uma seção de comandos nesse arquivo parecidas com o seguinte comando:
    ```
    RUN apt-get install -y openjdk-8-jdk && \
        apt-get install -y inetutils-ping && \
        apt-get install -y wget && \
        apt-get install -y man-db
    ```
    - Para instalar mais ferramentas no Ubuntu, você precisa adicionar `&& \` ao final de cada linha, exceto a última, e repetir o comando `apt-get install -y nome_da_ferramenta`