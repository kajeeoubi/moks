# MOKS

Aplikasi Android e-commerce sederhana berbasis Java dengan Gradle Kotlin DSL.

## Ringkasan

Project ini menyediakan alur pengguna dari onboarding, login/register, melihat produk, menambahkan ke keranjang, checkout, hingga melihat riwayat pesanan.

## Fitur Utama

- Splash screen dan onboarding (Lottie)
- Login, register, dan mode tamu
- Navigasi utama dengan Bottom Navigation
- Daftar produk dan detail produk
- Keranjang belanja dengan badge jumlah item
- Checkout dan riwayat/detail pesanan
- Profil pengguna dan edit data
- Integrasi API menggunakan Retrofit

## Teknologi yang Digunakan

- Bahasa: Java
- Build system: Gradle (Kotlin DSL)
- Min SDK: 26
- Target SDK: 34
- AndroidX Navigation
- ViewBinding
- Retrofit + Gson Converter
- Glide
- Lottie
- Firebase Firestore

## Struktur Direktori Penting

```text
app/src/main/java/com/example/uts_mobile_02995/
|- api/            # Layer API (Retrofit interface dan konfigurasi)
|- data/           # Model data
|- ui/             # Fragment, adapter, dan komponen per fitur
|- utils/          # Utility/helper
|- MainActivity.java
|- LoginActivity.java
|- RegisterActivity.java
|- SplashActivity.java
```

## Cara Menjalankan

1. Clone repository:

   ```bash
   git clone https://github.com/kajeeoubi/moks.git
   cd moks
   ```

2. Buka project di Android Studio.

3. Sinkronisasi Gradle (Sync Project with Gradle Files).

4. Pastikan koneksi internet aktif, karena aplikasi mengambil data dari API.

5. Jalankan aplikasi ke emulator/perangkat Android (API 26+).

## Konfigurasi API

Base URL saat ini berada di:

`app/src/main/java/com/example/uts_mobile_02995/api/ServerAPI.java`

```java
public static final String BASE_URL = "https://example.com/";
```

Jika backend berubah, sesuaikan nilai `BASE_URL` pada file tersebut.

## Catatan

- File lokal seperti `local.properties`, hasil build, dan cache Gradle sudah diabaikan via `.gitignore`.
- Pada perangkat Android versi baru, izin media/kamera tetap mengikuti kebijakan runtime permission.

## Lisensi

Project ini dibuat untuk kebutuhan pembelajaran/perkuliahan.