# Тестовое задание для Ростелеком-Солар  
### Получение статистики с серверов StackOverflow 


## Требования

- Java8  
- Clojure / Clj / RlWrap  
- Make  
- Curl  


## Использование

```sh
make build
make run_server
```

```curl -X GET url``` или ```make test_req```

Чтобы убить процесс, используйте 
```sh 
make kill_server
```  

## Ожидаемый вывод

```json
{
  "clojure" : {
    "total" : 113,
    "answered" : 137
  },
  "scala" : {
    "total" : 138,
    "answered" : 42
  }
```