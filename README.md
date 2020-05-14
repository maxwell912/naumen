PhoneBookAPI


Для запуска выполнить команду "sbt run" в корневой папке.

Возможные запросы:
  GET     /                                 Начальная html страница.
  GET     /phones                           Возвращает json со всеми записями.
  GET     /phones/:id                       Возвращает json с выбранной записью.
  GET     /phones/filterByName/:substring   Возвращает записи с $substring в имени.
  GET     /phones/filterByNumber/:substring Возвращает записи с $substring в номере.

  POST    /phones/add                       Получает на вход json с name и phoneNumber и добавляет его в базу.

  PUT     /phones/update/:id                Изменят name и phoneNumber выбранно1 записи.

  DELETE  /phones/delete/:id                Удаляет выбранную запись.
