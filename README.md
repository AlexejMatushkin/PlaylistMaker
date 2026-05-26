# 🎵 Playlist Maker

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)

## 📱 О проекте

**Playlist Maker** — учебное Android-приложение для поиска музыкальных треков, прослушивания отрывков, создания плейлистов и управления избранным.  
Проект демонстрирует современный подход к разработке под Android с использованием Clean Architecture, MVVM, корутин, Flow, Room и Jetpack Navigation.

## ✨ Возможности

- **Поиск треков** через iTunes API с **debounce** (корутины)
- **Воспроизведение 30-секундных отрывков** с обновлением прогресса (корутины вместо Handler)
- **Медиатека** с двумя вкладками:
    - *Избранные треки*
    - *Плейлисты* (заглушка, готово к расширению)
- **Настройки** — переключение светлой/тёмной темы с сохранением состояния
- **Нижняя навигация** (BottomNavigationView) с переключением между экранами «Медиатека», «Поиск», «Настройки»
- **Адаптивный интерфейс** — поддержка тёмной темы

## 🚀 Запуск проекта

1. Клонируйте репозиторий:
```bash
git clone https://github.com/AlexejMatushkin/PlaylistMaker.git
```
Откройте проект в Android Studio

Запустите на эмуляторе или устройстве  Android

## 🛠 Технологии и архитектура

- **Clean Architecture** (слои Data, Domain, Presentation)
- **MVVM** (ViewModel, LiveData/StateFlow)
- **Kotlin Coroutines + Flow** (асинхронность, debounce, прогресс плеера)
- **Room** (хранение избранных треков)
- **Retrofit** + **OkHttp** (сетевые запросы к iTunes API)
- **Koin** (Dependency Injection)
- **Jetpack Navigation Component** (NavController, безопасная навигация между фрагментами)
- **Glide** (загрузка обложек)

---

<div style="text-align: center;">

### 🎓 Учебный проект
**Практикум • 2025**

</div>
