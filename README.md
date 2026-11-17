# Grădina mea 🌱

Aplicație Android pentru îngrijirea plantelor din casă: ții evidența plantelor și vezi când trebuie udată fiecare.

Proiect pentru cursul *Informatique Mobile et Embarquée*, etapa 1.

## Funcționalități (etapa 1)

- **Lista plantelor**: card pentru fiecare plantă, cu starea udării (întârziată, azi, peste N zile)
- **Detalii plantă**: camera, intervalul de udare, ultima și următoarea udare, notițe, butonul „Am udat-o azi”
- **Adaugă / editează plantă**: formular cu validare, alegerea camerei, calendar pentru ultima udare
- **Ștergere** cu dialog de confirmare
- Datele sunt salvate local (Room) și rămân după repornirea aplicației

## Tehnologii

Kotlin · Jetpack Compose + Material 3 · Navigation Compose · Room · ViewModel + StateFlow (MVVM) · minSdk 26

## Structura

```
app/src/main/java/ro/gradinamea/
├── data/          Plant (entitate), PlantDao, PlantDatabase, PlantRepository, date de test
├── navigation/    AppNavGraph: rutele list, detail/{plantId}, edit?plantId={plantId}
├── ui/
│   ├── list/      PlantListScreen + PlantListViewModel
│   ├── detail/    PlantDetailScreen + PlantDetailViewModel
│   ├── edit/      PlantEditScreen + PlantEditViewModel
│   ├── components/ elemente comune (avatar, eticheta de udare, formatare)
│   └── theme/     culori și temă Material 3
├── GradinaMeaApplication.kt
└── MainActivity.kt
```

Fluxul datelor: UI (Compose) → ViewModel → Repository → Room (DAO) și înapoi prin `Flow`.

## Rulare

1. Deschide folderul în Android Studio (File → Open).
2. Așteaptă sincronizarea Gradle.
3. Rulează pe un emulator cu Android 8.0 (API 26) sau mai nou.

La prima pornire baza de date se completează cu 6 plante de test.

## Etapa 2 (plan)

Vremea (Open-Meteo), catalog de specii (Perenual), camera, senzorul de lumină, notificări de udare.
