# சிலை நல அறக்கட்டளை (Statue Welfare Trust) - Android App

அதிகாரப்பூர்வ உத்தியோகபூர்வ உறுப்பினர் பதிவு மற்றும் நிர்வாக செயலி மொபைல் பயன்பாடு.
Official membership registration and administration mobile application.

---

## 🎨 Theme Details
- **முதன்மை வண்ணம் (Primary Color):** உன்னத பச்சை (Deep Emerald Green - `#0F5B36`) representing nature, tree plantation, and life.
- **துணை வண்ணம் (Secondary Color):** தங்க நிறம் (Metallic Gold - `#D4AF37`) representing heritage preservation, culture, and statues.
- **பின்னணி வண்ணம் (Background Color):** உன்னத வெள்ளை (Mint White - `#F9FDFB`) / அடர் பச்சை காடு (Dark Forest Green - `#06180E`).

---

## 📱 Features

### 1. Home Page / முகப்பு பக்கம்
- **Trust Custom Logo:** Artistically drawn Vector Monument Silhouette surrounded by majestic sunburst ray paths representing protection, cultural legacy, and tree preservation.
- **Trust Name:** Displays "சிலை நல அறக்கட்டளை (Statue Welfare Trust)" with official registration info.
- **Welcome Message:** Elegant card outlining trust activities (Social Service, Tree Plantation, Education Support, and Public Welfare).

### 2. About Trust / நற்பணி விவரங்கள்
- Displays the official details comprehensively:
  - **Registration Number:** `BK4/23/2025`
  - **Darpan ID:** `TN/2025/0779121`
  - **PAN:** `ABLTS8950M`
  - **12A Registration ARN:** `935383490251225`
- **Interactive Actions:** Direct action buttons to initiate call dialing (`+91 9342511821`) and mailing (`Krishkavathi@gmail.com`).

### 3. Membership Registration Form / உறுப்பினர் பதிவுப் படிவம்
A fully validated, clean registration form with keyboard support and auto-recommendation chips of major districts in Tamil Nadu:
- Name (முழு பெயர்)
- Father/Husband Name (தந்தை / கணவர் பெயர்)
- Age (வயது)
- Mobile Number (கைபேசி எண்)
- Address (முகவரி)
- District (மாவட்டம்)
- Occupation (தொழில்)
- **Local SQLite Persistence:** Fully written using modern **Android Jetpack Room Database** which writes directly into an enterprise-grade SQLite relational database.

### 4. Admin List / நிர்வாகப் பதிவுகள்
- Secure visual dashboard for administrators showing the list of all members currently registered.
- Interactive Search Bar: Quickly query members based on their Tamil Name, Mobile, or District.
- Pull-to-delete member interactions and swift count indicators displaying actual statistics in real-time.

---

## 🛠️ Project Structure
- `MainActivity.kt`: Contains the core Compose navigation framework, layouts, UI components, interactive states, and beautiful Material Design 3 theme integrations.
- `data/`:
  - `Member.kt`: The Room Database Entity definition.
  - `MemberDao.kt`: Data Access Object defining SQLite queries.
  - `AppDatabase.kt`: Singleton builder for SQLite database integration.
  - `MemberRepository.kt`: Clean architecture repository handling queries.
- `viewmodel/`:
  - `MemberViewModel.kt`: MVVM pattern state manager driving reactive flow streams and Event streams.
- `res/`: Modern adaptive icons with green and gold themes.

---

## 📥 How to export to Android Studio ZIP?
You can easily download and run this complete Android Studio project on your local machine:
1. Open the **Settings or Export menu** on the top-right corner of the **Google AI Studio Build** sidebar.
2. Select **"Export Project as ZIP"** (or similar download button).
3. Extract the downloaded ZIP file.
4. Launch **Android Studio (Koala/Ladybug or newer)**, select **"Open"** and choose the extracted folder.
5. Gradle will synchronize automatically and build the project flawlessly.
