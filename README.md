# Мессенджер

Реализация совместима с Глебом Валиным.

## Протокол

Реализация протокола довольно проста:

0. Протокол основан на gRPC на единственном вызове `chat`.
0. Оба пира пишут друг другу сообщения, первым ожидается информация о противоположной стороне.
0. Чат закрывается просто закрытием потока одной из сторон.

## Реализация

Базовый пакет — `net.ldvsoft.spbau.messenger`.

### Протокол

В отдельном подпакете `protocol` реализованы все примитивы протокола, их запись и чтение.
Состоит из:

0. `Protocol` — класс, читающий и записывающий в соединение сообщения.
0. `Connection` — интерфейс, используемый в реализации протокола как обёртка, через которую происходит создание инфраструктуры gRPC.
0. `PeerInfo`, `TextMessage`, `StartedTyping` — структуры, хранящие, собственно, тела сообщений.

### Логика

Вся логика мессенджера умещается, в силу простоты, в класс `Messenger`.
Он, храня экземляр `Protocol`, предоставляет методы для отправки сообщений, а также запускает поток, читающий входящие сообщения,
и передающий их в интерфейс `Messenger.Listener`.

Отдельно есть класс-фабрика `Starter`, запускающая два вида сетевых соединений — клиентскую и серверную сторону, соотвественно.

### GUI

Пользовательский интерфейс на Swing реализован в подпакете `gui`.
Там представлены классы `Main` — точка входа, `StartFrame` — стартовое окно выбора подключения и `ChatDialog` — открываемое окно с беседой.

### CLI

В тестовых целях, перед разработкой GUI был реализован крайне простой CLI-интерфейс, его реализация в классе `cli.Main`.
